/**
 * 
 */
package de.urszeidler.eclipse.solidity.compiler.support.builder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IBuildConfiguration;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import de.urszeidler.eclipse.solidity.compiler.support.Activator;
import de.urszeidler.eclipse.solidity.compiler.support.preferences.PreferenceConstants;
import de.urszeidler.eclipse.solidity.compiler.support.util.StartCompiler;

/**
 * @author uzeidler
 *
 */
public class IncrementalProjectBuilder extends org.eclipse.core.resources.IncrementalProjectBuilder {

	private class SolidityBuildVisitor implements IResourceVisitor {

		@Override
		public boolean visit(IResource resource) throws CoreException {
			if (resource!=null && resource.getFileExtension()!=null && resource.getFileExtension().equals("sol")) {
				IncrementalProjectBuilder.this.src.add(resource.getRawLocation().toOSString());//  .getAbsolutePath());
			}
			return true;
		}

	}

	private class SolidityDeltaBuildVisitor implements IResourceDeltaVisitor {

		@Override
		public boolean visit(IResourceDelta delta) throws CoreException {
			// TODO Automatisch generierter Methodenstub
			return false;
		}

	}

	private List<String> src= new ArrayList<String>();

	/**
	 * 
	 */
	public IncrementalProjectBuilder() {
		super();
	}

	/*
	 * (none-Javadoc)
	 * 
	 * @see org.eclipse.core.resources.IncrementalProjectBuilder#build(int,
	 * java.util.Map, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected IProject[] build(int kind, Map<String, String> args, IProgressMonitor monitor) throws CoreException {
		if (kind == org.eclipse.core.resources.IncrementalProjectBuilder.FULL_BUILD) {
			fullBuild(monitor);
		} else if (kind == org.eclipse.core.resources.IncrementalProjectBuilder.CLEAN_BUILD) {
			cleanBuild(monitor);
		} else {
			IResourceDelta delta = getDelta(getProject());
			if (delta == null) {
				fullBuild(monitor);
			} else {
				incrementalBuild(delta, monitor);
			}
		}
		return null;
	}

	private void cleanBuild(IProgressMonitor monitor) {
		// TODO Automatisch generierter Methodenstub

	}

	private void incrementalBuild(IResourceDelta delta, IProgressMonitor monitor) {
		try {
			delta.accept(new SolidityDeltaBuildVisitor());
		} catch (CoreException e) {
			Activator.logError("Error while full build.", e);
		}
	}

	private void fullBuild(IProgressMonitor monitor) {
		monitor.beginTask("build: "+getProject().getName(), 1);
		src.clear();
		try {
			getProject().accept(new SolidityBuildVisitor());

			IPreferenceStore store = getPreferences();
			String command = store.getString(PreferenceConstants.COMPILER_PROGRAMM);

			String compile_folder = de.urszeidler.eclipse.solidity.compiler.support.Activator.getDefault()
					.getPreferenceStore().getString(
							de.urszeidler.eclipse.solidity.compiler.support.preferences.PreferenceConstants.COMPILER_TARGET);
			IContainer target = getProject().getFolder(compile_folder);
			if (!target.getLocation().toFile().exists()) {
				target.getLocation().toFile().mkdirs();
				//target.setDerived(true, monitor);
			}

			File outFile = target.getLocation().toFile();
			StartCompiler.startCompiler(outFile, src, store, command);
			monitor.done();
		} catch (CoreException e) {
			Activator.logError("Error while full build.", e);
		}
	}

	private void createBuildConfig(String projectName, String configName) {
		IBuildConfiguration newBuildConfig = ResourcesPlugin.getWorkspace().newBuildConfig(projectName, configName);
		// newBuildConfig.

	}

	private IPreferenceStore getPreferences() {
		IPreferenceStore store = new ScopedPreferenceStore(new ProjectScope(getProject()), Activator.PLUGIN_ID);
		if (!store.getBoolean(PreferenceConstants.COMPILE_CONTRACTS_PROJECT_SETTINGS))
			store = new ScopedPreferenceStore(InstanceScope.INSTANCE, Activator.PLUGIN_ID);

		return store;
	}
}
