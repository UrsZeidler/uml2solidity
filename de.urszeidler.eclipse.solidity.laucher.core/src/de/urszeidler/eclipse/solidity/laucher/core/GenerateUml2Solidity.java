/**
 * 
 */
package de.urszeidler.eclipse.solidity.laucher.core;

import java.util.ArrayList;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.PlatformUI;

import de.urszeidler.eclipse.solidity.ui.popupMenus.AcceleoGenerateSolidityAction;
import de.urszeidler.eclipse.solidity.ui.preferences.PreferenceConstants;

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
		final URI modelURI = URI.createURI(modelUri);// URI.createPlatformResourceURI(modelUri,
														// true);
		String generationTarget = configuration.getAttribute(PreferenceConstants.GENERATION_TARGET, "");
		final IFolder target = ResourcesPlugin.getWorkspace().getRoot().getFolder(new Path(generationTarget));

		
		//TODO initialize the data and set in the umlService class
		boolean generateMixConfig = configuration.getAttribute(PreferenceConstants.GENERATE_MIX, false);

		IRunnableWithProgress operation = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) {
				try {
					IPreferenceStore store = PreferenceConstants.getPreferenceStore(target.getProject()); // Activator.getDefault().getPreferenceStore();
					AcceleoGenerateSolidityAction.modelTransform(target, monitor, modelURI, new ArrayList<Object>());
				} catch (CoreException e) {
					throw new RuntimeException(e);
				}
			}
		};
		try {
			PlatformUI.getWorkbench().getProgressService().run(true, true, operation);
		} catch (Exception e) {
			IStatus status = new Status(IStatus.ERROR, "", e.getMessage(), e);
			throw new CoreException(status);
		}
	}

}
