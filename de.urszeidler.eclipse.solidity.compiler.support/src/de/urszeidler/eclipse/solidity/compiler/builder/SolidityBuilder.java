/**
 * 
 */
package de.urszeidler.eclipse.solidity.compiler.builder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
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

	@SuppressWarnings("rawtypes")
	protected IProject[] build(int kind, Map args, IProgressMonitor monitor) {
		try {
			if (kind == IncrementalProjectBuilder.FULL_BUILD) {
				fullBuild(monitor);
			} else {
				IResourceDelta delta = getDelta(getProject());
				if (delta == null) {
					// fullBuild(monitor);
				} else {
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

	private void fullBuild(IProgressMonitor monitor) throws CoreException {
		IPreferenceStore store = PreferenceConstants.getPreferenceStore(getProject());
		String src = store.getString(PreferenceConstants.SOL_SRC_DIRECTORY);
		if (src == null)
			return;
		IContainer folder;
		if (src.startsWith("/")) {
			folder = (IContainer) ResourcesPlugin.getWorkspace().getRoot().findMember(new Path(src));
		} else
			folder = getProject().getFolder(src);

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

		final List<String> options = new ArrayList<String>();
		String command = store.getString(PreferenceConstants.COMPILER_PROGRAMM);
		if (command == null || command.isEmpty())
			return;

		options.add(command);
		options.add("--combined-json");
		options.add("abi,bin");

		if (store.getBoolean(PreferenceConstants.ENABLE_GAS_OPTIMIZE))
			options.add("--optimize");

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
				} else
					Activator.logError(error + "  " + options, exception);
			}
		}, options);

		if (store.getBoolean(PreferenceConstants.ESTIMATE_GAS_COSTS)) {
			options.clear();
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

		if (monitor != null)
			monitor.subTask("code compiled");
		if (monitor != null)
			monitor.done();
	}

}
