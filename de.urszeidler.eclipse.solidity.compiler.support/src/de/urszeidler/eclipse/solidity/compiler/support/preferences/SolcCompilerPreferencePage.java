package de.urszeidler.eclipse.solidity.compiler.support.preferences;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import de.urszeidler.eclipse.solidity.compiler.support.Activator;

public class SolcCompilerPreferencePage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage, IWorkbenchPropertyPage {

	private IProject project;

	/**
	 * Create the preference page.
	 */
	public SolcCompilerPreferencePage() {
		super(GRID);
		setDescription("The solidity compiler preferences.");
	}

	/**
	 * Create contents of the preference page.
	 */
	@Override
	protected void createFieldEditors() {

		if (project == null) {
			IPreferenceStore store = new ScopedPreferenceStore(InstanceScope.INSTANCE, Activator.PLUGIN_ID);
			setPreferenceStore(Activator.getDefault().getPreferenceStore());
			
			
		} else {
			IPreferenceStore store = new ScopedPreferenceStore(new ProjectScope(project), Activator.PLUGIN_ID);
			setPreferenceStore(store);
			addField(new BooleanFieldEditor(PreferenceConstants.COMPILE_CONTRACTS_PROJECT_SETTINGS, "Project Settings",
					BooleanFieldEditor.DEFAULT, getFieldEditorParent()));
			// setPreferenceStore(node);
		}

		
		
		// Create the field editors
		addField(new BooleanFieldEditor(PreferenceConstants.COMPILE_CONTRACTS, "Compile generated solidity code.",
				BooleanFieldEditor.DEFAULT, getFieldEditorParent()));
		addField(new StringFieldEditor(PreferenceConstants.COMPILER_PROGRAMM, "Compiler path", -1,
				StringFieldEditor.VALIDATE_ON_KEY_STROKE, getFieldEditorParent()));
		addField(new StringFieldEditor(PreferenceConstants.COMPILER_TARGET, "target directory", -1,
				StringFieldEditor.VALIDATE_ON_KEY_STROKE, getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.COMPILER_OPTIMIZE, "optimize", BooleanFieldEditor.DEFAULT,
				getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.COMPILER_BIN, "generate bin", BooleanFieldEditor.DEFAULT,
				getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.COMPILER_BIN_RUNTIME, "generate bin runtime",
				BooleanFieldEditor.DEFAULT, getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.COMPILER_ABI, "generate abi", BooleanFieldEditor.DEFAULT,
				getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.COMPILER_INTERFACT, "generate interface",
				BooleanFieldEditor.DEFAULT, getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.COMPILER_ASM, "generate asm", BooleanFieldEditor.DEFAULT,
				getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.COMPILER_ASM_JSON, "generate asm json",
				BooleanFieldEditor.DEFAULT, getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.COMPILER_AST, "generate ast", BooleanFieldEditor.DEFAULT,
				getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.COMPILER_AST_JSON, "generate ast json",
				BooleanFieldEditor.DEFAULT, getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.COMPILER_USERDOC, "generate user doc",
				BooleanFieldEditor.DEFAULT, getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.COMPILER_DEVDOC, "generate dev doc",
				BooleanFieldEditor.DEFAULT, getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.COMPILER_OPCODE, "generate optcode",
				BooleanFieldEditor.DEFAULT, getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.COMPILER_FORMAL, "generate formal",
				BooleanFieldEditor.DEFAULT, getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.COMPILER_HASHES, "generate hashes",
				BooleanFieldEditor.DEFAULT, getFieldEditorParent()));
	}

	/**
	 * Initialize the preference page.
	 */
	public void init(IWorkbench workbench) {
		// Initialize the preference page
	}

	@Override
	public IAdaptable getElement() {
		return project;
	}

	@Override
	public void setElement(IAdaptable element) {
		if (element instanceof IProject) {
			project = (IProject) element;

		}
	}

}
