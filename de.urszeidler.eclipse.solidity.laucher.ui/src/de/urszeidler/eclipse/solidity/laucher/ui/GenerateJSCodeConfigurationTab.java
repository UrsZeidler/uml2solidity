package de.urszeidler.eclipse.solidity.laucher.ui;

import static de.urszeidler.eclipse.solidity.ui.preferences.PreferenceConstants.GENERATE_JS_CONTROLLER;
import static de.urszeidler.eclipse.solidity.ui.preferences.PreferenceConstants.GENERATE_JS_CONTROLLER_TARGET;
import static de.urszeidler.eclipse.solidity.ui.preferences.PreferenceConstants.GENERATE_JS_TEST;
import static de.urszeidler.eclipse.solidity.ui.preferences.PreferenceConstants.GENERATE_JS_TEST_TARGET;
import static de.urszeidler.eclipse.solidity.ui.preferences.PreferenceConstants.GENERATE_WEB3;
import static de.urszeidler.eclipse.solidity.ui.preferences.PreferenceConstants.JS_FILE_HEADER;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jface.preference.IPreferenceStore;
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
import org.eclipse.swt.widgets.Text;

import de.urszeidler.eclipse.solidity.laucher.Activator;
import de.urszeidler.eclipse.solidity.ui.preferences.PreferenceConstants;

/**
 * @author uzeidler
 *
 */
public class GenerateJSCodeConfigurationTab extends AbstractUml2SolidityLaunchConfigurationTab {
	private Text jsDirectoryText;
	private Text testDirectoryText;
	private Text jsHeaderText;
	private Button btnGenerateJsCode;
	private Button btnGenerateJsTestcode;
	private Button btnGenerateWeb3;

	/**
	 * @wbp.parser.entryPoint
	 */
	@Override
	public void createControl(Composite parent) {
		Composite mainComposite = new Composite(parent, SWT.NONE);
		mainComposite.setLayout(new GridLayout(1, true));
		setControl(mainComposite);

		Group grpGenerateJsCode = new Group(mainComposite, SWT.NONE);
		grpGenerateJsCode.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		grpGenerateJsCode.setText("generate js code");
		grpGenerateJsCode.setLayout(new GridLayout(3, false));

		btnGenerateJsCode = new Button(grpGenerateJsCode, SWT.CHECK);
		btnGenerateJsCode.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				validatePage();
			}
		});
		btnGenerateJsCode.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		btnGenerateJsCode.setText("generate js code");
		new Label(grpGenerateJsCode, SWT.NONE);
		
		btnGenerateWeb3 = new Button(grpGenerateJsCode, SWT.CHECK);
		btnGenerateWeb3.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				validatePage();
			}
		});
		btnGenerateWeb3.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		btnGenerateWeb3.setText("generate web3 js");
		new Label(grpGenerateJsCode, SWT.NONE);

		Label lblDirectoryForJs = new Label(grpGenerateJsCode, SWT.NONE);
		lblDirectoryForJs.setText("directory for js code");

		jsDirectoryText = new Text(grpGenerateJsCode, SWT.BORDER);
		jsDirectoryText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Button btnSelect = new Button(grpGenerateJsCode, SWT.NONE);
		btnSelect.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleChooseContainer(jsDirectoryText, "select js folder");
			}
		});
		btnSelect.setText("select");

		btnGenerateJsTestcode = new Button(grpGenerateJsCode, SWT.CHECK);
		btnGenerateJsTestcode.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				validatePage();
			}
		});
		btnGenerateJsTestcode.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		btnGenerateJsTestcode.setText("generate js testcode");
		new Label(grpGenerateJsCode, SWT.NONE);

		Label lblNewLabel = new Label(grpGenerateJsCode, SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel.setText("directory for test code");

		testDirectoryText = new Text(grpGenerateJsCode, SWT.BORDER);
		testDirectoryText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Button btnNewButton = new Button(grpGenerateJsCode, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleChooseContainer(testDirectoryText, "select js test folder");
			}
		});
		btnNewButton.setText("select");

		Label lblJsFileHeader = new Label(grpGenerateJsCode, SWT.NONE);
		lblJsFileHeader.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
		lblJsFileHeader.setText("js file header");

		jsHeaderText = new Text(grpGenerateJsCode, SWT.BORDER | SWT.MULTI);
		jsHeaderText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				validatePage();
			}
		});
		GridData gd_text_2 = new GridData(SWT.FILL, SWT.TOP, true, false, 3, 1);
		gd_text_2.heightHint = 97;
		jsHeaderText.setLayoutData(gd_text_2);

	}

	/*
	 * (nicht-Javadoc)
	 * 
	 * @see
	 * org.eclipse.debug.ui.ILaunchConfigurationTab#setDefaults(org.eclipse.
	 * debug.core.ILaunchConfigurationWorkingCopy)
	 */
	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		IPreferenceStore store = PreferenceConstants.getPreferenceStore(null);

		configuration.setAttribute(JS_FILE_HEADER, store.getString(JS_FILE_HEADER));
		configuration.setAttribute(GENERATE_JS_CONTROLLER, store.getBoolean(GENERATE_JS_CONTROLLER));
		configuration.setAttribute(GENERATE_JS_CONTROLLER_TARGET, store.getString(GENERATE_JS_CONTROLLER_TARGET));
		configuration.setAttribute(GENERATE_JS_TEST, store.getBoolean(GENERATE_JS_TEST));
		configuration.setAttribute(GENERATE_JS_TEST_TARGET, store.getString(GENERATE_JS_TEST_TARGET));
		configuration.setAttribute(GENERATE_WEB3, store.getBoolean(GENERATE_WEB3));
	}

	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		IPreferenceStore store = PreferenceConstants.getPreferenceStore(null);
		try {
			jsHeaderText.setText(configuration.getAttribute(JS_FILE_HEADER, store.getString(JS_FILE_HEADER)));
			jsDirectoryText.setText(configuration.getAttribute(GENERATE_JS_CONTROLLER_TARGET,
					store.getString(GENERATE_JS_CONTROLLER_TARGET)));
			testDirectoryText.setText(
					configuration.getAttribute(GENERATE_JS_TEST_TARGET, store.getString(GENERATE_JS_TEST_TARGET)));
			btnGenerateJsCode.setSelection(configuration.getAttribute(GENERATE_JS_CONTROLLER, true));
			btnGenerateJsTestcode.setSelection(configuration.getAttribute(GENERATE_JS_TEST, true));
			btnGenerateWeb3.setSelection(configuration.getAttribute(GENERATE_WEB3,store.getBoolean(GENERATE_WEB3)));

			IResource member = findResource(configuration, jsDirectoryText.getText());
			if (member != null)
				jsDirectoryText.setText(member.getFullPath().toString());
			member = findResource(configuration, testDirectoryText.getText());
			if (member != null)
				testDirectoryText.setText(member.getFullPath().toString());

		} catch (CoreException e) {
			Activator.logError("Error initalizing from LauncheConfig", e);
		}

	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(GENERATE_JS_CONTROLLER, btnGenerateJsCode.getSelection());
		configuration.setAttribute(GENERATE_JS_CONTROLLER_TARGET, jsDirectoryText.getText());
		configuration.setAttribute(GENERATE_JS_TEST, btnGenerateJsTestcode.getSelection());
		configuration.setAttribute(GENERATE_JS_TEST_TARGET, testDirectoryText.getText());
		configuration.setAttribute(JS_FILE_HEADER, jsHeaderText.getText());
		configuration.setAttribute(GENERATE_WEB3, btnGenerateWeb3.getSelection());
	}

	@Override
	protected void validatePage() {
		StringBuffer b = new StringBuffer();
		if(btnGenerateJsCode.getSelection()){
			if (jsDirectoryText.getText() == null || jsDirectoryText.getText().isEmpty()) {
				b.append("select a container where to store the js files.\n");
			}
		}
		if(btnGenerateJsTestcode.getSelection()){
			if (testDirectoryText.getText() == null || testDirectoryText.getText().isEmpty()) {
				b.append("select a container where to store the js tests.\n");
			}
		}
		
		if (b.length() != 0)
			setErrorMessage(b.toString());
		else
			setErrorMessage(null);

		super.validatePage();
	}
	
	@Override
	public String getName() {
		return "generate js code";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#getImage()
	 */
	@Override
	public Image getImage() {
		return Activator.getDefault().getImageRegistry().get("JsCode");
	}

}
