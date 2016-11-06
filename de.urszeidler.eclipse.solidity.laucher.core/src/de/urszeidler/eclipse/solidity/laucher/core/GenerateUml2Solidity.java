/**
 * 
 */
package de.urszeidler.eclipse.solidity.laucher.core;

import java.io.IOException;
import java.util.ArrayList;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStreamsProxy;
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.preference.PreferenceStore;

import de.urszeidler.eclipse.solidity.ui.common.GenerateAll;
import de.urszeidler.eclipse.solidity.util.Uml2Service;

/**
 * @author urs
 *
 */
public class GenerateUml2Solidity extends LaunchConfigurationDelegate {

	private final class IProcessImplementation implements IProcess {
		private ILaunch launch;
		private boolean terminated = false;
		int exitValue = 0;
		String label;

		public IProcessImplementation(ILaunch launch) {
			this.launch = launch;
		}

		@Override
		public void terminate() throws DebugException {
			
		}

		@Override
		public boolean isTerminated() {
			return terminated;
		}

		@Override
		public boolean canTerminate() {
			return false;
		}

		@Override
		public <T> T getAdapter(Class<T> adapter) {
			return null;
		}

		@Override
		public void setAttribute(String key, String value) {
			
		}

		@Override
		public IStreamsProxy getStreamsProxy() {
			return null;
		}

		@Override
		public ILaunch getLaunch() {
			return launch;
		}

		@Override
		public String getLabel() {
			return label;
		}

		@Override
		public int getExitValue() throws DebugException {
			return exitValue;
		}

		@Override
		public String getAttribute(String key) {
			return launch.getAttribute(key);
		}

		public void setTerminated(boolean terminated) {
			this.terminated = terminated;
		}
	}

	public static final String MODEL_URI = "modelUri";

	@Override
	public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor)
			throws CoreException {
		String modelUri = configuration.getAttribute(MODEL_URI, "");
		final URI modelURI =  URI.createPlatformResourceURI(modelUri,
														 true);
		
//		final String generationTarget = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString();// configuration.getAttribute(PreferenceConstants.GENERATION_TARGET, "");
		Path path = new Path(modelUri);
		final IResource findMember = ResourcesPlugin.getWorkspace().getRoot().findMember(path);
		if (findMember instanceof IFile) {
		}else
			throw new CoreException(Status.CANCEL_STATUS);
		final IFile file = (IFile) findMember;

		final ILaunchConfiguration con1 = configuration;
		Uml2Service.setStore(new PreferenceStore(){
			@Override
			public String getString(String name) {
				try {
					String attribute = con1.getAttribute(name, "");
					return attribute;
				} catch (CoreException e) {
				}
				return "";
			}
			@Override
			public boolean getBoolean(String name) {
				try {
					boolean attribute = con1.getAttribute(name, false);
					return attribute;
				} catch (CoreException e) {
				}
				return false;
			}
		});
		configuration.getAttributes();
		IProcessImplementation process = new IProcessImplementation(launch);
		try {
			launch.addProcess(process);
			process.label = "generation working";
			IContainer target = file.getProject();//ResourcesPlugin.getWorkspace().getRoot();//model.getProject().getFolder(gtarget);
			GenerateAll generator = new GenerateAll(modelURI, target, new ArrayList<Object>());
			generator.doGenerate(monitor);
			process.label = "generation finished.";
		} catch (IOException e) {
			process.label = e.getMessage();
			process.exitValue =1;
			throw new CoreException(Status.CANCEL_STATUS);
		} finally {
			file.getProject().refreshLocal(IResource.DEPTH_INFINITE, monitor);
		}
		if(monitor!=null)
			monitor.done();
		process.setTerminated(true);
	}

}
