package de.urszeidler.eclipse.solidity.ui.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import de.urszeidler.eclipse.solidity.ui.Activator;

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
		IPreferenceStore store = new ScopedPreferenceStore(InstanceScope.INSTANCE, Activator.PLUGIN_ID);
		
		store.setDefault(PreferenceConstants.GENERATE_HTML, true);
		store.setDefault(PreferenceConstants.GENERATE_MIX, true);
		store.setDefault(PreferenceConstants.GENERATE_WEB3, true);
		store.setDefault(PreferenceConstants.GENERATE_MARKDOWN, true);
		store.setDefault(PreferenceConstants.GENERATION_TARGET, "mix");
		store.setDefault(PreferenceConstants.GENERATION_TARGET_DOC, "doc");

		store.setDefault(PreferenceConstants.GENERATOR_PROJECT_SETTINGS, false);
	}

}
