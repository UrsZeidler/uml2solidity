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

	public static final String GENERATE_CONTRACT_FILES = "GENERATE_CONTRACT_FILES";
	public static final String GENERATION_TARGET = "GENERATION_TARGET";
	public static final String GENERATION_TARGET_DOC = "GENERATION_TARGET_DOC";
	public static final String GENERATION_ALL_IN_ONE_FILE = "GENERATION_ALL_IN_ONE_FILE";
	
	public static final String GENERATE_JAVA_INTERFACE = "GENERATE_JAVA_INTERFACE";
	public static final String GENERATION_JAVA_INTERFACE_TARGET = "GENERATION_JAVA_INTERFACE_TARGET";
	public static final String GENERATION_JAVA_INTERFACE_PACKAGE_PREFIX = "GENERATION_JAVA_INTERFACE_PACKAGE_PREFIX";
	public static final String GENERATION_JAVA_2_SOLIDITY_TYPES = "GENERATION_JAVA_2_SOLIDITY_TYPES";
	public static final String GENERATION_JAVA_2_SOLIDITY_TYPE_PREFIX = "GENERATION_JAVA_2_SOLIDITY_TYPE_";
	public static final String GENERATE_JAVA_TESTS = "GENERATE_JAVA_TESTS";
	public static final String GENERATION_JAVA_TEST_TARGET = "GENERATION_JAVA_TEST_TARGET";
	

	public static final String GENERATE_WEB3 = "GENERATE_WEB3";
	public static final String GENERATE_HTML = "GENERATE_HTML";	
	public static final String GENERATE_MIX = "GENERATE_MIX";
	public static final String GENERATE_MARKDOWN = "GENERATE_MARKDOWN";
	
	public static final String JS_FILE_HEADER = "JS_FILE_HEADER";
	public static final String GENERATE_JS_CONTROLLER = "GENERATE_JS_CONTROLLER";
	public static final String GENERATE_JS_CONTROLLER_TARGET = "GENERATE_JS_CONTROLLER_TARGET";
	public static final String GENERATE_JS_TEST = "GENERATE_JS_TEST_CONTROLLER";
	public static final String GENERATE_JS_TEST_TARGET = "GENERATE_JS_TEST_TARGET";
	public static final String GENERATE_ABI_TARGET = "GENERATE_ABI_TARGET";
	public static final String GENERATE_ABI = "GENERATE_ABI";
	
	public static final String GENERATOR_PROJECT_SETTINGS = "COMPILE_CONTRACTS_PROJECT_SETTINGS";
	public static final String CONTRACT_FILE_HEADER = "CONTRACT_FILE_HEADER";

	public static final String VERSION_PRAGMA = "version_pragma";
	public static final String ENABLE_VERSION = "enable_version";
	public static final String GENERATE_JAVA_NONBLOCKING = "GENERATE_JAVA_NONBLOCKING";

	
	
	public static IPreferenceStore getPreferenceStore(IProject project) {
		if (project != null) {
			IPreferenceStore store = new ScopedPreferenceStore(new ProjectScope(project), Activator.PLUGIN_ID);
			if(store.getBoolean(PreferenceConstants.GENERATOR_PROJECT_SETTINGS))
			return store;
		}
		return new ScopedPreferenceStore(InstanceScope.INSTANCE, Activator.PLUGIN_ID);//Activator.PLUGIN_ID);
	}

}
