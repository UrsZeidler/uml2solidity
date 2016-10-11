/**
 * 
 */
package de.urszeidler.eclipse.solidity.laucher.ui;



import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;
import org.eclipse.ui.dialogs.ResourceListSelectionDialog;

import de.urszeidler.eclipse.solidity.laucher.core.GenerateUml2Solidity;
import de.urszeidler.eclipse.solidity.ui.preferences.PreferenceConstants;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

/**
 * @author uzeidler
 *
 */
public class GenerateSolidityCodeConfigurationTab extends AbstractLaunchConfigurationTab {
	private Text modelText;
	private Text generationDirectoryText;
	private Text fileHeaderText;
	private Text docDirectoryText;
	private Text abiDirectoryText;
	private Button btnGenerateSolidityCode;
	private Button btnGenerateMixConfig;
	private Button btnGenerateMixHtml;
	private Button btnGenerateMarkdownReport;
	private Button btnGfenerateSingleAbi;

	/* (nicht-Javadoc)
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#createControl(org.eclipse.swt.widgets.Composite)
	 */
	/**
	 * @wbp.parser.entryPoint
	 */
	@SuppressWarnings("restriction")
	@Override
	public void createControl(Composite parent) {
		Composite mainComposite = new Composite(parent, SWT.NONE);
		mainComposite.setLayout(new GridLayout(1, true));		
		setControl(mainComposite);
		
		Group grpModel = new Group(mainComposite, SWT.NONE);
		grpModel.setLayout(new GridLayout(3, false));
		grpModel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		grpModel.setText("model");
		
		Label lblModel = new Label(grpModel, SWT.NONE);
		lblModel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblModel.setText("model");
		
		modelText = new Text(grpModel, SWT.BORDER);
		modelText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Button btnSelect = new Button(grpModel, SWT.NONE);
		btnSelect.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String filePath = modelText.getText();
				ResourceListSelectionDialog resourceListSelectionDialog = new ResourceListSelectionDialog(getShell(), null);
				resourceListSelectionDialog.open();
				Object[] result2 = resourceListSelectionDialog.getResult();
				
				ContainerSelectionDialog containerSelectionDialog = new ContainerSelectionDialog(getShell(), ResourcesPlugin.getWorkspace().getRoot(), false, "");
//				FileDialog dialog = new FileDialog(getShell(), SWT.SAVE);
//				dialog.setText("Select a callchain model.");
				containerSelectionDialog.open();
				
//				filePath = dialog.open();
				if (filePath != null) {
					Object[] result = containerSelectionDialog.getResult();
					modelText.setText((String) result[0]);
				}
				
			}
		});
		btnSelect.setText("select");
		
		Group grpSolidity = new Group(mainComposite, SWT.NONE);
		grpSolidity.setText("solidity");
		grpSolidity.setLayout(new GridLayout(3, false));
		GridData gd_grpSolidity = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_grpSolidity.widthHint = 87;
		grpSolidity.setLayoutData(gd_grpSolidity);
		
		btnGenerateSolidityCode = new Button(grpSolidity, SWT.CHECK);
		btnGenerateSolidityCode.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		btnGenerateSolidityCode.setText("generate Solidity code");
		new Label(grpSolidity, SWT.NONE);
		
		Label lblNewLabel = new Label(grpSolidity, SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel.setText("generate to directory");
		
		generationDirectoryText = new Text(grpSolidity, SWT.BORDER);
		generationDirectoryText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Button btnNewButton = new Button(grpSolidity, SWT.NONE);
		btnNewButton.setText("select");
		
		Label lblSolidityFileHeader = new Label(grpSolidity, SWT.NONE);
		lblSolidityFileHeader.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
		lblSolidityFileHeader.setText("solidity file header");
		
		fileHeaderText = new Text(grpSolidity, SWT.BORDER | SWT.MULTI);
		GridData gd_text_2 = new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1);
		gd_text_2.heightHint = 115;
		fileHeaderText.setLayoutData(gd_text_2);
		
		btnGenerateMixConfig = new Button(grpSolidity, SWT.CHECK);
		btnGenerateMixConfig.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
		btnGenerateMixConfig.setText("generate mix config");
		
		btnGenerateMixHtml = new Button(grpSolidity, SWT.CHECK);
		btnGenerateMixHtml.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
		btnGenerateMixHtml.setText("generate mix html");
		
		Group grpDocumentation = new Group(mainComposite, SWT.NONE);
		grpDocumentation.setLayout(new GridLayout(3, false));
		grpDocumentation.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		grpDocumentation.setText("documentation");
		
		btnGenerateMarkdownReport = new Button(grpDocumentation, SWT.CHECK);
		btnGenerateMarkdownReport.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		btnGenerateMarkdownReport.setText("generate markdown report");
		new Label(grpDocumentation, SWT.NONE);
		
		Label lblNewLabel_1 = new Label(grpDocumentation, SWT.NONE);
		lblNewLabel_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_1.setText("generate doc directory");
		
		docDirectoryText = new Text(grpDocumentation, SWT.BORDER);
		docDirectoryText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Button btnNewButton_1 = new Button(grpDocumentation, SWT.NONE);
		btnNewButton_1.setText("select");
		
		Group grpAbi = new Group(mainComposite, SWT.NONE);
		grpAbi.setLayout(new GridLayout(3, false));
		grpAbi.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		grpAbi.setText("abi");
		
		btnGfenerateSingleAbi = new Button(grpAbi, SWT.CHECK);
		btnGfenerateSingleAbi.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
		btnGfenerateSingleAbi.setText("generate single abi file");
		
		Label lblDirectory = new Label(grpAbi, SWT.NONE);
		lblDirectory.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblDirectory.setText("directory");
		
		abiDirectoryText = new Text(grpAbi, SWT.BORDER);
		abiDirectoryText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Button btnSelect_1 = new Button(grpAbi, SWT.NONE);
		btnSelect_1.setText("select");
		
		

	}

	/* (nicht-Javadoc)
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#setDefaults(org.eclipse.debug.core.ILaunchConfigurationWorkingCopy)
	 */
	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(GenerateUml2Solidity.MODEL_URI, "");
		configuration.setAttribute(PreferenceConstants.CONTRACT_FILE_HEADER, "");
		configuration.setAttribute(PreferenceConstants.GENERATE_ABI, true);
		configuration.setAttribute(PreferenceConstants.GENERATE_MARKDOWN, true);
		configuration.setAttribute(PreferenceConstants.GENERATE_MIX, true);
		configuration.setAttribute(PreferenceConstants.GENERATE_WEB3, true);
		configuration.setAttribute(PreferenceConstants.GENERATION_TARGET, "");
		configuration.setAttribute(PreferenceConstants.GENERATION_TARGET_DOC, "");
		

	}

	/* (nicht-Javadoc)
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#initializeFrom(org.eclipse.debug.core.ILaunchConfiguration)
	 */
	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		try {
			btnGenerateMixConfig.setSelection(configuration.getAttribute(PreferenceConstants.GENERATE_MIX, true));
			btnGenerateMixHtml.setSelection(configuration.getAttribute(PreferenceConstants.GENERATE_HTML, true));
			btnGenerateMarkdownReport.setSelection(configuration.getAttribute(PreferenceConstants.GENERATE_MARKDOWN, true));
//			btnGenerateSolidityCode.set
			btnGfenerateSingleAbi.setSelection(configuration.getAttribute(PreferenceConstants.GENERATE_ABI, true));
			fileHeaderText.setText(configuration.getAttribute(PreferenceConstants.CONTRACT_FILE_HEADER, ""));
			modelText.setText(configuration.getAttribute(GenerateUml2Solidity.MODEL_URI, ""));
			generationDirectoryText.setText(configuration.getAttribute("", ""));
			docDirectoryText.setText(configuration.getAttribute(PreferenceConstants.GENERATION_TARGET_DOC, ""));
			
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/* (nicht-Javadoc)
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#performApply(org.eclipse.debug.core.ILaunchConfigurationWorkingCopy)
	 */
	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		// TODO Automatisch generierter Methodenstub

	}

	/* (nicht-Javadoc)
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#getName()
	 */
	@Override
	public String getName() {
		return "generate Solidity";
	}
}
