package de.urszeidler.eclipse.solidity.ui.preferences;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import de.urszeidler.eclipse.solidity.ui.Activator;

/**
 * Constant definitions for plug-in preferences
 */
public class PreferenceConstants {

	public static final String GENERATION_TARGET = "GENERATION_TARGET";
	public static final String GENERATION_TARGET_DOC = "GENERATION_TARGET_DOC";

	public static final String GENERATE_WEB3 = "GENERATE_WEB3";
	public static final String GENERATE_HTML = "GENERATE_HTML";	
	public static final String GENERATE_MIX = "GENERATE_MIX";
	public static final String GENERATE_MARKDOWN = "GENERATE_MARKDOWN";
	public static final String GENERATE_JS_CONTROLLER = "GENERATE_JS_CONTROLLER";
	public static final String GENERATE_JS_CONTROLLER_TRAGET = "GENERATE_JS_CONTROLLER_TRAGET";
	
	public static final String GENERATOR_PROJECT_SETTINGS = "COMPILE_CONTRACTS_PROJECT_SETTINGS";
	public static final String CONTRACT_FILE_HEADER = "CONTRACT_FILE_HEADER";

	
	public static IPreferenceStore getPreferenceStore(IProject project) {
		if (project != null) {
			IPreferenceStore store = new ScopedPreferenceStore(new ProjectScope(project), Activator.PLUGIN_ID);
			if(store.getBoolean(PreferenceConstants.GENERATOR_PROJECT_SETTINGS))
			return store;
		}
		return new ScopedPreferenceStore(InstanceScope.INSTANCE, Activator.PLUGIN_ID);//Activator.PLUGIN_ID);
	}

}
