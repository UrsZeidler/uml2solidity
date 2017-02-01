package de.urszeidler.eclipse.solidity.compiler.support.preferences;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

public abstract class AbstractProjectPreferencesPage extends FieldEditorPreferencePage
		implements IWorkbenchPropertyPage {

	protected IProject project;
	private Button useProjectSettingsButton;
	private Link link;
	private List<FieldEditor> editors = new ArrayList<FieldEditor>();

	public AbstractProjectPreferencesPage() {
		super();
	}

	public AbstractProjectPreferencesPage(int style) {
		super(style);
	}

	public AbstractProjectPreferencesPage(String title, int style) {
		super(title, style);
	}

	public AbstractProjectPreferencesPage(String title, ImageDescriptor image, int style) {
		super(title, image, style);
	}

	public IAdaptable getElement() {
		return project;
	}

	public void setElement(IAdaptable element) {
		if (element instanceof IProject) {
			project = (IProject) element;
		} else if (element instanceof IProjectNature) {
			IProjectNature pn = (IProjectNature) element;
			project = pn.getProject();
		}
	}

	@Override
	protected Control createContents(Composite parent) {
		if (isPropertyPage())
			createUseProjectSettingsControls(parent);
		return super.createContents(parent);
	}

	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);
		if (isPropertyPage())
			handleUseProjectSettings();
	}

	@Override
	public IPreferenceStore getPreferenceStore() {
		if (project == null) {
			IPreferenceStore store = new ScopedPreferenceStore(InstanceScope.INSTANCE, preferencesId());// Activator.PLUGIN_ID);
			return store;
		} else {
			IPreferenceStore store = new ScopedPreferenceStore(new ProjectScope(project), preferencesId());
			return store;
		}
	}

	private void createUseProjectSettingsControls(Composite parent) {
		Composite projectSettingsParent = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		projectSettingsParent.setLayout(layout);
		projectSettingsParent.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// use project settings button
		useProjectSettingsButton = new Button(projectSettingsParent, SWT.CHECK);
		useProjectSettingsButton.setText("Project Settings");
		useProjectSettingsButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleUseProjectSettings();

			}
		});

		// configure ws settings link
		link = new Link(projectSettingsParent, SWT.NONE);
		link.setFont(projectSettingsParent.getFont());
		link.setText("<A>" + "configure" + "</A>"); //$NON-NLS-1$
		link.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String id = preferencesPageId();
				PreferencesUtil.createPreferenceDialogOn(getShell(), id, new String[] { id }, null).open();
				updateFieldEditors(false);
			}
		});
		link.setLayoutData(new GridData(SWT.END, SWT.CENTER, true, false));

		// separator line
		Label horizontalLine = new Label(projectSettingsParent, SWT.SEPARATOR | SWT.HORIZONTAL);
		horizontalLine.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 2, 1));
		horizontalLine.setFont(projectSettingsParent.getFont());
		// backward compatibility
		restoreOldSettings();
		useProjectSettingsButton.setSelection(isUseProjectSettings());
	}

	protected void handleUseProjectSettings() {
		boolean isUseProjectSettings = useProjectSettingsButton.getSelection();
		link.setEnabled(!isUseProjectSettings);
		updateFieldEditors(isUseProjectSettings);
	}

	public boolean isPropertyPage() {
		return project != null;
	}

	/**
	 * Gets the 'useProjectSettings' flag in the project preferences.
	 * 
	 * @return true if the settings on this page are project specific
	 */
	private Boolean isUseProjectSettings() {
		return Boolean.valueOf(getPreferenceStore().getBoolean(useProjectSettingsPreferenceName()));
	}

	protected abstract String useProjectSettingsPreferenceName();

	protected abstract String preferencesId();

	protected abstract String preferencesPageId();

	private void restoreOldSettings() {
		if (isPropertyPage()) {
			QualifiedName oldKey = new QualifiedName(preferencesId(), useProjectSettingsPreferenceName());
			try {
				String oldValue = project.getPersistentProperty(oldKey);
				if (oldValue != null) {
					// remove old entry
					project.setPersistentProperty(oldKey, null);
					// if were true - save copy into project settings
					if (Boolean.valueOf(oldValue)) {
						saveUseProjectSettings(true);
					}
				}
			} catch (Exception e) {
			}
		}

	}

	private void saveUseProjectSettings(boolean isProjectSpecific) throws IOException {
		ScopedPreferenceStore store = (ScopedPreferenceStore) getPreferenceStore();
		store.setValue(useProjectSettingsPreferenceName(), Boolean.toString(isProjectSpecific));
		store.save();
	}

	protected void updateFieldEditors(boolean enabled) {
		Composite parent = getFieldEditorParent();
		for (FieldEditor editor : editors) {
			editor.load();
			editor.setEnabled(enabled, parent);
		}
		getDefaultsButton().setEnabled(enabled);
	}

	@Override
	protected void addField(FieldEditor editor) {
		editors.add(editor);
		super.addField(editor);
	}

	@Override
	public boolean performOk() {
		boolean retVal = super.performOk();
		if (retVal && isPropertyPage()) {
			try {
				saveUseProjectSettings(useProjectSettingsButton.getSelection());
			} catch (Exception e) {
				retVal = false;
			}
		}
		return retVal;
	}

}