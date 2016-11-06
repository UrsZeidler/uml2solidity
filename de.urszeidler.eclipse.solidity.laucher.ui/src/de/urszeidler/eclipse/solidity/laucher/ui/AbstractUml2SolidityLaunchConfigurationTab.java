/**
 * 
 */
package de.urszeidler.eclipse.solidity.laucher.ui;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.swt.widgets.Decorations;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;

import de.urszeidler.eclipse.solidity.laucher.core.GenerateUml2Solidity;

/**
 * @author urs
 *
 */
public abstract class AbstractUml2SolidityLaunchConfigurationTab extends AbstractLaunchConfigurationTab {

	public void handleChooseContainer(Text base_target_text,String dialogTitel) {
		IContainer initialRoot = toContainer(base_target_text.getText());
		ContainerSelectionDialog containerSelectionDialog = new ContainerSelectionDialog(getShell(),
				initialRoot, false, dialogTitel);
		containerSelectionDialog.open();
		Object[] result = containerSelectionDialog.getResult();
		if (result != null && result.length == 1) {
			IPath container = (IPath) result[0];
			base_target_text.setText(container.toString());
			setDirty(true);
			updateLaunchConfigurationDialog();
		}

	}
	
	/**
	 * Return the container or the workspace root.
	 * @param p
	 * @return
	 */
	protected IContainer toContainer(String p) {
		IContainer initialRoot = ResourcesPlugin.getWorkspace().getRoot();//.findMember(generationDirectoryText.getText());
		Path path = new Path(p);
		 IResource findMember = initialRoot.findMember(path);
		 if (findMember instanceof IContainer) {
			 initialRoot = (IContainer) findMember;
			
		}
		return initialRoot;
	}

	/**
	 * @param configuration
	 * @param resourceName
	 * @return
	 * @throws CoreException
	 */
	protected IResource findResource(ILaunchConfiguration configuration, String resourceName) throws CoreException {
		String model = configuration.getAttribute(GenerateUml2Solidity.MODEL_URI, "");
		Path path = new Path(model);
		IResource findMember = ResourcesPlugin.getWorkspace().getRoot().findMember(path);
		if (findMember instanceof IFile) {
			IFile f = (IFile) findMember;
			IProject project = f.getProject();
			IResource member = project.findMember(resourceName);
			return member;
		}
		return null;
	}

}
