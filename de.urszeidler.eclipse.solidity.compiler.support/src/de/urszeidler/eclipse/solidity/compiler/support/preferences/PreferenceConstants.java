package de.urszeidler.eclipse.solidity.compiler.support.preferences;

import java.util.ArrayList;
import java.util.List;

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
	public static final String ENABLE_GAS_OPTIMIZE = "ENABLE_GAS_OPTIMIZE";
	public static final String ESTIMATE_GAS_COSTS = "ESTIMATE_GAS_COSTS";
	
	public static final String INSTALLED_SOL_COMPILERS = "INSTALLED_SOL_COMPILERS";
	

	public static final String COMPILER_PROJECT_SETTINGS = "COMPILE_CONTRACTS_PROJECT_SETTINGS";
	public static final String BUILDER_PROJECT_SETTINGS = "BUILDER_PROJECT_SETTINGS";
	public static final String SELECTED_COMPILER = "SELECTED_COMPILER";

	public static final String[] COMPILE_SWITCHES = {COMPILER_INTERFACT,
			COMPILER_ASM,COMPILER_ASM_JSON,COMPILER_BIN,COMPILER_BIN_RUNTIME,
			COMPILER_AST,COMPILER_AST_JSON, COMPILER_USERDOC,COMPILER_DEVDOC,
			COMPILER_OPTIMIZE,COMPILER_OPCODE,COMPILER_FORMAL,COMPILER_HASHES};
	
	
	public static class SolC{
		String name;
		String path;
		String version;
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			result = prime * result + ((path == null) ? 0 : path.hashCode());
			result = prime * result + ((version == null) ? 0 : version.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			SolC other = (SolC) obj;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			if (path == null) {
				if (other.path != null)
					return false;
			} else if (!path.equals(other.path))
				return false;
			if (version == null) {
				if (other.version != null)
					return false;
			} else if (!version.equals(other.version))
				return false;
			return true;
		}
	}
	
	public static IPreferenceStore getPreferenceStore(IProject project) {
		if (project != null) {
			IPreferenceStore store = new ScopedPreferenceStore(new ProjectScope(project), Activator.PLUGIN_ID);
			if(store.getBoolean(PreferenceConstants.COMPILER_PROJECT_SETTINGS)|| store.getBoolean(PreferenceConstants.BUILDER_PROJECT_SETTINGS))
			return store;
		}
		return new ScopedPreferenceStore(InstanceScope.INSTANCE, Activator.PLUGIN_ID);//Activator.PLUGIN_ID);
	}
	
	public static List<SolC> parsePreferences(String pref) {
		ArrayList<SolC> list = new ArrayList<SolC>();
		String[] split = pref.split("::");
		for (String line : split) {
			String[] split2 = line.split(",");
			if(split2.length!=3)
				continue;
			SolC solc=new SolC();
			solc.name = split2[0];
			solc.path = split2[1];
			solc.version = split2[2];
			list.add(solc);
		}
		return list;
	}

	public static SolC solCof(String name,String path, String version) {
		SolC solc=new SolC();
		solc.name = name;
		solc.path = path;
		solc.version = version;
		return solc;
	}
	
}
