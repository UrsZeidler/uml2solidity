/**
 * 
 */
package de.urszeidler.eclipse.solidity.laucher.ui;

import static de.urszeidler.eclipse.solidity.ui.preferences.PreferenceConstants.GENERATE_JAVA_INTERFACE;
import static de.urszeidler.eclipse.solidity.ui.preferences.PreferenceConstants.GENERATE_JAVA_TESTS;
import static de.urszeidler.eclipse.solidity.ui.preferences.PreferenceConstants.GENERATION_JAVA_2_SOLIDITY_TYPES;
import static de.urszeidler.eclipse.solidity.ui.preferences.PreferenceConstants.GENERATION_JAVA_2_SOLIDITY_TYPE_PREFIX;
import static de.urszeidler.eclipse.solidity.ui.preferences.PreferenceConstants.GENERATION_JAVA_INTERFACE_PACKAGE_PREFIX;
import static de.urszeidler.eclipse.solidity.ui.preferences.PreferenceConstants.GENERATION_JAVA_INTERFACE_TARGET;
import static de.urszeidler.eclipse.solidity.ui.preferences.PreferenceConstants.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import de.urszeidler.eclipse.solidity.laucher.Activator;
import de.urszeidler.eclipse.solidity.ui.preferences.PreferenceConstants;

/**
 * @author urs
 *
 */
public class GenerateJavaCodeConfigurationTab extends AbstractUml2SolidityLaunchConfigurationTab {
	private final class EditingSupportExtension extends EditingSupport {
		private TextCellEditor editor;
		private boolean key = true;

		private EditingSupportExtension(TableViewer viewer, boolean key) {
			super(viewer);
			this.editor = new TextCellEditor(viewer.getTable());
			this.key = key;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void setValue(Object element, Object value) {
			Entry<String, String> entry = (Entry<String, String>) element;
			if (key) {
				if (entry.getKey().equals(value))
					return;

				String val = java2solTypes.remove(entry.getKey());
				java2solTypes.put((String) value, val);
				tableViewer.refresh();
				setDirty(true);
				updateLaunchConfigurationDialog();
			} else {
				if(entry.getValue().equals(value)) return;
				
				java2solTypes.put(entry.getKey(), (String) value);
				tableViewer.refresh();
				setDirty(true);
				updateLaunchConfigurationDialog();
			}
		}

		@SuppressWarnings("unchecked")
		@Override
		protected Object getValue(Object element) {
			if(key)
			return ((Entry<String, String>) element).getKey();
			else
				return ((Entry<String, String>) element).getValue();
		}

		@Override
		protected CellEditor getCellEditor(Object element) {
			return editor;
		}

		@Override
		protected boolean canEdit(Object element) {
			return true;
		}
	}

	private class TableLabelProvider extends LabelProvider implements ITableLabelProvider {
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		@SuppressWarnings("rawtypes")
		public String getColumnText(Object element, int columnIndex) {
			if (element instanceof Object[]) {
				Object[] ar = (Object[]) element;
				return ar[columnIndex].toString();
			} else if (element instanceof Map.Entry) {
				Map.Entry me = (Map.Entry) element;
				if (columnIndex == 0)
					return (String) me.getKey();
				else
					return (String) me.getValue();
			}
			return element.toString();
		}
	}

	private static class ContentProvider implements IStructuredContentProvider {
		@SuppressWarnings("unchecked")
		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof Map) {
				Set<?> entrySet = ((Map<?, ?>) inputElement).entrySet();
				return entrySet.toArray();
			}
			if (inputElement instanceof Map.Entry) {
				Map.Entry<String, String> e = ((Map.Entry<String, String>) inputElement);
				return new Object[] { e.getKey(), e.getValue() };
			}
			return new Object[0];
		}

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			viewer.refresh();
		}
	}

	private Text base_target_text;
	private Text package_prefix_tex;
	private Button btnGenerateJava;
	private Table table;

	private Map<String, String> java2solTypes = new HashMap<>();
	private TableViewer tableViewer;
	private Text base_Test_text;
	private Button btnGenerateTestCode;
	private Button btnGenerateJavaNoneBlocking;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.debug.ui.ILaunchConfigurationTab#createControl(org.eclipse.
	 * swt.widgets.Composite)
	 */
	/**
	 * @wbp.parser.entryPoint
	 */
	@Override
	public void createControl(Composite parent) {
		Composite mainComposite = new Composite(parent, SWT.NONE);
		mainComposite.setLayout(new GridLayout(1, true));
		setControl(mainComposite);

		Group grpJavaInterface = new Group(mainComposite, SWT.NONE);
		GridData gd_grpJavaInterface = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_grpJavaInterface.widthHint = 426;
		grpJavaInterface.setLayoutData(gd_grpJavaInterface);
		grpJavaInterface.setText("java interface");
		grpJavaInterface.setLayout(new GridLayout(3, false));

		btnGenerateJava = new Button(grpJavaInterface, SWT.CHECK);
		btnGenerateJava.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				validatePage();
			}
		});
		btnGenerateJava.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		btnGenerateJava.setText("generate Java interfaces");
		new Label(grpJavaInterface, SWT.NONE);

		Label lblNewLabel = new Label(grpJavaInterface, SWT.NONE);
		lblNewLabel.setText("base target");

		base_target_text = new Text(grpJavaInterface, SWT.BORDER);
		base_target_text.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				validatePage();
			}
		});
		base_target_text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Button btnNewButton = new Button(grpJavaInterface, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleChooseContainer(base_target_text, "select source container");
			}
		});
		btnNewButton.setText("select");

		Label lblNewLabel_1 = new Label(grpJavaInterface, SWT.NONE);
		lblNewLabel_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_1.setText("package prefix");

		package_prefix_tex = new Text(grpJavaInterface, SWT.BORDER);
		package_prefix_tex.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				validatePage();
			}
		});
		package_prefix_tex.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		
		btnGenerateJavaNoneBlocking = new Button(grpJavaInterface, SWT.CHECK);
		btnGenerateJavaNoneBlocking.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				validatePage();
			}
		});
		btnGenerateJavaNoneBlocking.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
		btnGenerateJavaNoneBlocking.setText("generate java void as Completable<void>");
		
		Group group = new Group(mainComposite, SWT.NONE);
		group.setLayout(new GridLayout(3, false));
		group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		btnGenerateTestCode = new Button(group, SWT.CHECK);
		btnGenerateTestCode.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				validatePage();
			}
		});
		btnGenerateTestCode.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		btnGenerateTestCode.setText("generate java tests");
		new Label(group, SWT.NONE);
		
		Label lblNewLabel_2 = new Label(group, SWT.NONE);
		lblNewLabel_2.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_2.setText("base target");
		
		base_Test_text = new Text(group, SWT.BORDER);
		base_Test_text.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				validatePage();
			}
		});
		base_Test_text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Button btnNewButton_3 = new Button(group, SWT.NONE);
		btnNewButton_3.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleChooseContainer(base_Test_text, "select test container");
			}
		});
		btnNewButton_3.setText("select");

		Group grpTypeMapping = new Group(mainComposite, SWT.NONE);
		grpTypeMapping.setLayout(new GridLayout(1, false));
		GridData gd_grpTypeMapping = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_grpTypeMapping.heightHint = 210;
		grpTypeMapping.setLayoutData(gd_grpTypeMapping);
		grpTypeMapping.setText("type mapping");

		tableViewer = new TableViewer(grpTypeMapping, SWT.BORDER | SWT.FULL_SELECTION);
		tableViewer.setColumnProperties(new String[] {});

		table = tableViewer.getTable();
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		GridData gd_table = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_table.heightHint = 155;
		table.setLayoutData(gd_table);

		TableViewerColumn tableViewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnNewColumn = tableViewerColumn.getColumn();
		tblclmnNewColumn.setWidth(100);
		tblclmnNewColumn.setText("solidity type");
		EditingSupport editingSupport = new EditingSupportExtension(tableViewer,true);
		tableViewerColumn.setEditingSupport(editingSupport);

		TableViewerColumn tableViewerColumn_1 = new TableViewerColumn(tableViewer, SWT.NONE);
		tableViewerColumn_1.setEditingSupport(new EditingSupportExtension(tableViewer,false));
		TableColumn tblclmnNewColumn_1 = tableViewerColumn_1.getColumn();
		tblclmnNewColumn_1.setWidth(100);
		tblclmnNewColumn_1.setText("java type");

		Composite composite = new Composite(grpTypeMapping, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

		Button btnAdd = new Button(composite, SWT.NONE);
		btnAdd.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				java2solTypes.put("new sol type", "java type");
				tableViewer.refresh();
				validatePage();
			}
		});
		btnAdd.setText("add");

		Button btnNewButton_2 = new Button(composite, SWT.NONE);
		btnNewButton_2.addSelectionListener(new SelectionAdapter() {
			@SuppressWarnings("unchecked")
			@Override
			public void widgetSelected(SelectionEvent e) {
				Object firstElement = tableViewer.getStructuredSelection().getFirstElement();
				if (firstElement instanceof Map.Entry) {
					Map.Entry<String,String > me = (Map.Entry<String, String>) firstElement;
					java2solTypes.remove(me.getKey());
					tableViewer.refresh();
					validatePage();
				}
			}
		});
		btnNewButton_2.setText("del");
		tableViewer.setLabelProvider(new TableLabelProvider());
		tableViewer.setContentProvider(new ContentProvider());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.debug.ui.ILaunchConfigurationTab#setDefaults(org.eclipse.
	 * debug.core.ILaunchConfigurationWorkingCopy)
	 */
	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		IPreferenceStore store = PreferenceConstants.getPreferenceStore(null);

		configuration.setAttribute(GENERATE_JAVA_INTERFACE, store.getBoolean(GENERATE_JAVA_INTERFACE));
		configuration.setAttribute(GENERATE_JAVA_NONBLOCKING, store.getBoolean(GENERATE_JAVA_NONBLOCKING));
		configuration.setAttribute(GENERATION_JAVA_INTERFACE_TARGET, store.getString(GENERATION_JAVA_INTERFACE_TARGET));
		configuration.setAttribute(GENERATION_JAVA_INTERFACE_PACKAGE_PREFIX,
				store.getString(GENERATION_JAVA_INTERFACE_PACKAGE_PREFIX));

		configuration.setAttribute(GENERATE_JAVA_TESTS, store.getBoolean(GENERATE_JAVA_TESTS));
		configuration.setAttribute(GENERATION_JAVA_TEST_TARGET, store.getString(GENERATION_JAVA_TEST_TARGET));
		
		String types = store.getString(GENERATION_JAVA_2_SOLIDITY_TYPES);
		if (types != null) {
			String[] split = types.split(",");
			for (int i = 0; i < split.length; i++) {
				String t = split[i];
				configuration.setAttribute(GENERATION_JAVA_2_SOLIDITY_TYPE_PREFIX + t,
						store.getString(GENERATION_JAVA_2_SOLIDITY_TYPE_PREFIX + t));
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.debug.ui.ILaunchConfigurationTab#initializeFrom(org.eclipse.
	 * debug.core.ILaunchConfiguration)
	 */
	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		IPreferenceStore store = PreferenceConstants.getPreferenceStore(null);
		try {
			btnGenerateJava.setSelection(
					configuration.getAttribute(GENERATE_JAVA_INTERFACE, store.getBoolean(GENERATE_JAVA_INTERFACE)));
			btnGenerateJavaNoneBlocking.setSelection(
					configuration.getAttribute(GENERATE_JAVA_NONBLOCKING, store.getBoolean(GENERATE_JAVA_NONBLOCKING)));
			base_target_text.setText(configuration.getAttribute(GENERATION_JAVA_INTERFACE_TARGET,
					store.getString(GENERATION_JAVA_INTERFACE_TARGET)));
			package_prefix_tex.setText(configuration.getAttribute(GENERATION_JAVA_INTERFACE_PACKAGE_PREFIX,
					store.getString(GENERATION_JAVA_INTERFACE_PACKAGE_PREFIX)));
			btnGenerateTestCode.setSelection(configuration.getAttribute(GENERATE_JAVA_TESTS, store.getBoolean(GENERATE_JAVA_TESTS)));
			base_Test_text.setText(configuration.getAttribute(GENERATION_JAVA_TEST_TARGET, store.getString(GENERATION_JAVA_TEST_TARGET)));
			
			IResource member = findResource(configuration, base_target_text.getText());
			if (member != null)
				base_target_text.setText(member.getFullPath().toString());

			java2solTypes.clear();
			Map<String, Object> attributes = configuration.getAttributes();
			for (Entry<String, Object> e : attributes.entrySet()) {
				if (e.getKey().startsWith(GENERATION_JAVA_2_SOLIDITY_TYPE_PREFIX)) {
					String sol_type = e.getKey().substring(GENERATION_JAVA_2_SOLIDITY_TYPE_PREFIX.length());
					java2solTypes.put(sol_type, (String) e.getValue());
				}
			}
			tableViewer.setInput(java2solTypes);
		} catch (CoreException e) {
			Activator.logError("Error initalizing from LauncheConfig", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.debug.ui.ILaunchConfigurationTab#performApply(org.eclipse.
	 * debug.core.ILaunchConfigurationWorkingCopy)
	 */
	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(GENERATE_JAVA_INTERFACE, btnGenerateJava.getSelection());
		configuration.setAttribute(GENERATE_JAVA_NONBLOCKING, btnGenerateJavaNoneBlocking.getSelection());
		configuration.setAttribute(GENERATION_JAVA_INTERFACE_TARGET, base_target_text.getText());
		configuration.setAttribute(GENERATION_JAVA_INTERFACE_PACKAGE_PREFIX, package_prefix_tex.getText());
		
		configuration.setAttribute(GENERATE_JAVA_TESTS, btnGenerateTestCode.getSelection());
		configuration.setAttribute(GENERATION_JAVA_TEST_TARGET, base_Test_text.getText());

		for (Entry<String, String> e : java2solTypes.entrySet()) {
			configuration.setAttribute(GENERATION_JAVA_2_SOLIDITY_TYPE_PREFIX + e.getKey(), e.getValue());
		}
	}

	@Override
	protected void validatePage() {
		StringBuffer b = new StringBuffer();
		if (btnGenerateJava.getSelection()) {
			if (package_prefix_tex.getText() == null || package_prefix_tex.getText().isEmpty()) {
				b.append("Set apackage prefix.\n");
			}
			if (base_Test_text.getText() == null || base_target_text.getText().isEmpty()) {
				b.append("select a container where to store the contract interfaces.\n");
			}
		}
		if(btnGenerateTestCode.getSelection()){
			if (base_Test_text.getText() == null || base_Test_text.getText().isEmpty()) {
				b.append("select a container where to store the contract tests.\n");
			}
		}
		if (b.length() != 0)
			setErrorMessage(b.toString());
		else
			setErrorMessage(null);
		super.validatePage();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#getName()
	 */
	@Override
	public String getName() {
		return "generate java";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#getImage()
	 */
	@Override
	public Image getImage() {
		return Activator.getDefault().getImageRegistry().get("JavaCode");
	}

}
