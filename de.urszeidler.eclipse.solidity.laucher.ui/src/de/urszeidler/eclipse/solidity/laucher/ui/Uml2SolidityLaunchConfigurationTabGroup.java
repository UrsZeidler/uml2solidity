/**
 * 
 */
package de.urszeidler.eclipse.solidity.laucher.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
	
	private class LaunchingConfig {
		private ILaunchConfigurationTab tab;
		private int order;
	}
	
	private final class ComparatorImplementation implements Comparator<LaunchingConfig> {
		@Override
		public int compare(LaunchingConfig o1, LaunchingConfig o2) {
			if(o1==null)
				return -1;
			if(o2==null)
				return 1;
			return o1.order-o2.order;
		}
	}
	
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

		List<LaunchingConfig> confs = new ArrayList<LaunchingConfig>();
		for (IConfigurationElement element : configurationElements) {
			ILaunchConfigurationTab tab;
			try {
				tab = (ILaunchConfigurationTab) element.createExecutableExtension("tab_class");
				String orderString = element.getAttribute("tab_order");
				int order = 10;
				try {
					order = Integer.parseInt(orderString);
				} catch (NumberFormatException e) {
				}
				
				LaunchingConfig launchingConfig = new LaunchingConfig();
				launchingConfig.tab = tab;
				launchingConfig.order = order;
				confs.add(launchingConfig);
			} catch (Exception e) {
				Activator.logError("Error instanciate the tab.", e);
			}
		}
		
		Collections.sort(confs, new ComparatorImplementation());
		List<ILaunchConfigurationTab> tabs = new ArrayList<ILaunchConfigurationTab>();
		for (LaunchingConfig launchingConfig : confs) {
			tabs.add(launchingConfig.tab);
		}
		
		tabs.add(new CommonTab());
		setTabs(tabs.toArray(new ILaunchConfigurationTab[]{}));
	}

}
