/**
 * 
 */
package de.urszeidler.eclipse.solidity.laucher;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * @author urs
 *
 */
public class Activator extends AbstractUIPlugin {

	private static Activator plugin;
	/**
	 * The plug-in ID.
	 */
	public static final String PLUGIN_ID = "de.urszeidler.eclipse.solidity.laucher.ui";

	/**
	 * 
	 */
	public Activator() {
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.core.runtime.Plugin#start(org.osgi.framework.BundleContext)
	 * @generated
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	@Override
	protected void initializeImageRegistry(ImageRegistry reg) {
		ImageDescriptor image = imageDescriptorFromPlugin(PLUGIN_ID, "images/solidity16.png");
		reg.put("UML2Solidity", image);
		image = imageDescriptorFromPlugin(PLUGIN_ID, "images/solidity16.png");
		reg.put("JsCode", image);
		image = imageDescriptorFromPlugin(PLUGIN_ID, "images/solidity16.png");
		reg.put("OtherFiles", image);
		super.initializeImageRegistry(reg);
	}
	
	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 * @generated
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance.
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	public static void logError(String message, Exception e) {
		getDefault().getLog().log(createErrorStatus(message, e));
	}

	public static Status createErrorStatus(String message, Exception e) {
		return new Status(IStatus.ERROR, PLUGIN_ID, message, e);
	}

	public static void logError(String message) {
		getDefault().getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, message));
	}

	public static void logInfo(String message) {
		getDefault().getLog().log(new Status(IStatus.INFO, PLUGIN_ID, message));
	}

	public static void logWarning(String message) {
		getDefault().getLog().log(new Status(IStatus.INFO, PLUGIN_ID, message));
	}

}
