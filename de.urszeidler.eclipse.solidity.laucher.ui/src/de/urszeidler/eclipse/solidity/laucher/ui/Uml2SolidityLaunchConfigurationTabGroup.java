/**
 * 
 */
package de.urszeidler.eclipse.solidity.laucher.ui;

import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;

/**
 * @author urs
 *
 */
public class Uml2SolidityLaunchConfigurationTabGroup extends AbstractLaunchConfigurationTabGroup {

	/**
	 * 
	 */
	public Uml2SolidityLaunchConfigurationTabGroup() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTabGroup#createTabs(org.eclipse.debug.ui.ILaunchConfigurationDialog, java.lang.String)
	 */
	@Override
	public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
		ILaunchConfigurationTab[] tabs = new ILaunchConfigurationTab[] { new GenerateUml2SolidityCodeConfigurationTab(),
				new GenerateJSCodeConfigurationTab(), new GenerateOthersConfigurationTab(),new CommonTab() };
		setTabs(tabs);
	}

}
