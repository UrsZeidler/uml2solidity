package de.urszeidler.eclipse.solidity.compiler.support.preferences;

import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringButtonFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import de.urszeidler.eclipse.solidity.compiler.support.Activator;
import de.urszeidler.eclipse.solidity.compiler.support.preferences.PreferenceConstants.SolC;

public class SolcBuilderPreferencePage extends AbstractProjectPreferencesPage implements IWorkbenchPreferencePage {

	private StringFieldEditor editor;
	private StringFieldEditor compilerTarget;
	private StringFieldEditor sourceDirectory;

	/**
	 * Create the preference page.
	 */
	public SolcBuilderPreferencePage() {
		super(GRID);
	}

	/**
	 * Create contents of the preference page.
	 */
	@Override
	protected void createFieldEditors() {
		// Create the field editors
		if (!isPropertyPage()) {
			sourceDirectory = new StringFieldEditor(PreferenceConstants.SOL_SRC_DIRECTORY, "source directory", -1,
					StringFieldEditor.VALIDATE_ON_KEY_STROKE, getFieldEditorParent());
			addField(sourceDirectory);
		} else {
			sourceDirectory = new StringButtonFieldEditor(PreferenceConstants.SOL_SRC_DIRECTORY, "source directory",
					getFieldEditorParent()) {

				@Override
				protected String changePressed() {
					Path srcDir = new Path(getStringValue());
					IResource member = project.findMember(srcDir);
					if (member == null)
						member = ResourcesPlugin.getWorkspace().getRoot().findMember(srcDir);

					ContainerSelectionDialog containerSelectionDialog = new ContainerSelectionDialog(getShell(),
							(IContainer) member, false, "select dirctory of the source files");
					containerSelectionDialog.open();
					Object[] result = containerSelectionDialog.getResult();
					if (result != null && result.length == 1) {
						IPath container = (IPath) result[0];

						compilerTarget.setStringValue(container.toString() + "/combined.json");
						return container.toString();
					}
					return null;
				}
			};
			sourceDirectory.setEmptyStringAllowed(false);
			addField(sourceDirectory);
		}
		compilerTarget = new StringFieldEditor(PreferenceConstants.COMPILER_TARGET_COMBINE_ABI, "compile to file", -1,
				StringFieldEditor.VALIDATE_ON_KEY_STROKE, getFieldEditorParent());
		addField(compilerTarget);

		IPreferenceStore store = new ScopedPreferenceStore(InstanceScope.INSTANCE, preferencesId());
		final List<SolC> parsePreferences = PreferenceConstants
				.parsePreferences(store.getString(PreferenceConstants.INSTALLED_SOL_COMPILERS));

		Set<Object> collect = parsePreferences.stream().map(new Function<SolC, String[]>() {
			@Override
			public String[] apply(SolC t) {
				return new String[] { t.name, t.path };
			}
		}).collect(Collectors.toSet());
		String[][] cvalues = new String[][] {};
		cvalues = collect.toArray(cvalues);

		ComboFieldEditor comboFieldEditor = new ComboFieldEditor(PreferenceConstants.SELECTED_COMPILER,
				"selected compiler", cvalues, getFieldEditorParent()) {
			@Override
			protected void fireValueChanged(String property, Object oldValue, Object newValue) {
				getPreferenceStore().setValue(PreferenceConstants.COMPILER_PROGRAMM, (String) newValue);
				getPreferenceStore().setValue(PreferenceConstants.SELECTED_COMPILER, (String) newValue);
				editor.load();
				super.fireValueChanged(property, oldValue, newValue);
			}
		};
		addField(comboFieldEditor);
		editor = new StringFieldEditor(PreferenceConstants.COMPILER_PROGRAMM, "Compiler path", -1,
				StringFieldEditor.VALIDATE_ON_KEY_STROKE, getFieldEditorParent());
		editor.setEnabled(false, getFieldEditorParent());
		addField(editor);

		addField(new BooleanFieldEditor(PreferenceConstants.ENABLE_GAS_OPTIMIZE, "Enable gas optimization.",
				BooleanFieldEditor.DEFAULT, getFieldEditorParent()));

		addField(new BooleanFieldEditor(PreferenceConstants.ESTIMATE_GAS_COSTS, "Write estimate gas costs.",
				BooleanFieldEditor.DEFAULT, getFieldEditorParent()));
	}

    @Override
	public void createControl(Composite parent){
		if(isPropertyPage()){
			setDescription(
					"The project solidity builder preferences. "
					+ "The builder compiles to a combine json format. "
					+ "All *.sol files of the source directory are selected. Add/remove the builder via configure project.");
			
		}else
		setDescription(
				"The solidity builder preferences. "
				+ "The builder compiles to a combine json format. When selected for a project. "
				+ "All *.sol files of the source directory are selected. Add/remove the builder via configure project.");

    	super.createControl(parent);
    }
	
	@Override
	protected void checkState() {
		super.checkState();
		validateInput();
	}

	/**
	 * Initialize the preference page.
	 */
	public void init(IWorkbench workbench) {
		// Initialize the preference page
		
	}

	@Override
	protected String preferencesId() {
		return Activator.PLUGIN_ID;
	}

	@Override
	protected String useProjectSettingsPreferenceName() {
		return PreferenceConstants.BUILDER_PROJECT_SETTINGS;
	}

	@Override
	protected String preferencesPageId() {
		return "de.urszeidler.eclipse.solidity.ui.preferences.BuilderPreferencePage";
	}

	@Override
	protected void updateFieldEditors(boolean enabled) {
		super.updateFieldEditors(enabled);
		if (enabled)
			editor.setEnabled(false, getFieldEditorParent());
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		super.propertyChange(event);
		validateInput();
	}

	/**
	 * 
	 */
	private void validateInput() {
		if (isPropertyPage()) {

			Path srcDir = new Path(sourceDirectory.getStringValue());
			IResource member = project.findMember(srcDir);
			if (member == null)
				member = ResourcesPlugin.getWorkspace().getRoot().findMember(srcDir);
			if (member == null || !member.exists()) {
				setErrorMessage("source directory does not exist");
				return;
			}
			if (member.getType() != IResource.FOLDER) {
				setErrorMessage("source must be a folder");
				return;
			}

			String stringValue = compilerTarget.getStringValue();
			Path ct = new Path(stringValue);
			IResource resource = project.findMember(ct);
			if (resource == null)
				resource = project.findMember(ct.removeLastSegments(1));
			if (resource == null)
				resource = ResourcesPlugin.getWorkspace().getRoot().findMember(ct);
			if (resource == null)
				resource = ResourcesPlugin.getWorkspace().getRoot().findMember(ct.removeLastSegments(1));
			if (resource == null || !resource.exists()) {
				setErrorMessage("target file or dir not found:" + stringValue);
				return;
			}
		}

		String fileName = editor.getStringValue();
		File file = new File(fileName);
		if (!file.exists()) {
			setErrorMessage("The selected compiles does not exitst.");
			return;
		}
		if (!file.canExecute()) {
			setErrorMessage("The selected compiler can not be executed.");
			return;
		}
	}

}
