/**
 * 
 */
package de.urszeidler.eclipse.solidity.laucher.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;

import de.urszeidler.eclipse.solidity.laucher.Activator;

/**
 * @author urs
 *
 */
public class Uml2SolidityLaunchConfigurationTabGroup extends AbstractLaunchConfigurationTabGroup {

	/**
	 * 
	 */
	public Uml2SolidityLaunchConfigurationTabGroup() {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTabGroup#createTabs(org.eclipse.debug.ui.ILaunchConfigurationDialog, java.lang.String)
	 */
	@Override
	public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
		IConfigurationElement[] configurationElements = Platform.getExtensionRegistry()
				.getConfigurationElementsFor("de.urszeidler.eclipse.solidity.um2solidity.m2t.laucherTab");

		List<ILaunchConfigurationTab> tabs = new ArrayList<ILaunchConfigurationTab>();
		for (IConfigurationElement element : configurationElements) {
			ILaunchConfigurationTab tab;
			try {
				tab = (ILaunchConfigurationTab) element.createExecutableExtension("tab_class");
				tabs.add(tab);
			} catch (Exception e) {
				Activator.logError("Error instanciate the tab.", e);
			}
		}
		
		tabs.add(new CommonTab());
		setTabs(tabs.toArray(new ILaunchConfigurationTab[]{}));
	}

}
