/**
 * 
 */
package de.urszeidler.eclipse.solidity.laucher.ui;

import static de.urszeidler.eclipse.solidity.ui.preferences.PreferenceConstants.GENERATE_ABI;
import static de.urszeidler.eclipse.solidity.ui.preferences.PreferenceConstants.GENERATE_ABI_TARGET;
import static de.urszeidler.eclipse.solidity.ui.preferences.PreferenceConstants.GENERATE_MARKDOWN;
import static de.urszeidler.eclipse.solidity.ui.preferences.PreferenceConstants.GENERATION_TARGET_DOC;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
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
 * @author urs
 *
 */
public class GenerateOthersConfigurationTab extends AbstractUml2SolidityLaunchConfigurationTab {

	private Button btnGenerateMarkdownReport;
	private Text docDirectoryText;
	private Button btnGenerateSingleAbi;
	private Text abiDirectoryText;

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
		Group grpDocumentation = new Group(mainComposite, SWT.NONE);
		grpDocumentation.setLayout(new GridLayout(3, false));
		grpDocumentation.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		grpDocumentation.setText("documentation");

		btnGenerateMarkdownReport = new Button(grpDocumentation, SWT.CHECK);
		btnGenerateMarkdownReport.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setDirty(true);
				updateLaunchConfigurationDialog();
			}
		});
		btnGenerateMarkdownReport.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		btnGenerateMarkdownReport.setText("generate markdown report");
		new Label(grpDocumentation, SWT.NONE);

		Label lblNewLabel_1 = new Label(grpDocumentation, SWT.NONE);
		lblNewLabel_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_1.setText("generate doc directory");

		docDirectoryText = new Text(grpDocumentation, SWT.BORDER);
		docDirectoryText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Button btnNewButton_1 = new Button(grpDocumentation, SWT.NONE);
		btnNewButton_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleChooseContainer(docDirectoryText, "select doc folder");
			}
		});
		btnNewButton_1.setText("select");

		Group grpAbi = new Group(mainComposite, SWT.NONE);
		grpAbi.setLayout(new GridLayout(3, false));
		grpAbi.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		grpAbi.setText("abi");

		btnGenerateSingleAbi = new Button(grpAbi, SWT.CHECK);
		btnGenerateSingleAbi.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				validatePage();
			}
		});
		btnGenerateSingleAbi.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
		btnGenerateSingleAbi.setText("generate single abi file");

		Label lblDirectory = new Label(grpAbi, SWT.NONE);
		lblDirectory.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblDirectory.setText("directory");

		abiDirectoryText = new Text(grpAbi, SWT.BORDER);
		abiDirectoryText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Button btnSelect_1 = new Button(grpAbi, SWT.NONE);
		btnSelect_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleChooseContainer(abiDirectoryText, "select abi folder");
			}
		});
		btnSelect_1.setText("select");
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

		configuration.setAttribute(GENERATE_ABI, store.getBoolean(GENERATE_ABI));
		configuration.setAttribute(GENERATE_MARKDOWN, store.getBoolean(GENERATE_MARKDOWN));
		configuration.setAttribute(GENERATION_TARGET_DOC, store.getString(GENERATION_TARGET_DOC));
		configuration.setAttribute(GENERATE_ABI_TARGET, store.getString(GENERATE_ABI_TARGET));

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
			btnGenerateMarkdownReport.setSelection(configuration.getAttribute(GENERATE_MARKDOWN, store.getBoolean(GENERATE_MARKDOWN)));
			btnGenerateSingleAbi.setSelection(configuration.getAttribute(GENERATE_ABI, store.getBoolean(GENERATE_ABI)));
			docDirectoryText.setText(configuration.getAttribute(GENERATION_TARGET_DOC, store.getString(GENERATION_TARGET_DOC)));
			abiDirectoryText.setText(configuration.getAttribute(GENERATE_ABI_TARGET, store.getString(GENERATE_ABI_TARGET)));
			
			IResource member = findResource(configuration, docDirectoryText.getText());
			if (member != null)
				docDirectoryText.setText(member.getFullPath().toString());
			member = findResource(configuration, abiDirectoryText.getText());
			if (member != null)
				abiDirectoryText.setText(member.getFullPath().toString());

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
		configuration.setAttribute(GENERATE_ABI, btnGenerateSingleAbi.getSelection());
		configuration.setAttribute(GENERATE_MARKDOWN, btnGenerateMarkdownReport.getSelection());
		configuration.setAttribute(GENERATION_TARGET_DOC, docDirectoryText.getText());
		configuration.setAttribute(GENERATE_ABI_TARGET, abiDirectoryText.getText());

	}

	@Override
	protected void validatePage() {
		StringBuffer b = new StringBuffer();
		if(btnGenerateMarkdownReport.getSelection()){
			if (docDirectoryText.getText() == null || docDirectoryText.getText().isEmpty()) {
				b.append("select a container where to store the markdown report.\n");
			}
		}
		if(btnGenerateSingleAbi.getSelection()){
			if (abiDirectoryText.getText() == null || abiDirectoryText.getText().isEmpty()) {
				b.append("select a container where to store the abi files.\n");
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
		return "generate doc";
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#getImage()
	 */
	@Override
	public Image getImage() {
		return Activator.getDefault().getImageRegistry().get("OtherFiles");
	}

}
