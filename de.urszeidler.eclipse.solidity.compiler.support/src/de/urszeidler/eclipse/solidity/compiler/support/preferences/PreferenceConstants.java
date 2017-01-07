package de.urszeidler.eclipse.solidity.compiler.support.preferences;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import de.urszeidler.eclipse.solidity.compiler.support.Activator;

/**
 * Constant definitions for plug-in preferences
 */
public class PreferenceConstants {

	public static final String COMPILE_CONTRACTS = "COMPILE_CONTRACTS";
	public static final String COMPILER_PROGRAMM = "COMPILER_PROGRAMM";
	public static final String COMPILER_TARGET = "COMPILER_TARGET";
	public static final String COMPILER_ABI = "COMPILER_ABI";
	public static final String COMPILER_INTERFACT = "COMPILER_INTERFACT";
	public static final String COMPILER_ASM = "COMPILER_ASM";
	public static final String COMPILER_ASM_JSON = "COMPILER_ASM_JSON";
	public static final String COMPILER_BIN = "COMPILER_BIN";
	public static final String COMPILER_BIN_RUNTIME = "COMPILER_BIN_RUNTIME";
	public static final String COMPILER_AST = "COMPILER_AST";
	public static final String COMPILER_AST_JSON = "COMPILER_AST_JSON";
	public static final String COMPILER_USERDOC = "COMPILER_USERDOC";
	public static final String COMPILER_DEVDOC = "COMPILER_DEVDOC";
	public static final String COMPILER_OPTIMIZE = "COMPILER_OPTIMIZE";
	public static final String COMPILER_OPCODE = "COMPILER_OPCODE";
	public static final String COMPILER_FORMAL = "COMPILER_FORMAL";
	public static final String COMPILER_HASHES = "COMPILER_HASHES";
	public static final String COMBINED_JSON = "COMBINED_JSON";
	public static final String COMBINED_JSON_OPTIONS = "COMBINED_JSON_OPTIONS";
	public static final String SOL_SRC_DIRECTORY = "SOL_SRC_DIRECTORY";
	public static final String COMPILER_TARGET_COMBINE_ABI = "COMPILER_TARGET_COMBINE_ABI";
	public static final String COMPILER_TARGET_COMBINE_ABI_PATH = "COMPILER_TARGET_COMBINE_ABI_PATH";
	
	

	public static final String COMPILER_PROJECT_SETTINGS = "COMPILE_CONTRACTS_PROJECT_SETTINGS";
	public static final String BUILDER_PROJECT_SETTINGS = "BUILDER_PROJECT_SETTINGS";

	public static final String[] COMPILE_SWITCHES = {COMPILER_INTERFACT,
			COMPILER_ASM,COMPILER_ASM_JSON,COMPILER_BIN,COMPILER_BIN_RUNTIME,
			COMPILER_AST,COMPILER_AST_JSON, COMPILER_USERDOC,COMPILER_DEVDOC,
			COMPILER_OPTIMIZE,COMPILER_OPCODE,COMPILER_FORMAL,COMPILER_HASHES};
	
	
	public static IPreferenceStore getPreferenceStore(IProject project) {
		if (project != null) {
			IPreferenceStore store = new ScopedPreferenceStore(new ProjectScope(project), Activator.PLUGIN_ID);
			if(store.getBoolean(PreferenceConstants.COMPILER_PROJECT_SETTINGS)|| store.getBoolean(PreferenceConstants.BUILDER_PROJECT_SETTINGS))
			return store;
		}
		return new ScopedPreferenceStore(InstanceScope.INSTANCE, Activator.PLUGIN_ID);//Activator.PLUGIN_ID);
	}

}
