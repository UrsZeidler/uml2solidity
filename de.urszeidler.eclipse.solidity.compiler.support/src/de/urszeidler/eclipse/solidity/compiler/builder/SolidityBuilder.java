/**
 * 
 */
package de.urszeidler.eclipse.solidity.compiler.builder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.preference.IPreferenceStore;

import de.urszeidler.eclipse.solidity.compiler.support.Activator;
import de.urszeidler.eclipse.solidity.compiler.support.preferences.PreferenceConstants;
import de.urszeidler.eclipse.solidity.compiler.support.util.StartCompiler;
import de.urszeidler.eclipse.solidity.compiler.support.util.StartCompiler.CompilerCallback;

/**
 * @author urs
 *
 */
public class SolidityBuilder extends IncrementalProjectBuilder {

	private final class IResourceVisitorImplementation implements IResourceVisitor {
		private final List<String> files;

		private IResourceVisitorImplementation(List<String> files) {
			this.files = files;
		}

		@Override
		public boolean visit(IResource resource) throws CoreException {
			if (resource.getType() == IResource.FILE && "sol".equals(resource.getFileExtension())) {
				files.add(resource.getRawLocation().toString());
			}
			return true;
		}
	}

	public static final String BUILDER_ID = "de.urszeidler.eclipse.solidity.compiler.support.SolidityCompilerBuilder";
	public static final String SOLIDITY_MARKER_ID = "de.urszeidler.eclipse.solidity.compiler.solidityCodeMarker";

	public static final String ERROR_PARSER = "^([^:]*):(\\p{Digit}*):(\\p{Digit}*):(.*):(.*)";
	public static final String MESSAGE_SPLITTER = "\\^(-*)\\^";

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.resources.IncrementalProjectBuilder#build(int,
	 * java.util.Map, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@SuppressWarnings("rawtypes")
	protected IProject[] build(int kind, Map args, IProgressMonitor monitor) {
		try {
			if (kind == IncrementalProjectBuilder.FULL_BUILD) {
				fullBuild(monitor);
			} else {
				IResourceDelta delta = getDelta(getProject());
				if (delta != null) {
					final AtomicBoolean ab = new AtomicBoolean(false);
					delta.accept(new IResourceDeltaVisitor() {
						@Override
						public boolean visit(IResourceDelta delta) throws CoreException {
							if (delta.getResource().getType() == IResource.FILE
									&& "sol".equals(delta.getResource().getFileExtension())) {
								ab.set(true);
								return false;
							}
							return true;
						}
					});
					if (ab.get())
						fullBuild(monitor);
				}
			}
		} catch (CoreException e) {
			Activator.logError("Error building " + getProject().getName(), e);
		}
		return null;
	}

	/**
	 * Do a full build.
	 * 
	 * @param monitor
	 * @throws CoreException
	 */
	private void fullBuild(IProgressMonitor monitor) throws CoreException {
		IPreferenceStore store = PreferenceConstants.getPreferenceStore(getProject());
		String src = store.getString(PreferenceConstants.SOL_SRC_DIRECTORY);
		if (src == null)
			return;

		String command = store.getString(PreferenceConstants.COMPILER_PROGRAMM);
		if (command == null || command.isEmpty())
			return;
		File file = new File(command);
		if (!file.exists() || !file.canExecute())
			return;

		IContainer folder;
		if (src.startsWith("/")) {
			folder = (IContainer) ResourcesPlugin.getWorkspace().getRoot().findMember(new Path(src));
		} else
			folder = getProject().getFolder(src);

		if (folder == null)
			return;

		if (!folder.exists())
			folder.getLocation().toFile().mkdirs();

		String filename = store.getString(PreferenceConstants.COMPILER_TARGET_COMBINE_ABI);
		final Path fileToWritePath = new Path(filename);
		IPath removeLastSegments = fileToWritePath.removeLastSegments(1);

		final IFolder outPath = (IFolder) (filename.startsWith("/")
				? ResourcesPlugin.getWorkspace().getRoot().findMember(removeLastSegments)
				: getProject().findMember(removeLastSegments));

		if (outPath == null)
			return;

		final IFile outFile = outPath.getFile(fileToWritePath.lastSegment());
		if (outFile == null)
			return;
		final List<String> files = new ArrayList<>();
		if (outFile.exists()) {
			final long localTimeStamp = outFile.getLocalTimeStamp();

			final AtomicBoolean ab = new AtomicBoolean(true);
			folder.accept(new IResourceVisitor() {

				@Override
				public boolean visit(IResource resource) throws CoreException {
					if (resource.getType() == IResource.FILE && "sol".equals(resource.getFileExtension())) {
						files.add(resource.getRawLocation().toString());
						if (resource.getLocalTimeStamp() > localTimeStamp)
							ab.set(false);
					}
					return true;
				}
			});
			if (ab.get())
				return;
		} else {
			folder.accept(new IResourceVisitorImplementation(files));
		}
		if (files.isEmpty())
			return;

		if (monitor != null)
			monitor.subTask("compile solidity code:" + files);

		if (!deleteCompilerMarkers(getProject()))
			return;

		boolean errors = executeCompileSource(store, command, outFile, files, new ArrayList<String>());
		if (!errors && store.getBoolean(PreferenceConstants.ESTIMATE_GAS_COSTS)) {
			executeEstimateGas(monitor, store, outPath, files, new ArrayList<String>());
		}

		if (monitor != null)
			monitor.subTask("code compiled");
		if (monitor != null)
			monitor.done();
	}

	/**
	 * Compiles the sources.
	 * 
	 * @param store
	 * @param command
	 * @param outFile
	 * @param files
	 * @param options
	 * @return
	 */
	private boolean executeCompileSource(IPreferenceStore store, String command, final IFile outFile,
			final List<String> files, final List<String> options) {
		options.add(command);
		options.add("--combined-json");
		options.add(store.getString(PreferenceConstants.COMBINED_JSON_OPTIONS));

		if (store.getBoolean(PreferenceConstants.ENABLE_GAS_OPTIMIZE))
			options.add("--optimize");

		final AtomicBoolean errors = new AtomicBoolean(false);
		StartCompiler.startCompiler(files, new CompilerCallback() {

			@Override
			public void compiled(String input, String error, Exception exception) {
				if (exception == null && (input != null && !input.isEmpty())) {
					try {
						File file = outFile.getRawLocation().toFile();
						if (!file.exists())
							file.createNewFile();
						FileWriter fileWriter;
						fileWriter = new FileWriter(file);
						fileWriter.write(input);
						fileWriter.close();
					} catch (IOException e) {
						Activator.logError("Error ", e);
					}
				} else {
					createMarker(error);
					errors.set(true);
				}
			}
		}, options);
		return errors.get();
	}

	/**
	 * Delete all the solidity compile markers for the project.
	 * 
	 * @param project
	 * @return
	 */
	private boolean deleteCompilerMarkers(IProject project) {
		try {
			project.deleteMarkers(SOLIDITY_MARKER_ID, false, IResource.DEPTH_INFINITE);
			return true;
		} catch (CoreException e) {
			Activator.logError("Error cleaning Markers.", e);
		}
		return false;
	}

	/**
	 * Create the marker from the compiler output.
	 * 
	 * @param error
	 */
	private void createMarker(String error) {
		String[] errors = error.trim().split(MESSAGE_SPLITTER);//error.trim().split("\\^");//
		Pattern pattern = Pattern.compile(ERROR_PARSER, Pattern.DOTALL | Pattern.MULTILINE);
		IPath location = getProject().getLocation();

		for (String errorMessage : errors) {
			Matcher matcher = pattern.matcher(errorMessage.trim());
			if (!matcher.matches())
				continue;

			String filename = matcher.group(1).trim();
			String lineNumber = matcher.group(2);
			String errorType = matcher.group(4).trim();
			String errorM = matcher.group(5).trim();

			Path path = new Path(filename);
			if (location.isPrefixOf(path)) {
				IPath filePath = path.removeFirstSegments(location.segmentCount());
				IResource resource = getProject().findMember(filePath);
				if (!resource.exists())
					continue;

				try {
					int lineN = Integer.parseInt(lineNumber);
					IMarker marker = resource.createMarker(SOLIDITY_MARKER_ID);
					Map<String, Object> attributes = new HashMap<String, Object>();

					attributes.put(IMarker.MESSAGE, errorM);
					attributes.put(IMarker.LINE_NUMBER, lineN);
					if ("Error".equalsIgnoreCase(errorType))
						attributes.put(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
					else if ("Warning".equalsIgnoreCase(errorType))
						attributes.put(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
					else
						attributes.put(IMarker.SEVERITY, IMarker.SEVERITY_INFO);

					marker.setAttributes(attributes);
				} catch (Exception e) {
					Activator.logError("Error creating marker for: " + filename, e);
				}
			}
		}
	}

	/**
	 * Estimate the gas cost.
	 * 
	 * @param monitor
	 * @param store
	 * @param outPath
	 * @param files
	 * @param options
	 */
	private void executeEstimateGas(IProgressMonitor monitor, IPreferenceStore store, final IFolder outPath,
			final List<String> files, final List<String> options) {
		options.add(store.getString(PreferenceConstants.COMPILER_PROGRAMM));
		if (store.getBoolean(PreferenceConstants.ENABLE_GAS_OPTIMIZE))
			options.add("--optimize");

		options.add("--gas");
		if (monitor != null)
			monitor.subTask("estimate gas costs:" + files);

		final IFile ofile = outPath.getFile("gas-costs.txt");
		StartCompiler.startCompiler(files, new CompilerCallback() {

			@Override
			public void compiled(String input, String error, Exception exception) {
				if (exception == null && (input != null && !input.isEmpty())) {
					try {
						File file = ofile.getRawLocation().toFile();
						if (!file.exists())
							file.createNewFile();
						FileWriter fileWriter;
						fileWriter = new FileWriter(file);
						fileWriter.write(input);
						fileWriter.close();
					} catch (IOException e) {
						Activator.logError("Error ", e);
					}
				} else
					Activator.logError(error + "  " + options, exception);
			}
		}, options);
	}
}
