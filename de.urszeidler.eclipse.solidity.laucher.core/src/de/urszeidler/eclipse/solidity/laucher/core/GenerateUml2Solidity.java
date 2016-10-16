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
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
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
		//TODO: check out if this is a nice and simple way
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
		try {
			IContainer target = file.getProject();//ResourcesPlugin.getWorkspace().getRoot();//model.getProject().getFolder(gtarget);
			GenerateAll generator = new GenerateAll(modelURI, target, new ArrayList<Object>());
			generator.doGenerate(monitor);
		} catch (IOException e) {
			throw new CoreException(Status.CANCEL_STATUS);
		} finally {
			file.getProject().refreshLocal(IResource.DEPTH_INFINITE, monitor);
		}

	}

}
