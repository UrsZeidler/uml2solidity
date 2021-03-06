package de.urszeidler.eclipse.solidity.compiler.support.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;



/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() {
		IPreferenceStore store = PreferenceConstants.getPreferenceStore(null);
		store.setDefault(PreferenceConstants.COMPILE_CONTRACTS, false);
		store.setDefault(PreferenceConstants.COMPILER_PROGRAMM,
				"/usr/bin/solc");
		store.setDefault(PreferenceConstants.COMPILER_TARGET,
				"bin");
		store.setDefault(PreferenceConstants.SOL_SRC_DIRECTORY,
				"sol-src/");
		store.setDefault(PreferenceConstants.COMPILER_BIN, true);
		store.setDefault(PreferenceConstants.COMPILER_BIN_RUNTIME, false);
		store.setDefault(PreferenceConstants.COMPILER_ABI, true);
		store.setDefault(PreferenceConstants.COMPILER_INTERFACT, false);
		store.setDefault(PreferenceConstants.COMPILER_ASM, false);
		store.setDefault(PreferenceConstants.COMPILER_AST, false);
		store.setDefault(PreferenceConstants.COMPILER_ASM_JSON, false);
		store.setDefault(PreferenceConstants.COMPILER_AST_JSON, false);
		store.setDefault(PreferenceConstants.COMPILER_USERDOC, false);
		store.setDefault(PreferenceConstants.COMPILER_DEVDOC, false);
		store.setDefault(PreferenceConstants.COMPILER_OPTIMIZE, false);
		store.setDefault(PreferenceConstants.COMPILER_OPCODE, false);
		store.setDefault(PreferenceConstants.COMPILER_FORMAL, false);
		store.setDefault(PreferenceConstants.COMPILER_HASHES, false);
		
		store.setDefault(PreferenceConstants.ENABLE_GAS_OPTIMIZE, false);
		store.setDefault(PreferenceConstants.ESTIMATE_GAS_COSTS, false);		
		
		store.setDefault(PreferenceConstants.COMBINED_JSON_OPTIONS, "abi,bin,metadata");		 
		store.setDefault(PreferenceConstants.COMPILER_TARGET_COMBINE_ABI, "bin/combine.json");		 
		
		store.setDefault(PreferenceConstants.SELECTED_COMPILER, "/usr/bin/solc");
	}

}
