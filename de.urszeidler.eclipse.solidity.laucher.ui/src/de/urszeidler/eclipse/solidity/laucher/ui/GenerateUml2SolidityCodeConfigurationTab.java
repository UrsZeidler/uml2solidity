/**
 * 
 */
package de.urszeidler.eclipse.solidity.laucher.ui;

import static de.urszeidler.eclipse.solidity.ui.preferences.PreferenceConstants.CONTRACT_FILE_HEADER;
import static de.urszeidler.eclipse.solidity.ui.preferences.PreferenceConstants.GENERATE_ABI;
import static de.urszeidler.eclipse.solidity.ui.preferences.PreferenceConstants.GENERATE_HTML;
import static de.urszeidler.eclipse.solidity.ui.preferences.PreferenceConstants.GENERATE_MARKDOWN;
import static de.urszeidler.eclipse.solidity.ui.preferences.PreferenceConstants.GENERATE_MIX;
import static de.urszeidler.eclipse.solidity.ui.preferences.PreferenceConstants.GENERATE_WEB3;
import static de.urszeidler.eclipse.solidity.ui.preferences.PreferenceConstants.GENERATION_TARGET;
import static de.urszeidler.eclipse.solidity.ui.preferences.PreferenceConstants.*;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.internal.ui.IInternalDebugUIConstants;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.debug.ui.DebugUITools;
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
import org.eclipse.ui.dialogs.ContainerSelectionDialog;
import org.eclipse.ui.dialogs.ResourceListSelectionDialog;

import de.urszeidler.eclipse.solidity.laucher.Activator;
import de.urszeidler.eclipse.solidity.laucher.core.GenerateUml2Solidity;
import de.urszeidler.eclipse.solidity.ui.preferences.PreferenceConstants;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.ModifyEvent;

/**
 * @author uzeidler
 *
 */
public class GenerateUml2SolidityCodeConfigurationTab extends AbstractUml2SolidityLaunchConfigurationTab {
	private Text modelText;
	private Text generationDirectoryText;
	private Text fileHeaderText;
	// private Text docDirectoryText;
	// private Text abiDirectoryText;
	private Button btnGenerateSolidityCode;
	private Button btnGenerateMixConfig;
	private Button btnGenerateMixHtml;
	// private Button btnGenerateMarkdownReport;
	// private Button btnGfenerateSingleAbi;
	private Text versionText;
	private Button btnVersionAbove;

	/*
	 * (nicht-Javadoc)
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
				ResourceListSelectionDialog resourceListSelectionDialog = new ResourceListSelectionDialog(getShell(),
						ResourcesPlugin.getWorkspace().getRoot(), IResource.FILE) {

					@Override
					public void create() {
						super.create();
						refresh(true);
					}

					@Override
					protected String adjustPattern() {
						String adjustPattern = super.adjustPattern();
						if (adjustPattern == null || adjustPattern.isEmpty())
							return "*.uml";

						return adjustPattern;
					}

				};
				resourceListSelectionDialog.open();
				Object[] result = resourceListSelectionDialog.getResult();
				if (result != null && result.length == 1) {
					IFile file = (IFile) result[0];
					modelText.setText(file.getFullPath().toString());
					IProject project = file.getProject();
					IResource member = project.findMember(generationDirectoryText.getText());
					if (member != null)
						generationDirectoryText.setText(member.getFullPath().toString());

					setDirty(true);
					updateLaunchConfigurationDialog();
				}
			}
		});
		btnSelect.setText("select");

		final Group grpSolidity = new Group(mainComposite, SWT.NONE);
		grpSolidity.setText("solidity");
		grpSolidity.setLayout(new GridLayout(3, false));
		GridData gd_grpSolidity = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_grpSolidity.widthHint = 87;
		grpSolidity.setLayoutData(gd_grpSolidity);

		btnGenerateSolidityCode = new Button(grpSolidity, SWT.CHECK);
		btnGenerateSolidityCode.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setDirty(true);
				updateLaunchConfigurationDialog();
				// grpSolidity.setEnabled(btnGenerateSolidityCode.getSelection());
			}
		});
		btnGenerateSolidityCode.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		btnGenerateSolidityCode.setText("generate Solidity code");
		new Label(grpSolidity, SWT.NONE);

		btnVersionAbove = new Button(grpSolidity, SWT.CHECK);
		btnVersionAbove.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				versionText.setEnabled(btnVersionAbove.getSelection());
				setDirty(true);
				updateLaunchConfigurationDialog();
			}
		});
		btnVersionAbove.setText("version above 0.4.0");

		versionText = new Text(grpSolidity, SWT.BORDER);
		versionText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

		Label lblNewLabel = new Label(grpSolidity, SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel.setText("generate to directory");

		generationDirectoryText = new Text(grpSolidity, SWT.BORDER);
		generationDirectoryText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Button btnNewButton = new Button(grpSolidity, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String p = generationDirectoryText.getText();
				IContainer initialRoot = toContainer(p);
				ContainerSelectionDialog containerSelectionDialog = new ContainerSelectionDialog(getShell(),
						initialRoot, false, "select contract folder");
				containerSelectionDialog.open();
				Object[] result = containerSelectionDialog.getResult();
				if (result != null && result.length == 1) {
					IPath container = (IPath) result[0];
					generationDirectoryText.setText(container.toString());
					setDirty(true);
					updateLaunchConfigurationDialog();
				}
			}
		});
		btnNewButton.setText("select");

		Label lblSolidityFileHeader = new Label(grpSolidity, SWT.NONE);
		lblSolidityFileHeader.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
		lblSolidityFileHeader.setText("solidity file header");

		fileHeaderText = new Text(grpSolidity, SWT.BORDER | SWT.MULTI);
		fileHeaderText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				setDirty(true);
				updateLaunchConfigurationDialog();
			}
		});
		GridData gd_text_2 = new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1);
		gd_text_2.heightHint = 115;
		fileHeaderText.setLayoutData(gd_text_2);

		btnGenerateMixConfig = new Button(grpSolidity, SWT.CHECK);
		btnGenerateMixConfig.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setDirty(true);
				updateLaunchConfigurationDialog();
			}
		});
		btnGenerateMixConfig.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
		btnGenerateMixConfig.setText("generate mix config");

		btnGenerateMixHtml = new Button(grpSolidity, SWT.CHECK);
		btnGenerateMixHtml.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setDirty(true);
				updateLaunchConfigurationDialog();
			}
		});
		btnGenerateMixHtml.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
		btnGenerateMixHtml.setText("generate mix html");

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
		configuration.setAttribute(GenerateUml2Solidity.MODEL_URI, "");

		configuration.setAttribute(GENERATE_CONTRACT_FILES, store.getBoolean(GENERATE_CONTRACT_FILES));
		configuration.setAttribute(GENERATION_TARGET, store.getString(GENERATION_TARGET));
		configuration.setAttribute(CONTRACT_FILE_HEADER, store.getString(CONTRACT_FILE_HEADER));
		configuration.setAttribute(GENERATE_MIX, store.getBoolean(GENERATE_MIX));
		configuration.setAttribute(GENERATE_WEB3, store.getBoolean(GENERATE_WEB3));
		configuration.setAttribute(ENABLE_VERSION, store.getBoolean(ENABLE_VERSION));
		configuration.setAttribute(VERSION_PRAGMA, store.getString(VERSION_PRAGMA));
	}

	/*
	 * (nicht-Javadoc)
	 * 
	 * @see
	 * org.eclipse.debug.ui.ILaunchConfigurationTab#initializeFrom(org.eclipse.
	 * debug.core.ILaunchConfiguration)
	 */
	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		IPreferenceStore store = PreferenceConstants.getPreferenceStore(null);
		try {
			btnGenerateSolidityCode.setSelection(
					configuration.getAttribute(GENERATE_CONTRACT_FILES, store.getBoolean(GENERATE_CONTRACT_FILES)));
			btnGenerateMixConfig.setSelection(configuration.getAttribute(GENERATE_MIX, store.getBoolean(GENERATE_MIX)));
			btnGenerateMixHtml.setSelection(configuration.getAttribute(GENERATE_HTML, store.getBoolean(GENERATE_HTML)));
			modelText.setText(configuration.getAttribute(GenerateUml2Solidity.MODEL_URI, ""));
			fileHeaderText
					.setText(configuration.getAttribute(CONTRACT_FILE_HEADER, store.getString(CONTRACT_FILE_HEADER)));
			generationDirectoryText
					.setText(configuration.getAttribute(GENERATION_TARGET, store.getString(GENERATION_TARGET)));
			versionText.setText(configuration.getAttribute(VERSION_PRAGMA, store.getString(VERSION_PRAGMA)));
			versionText.setEnabled(configuration.getAttribute(ENABLE_VERSION, store.getBoolean(ENABLE_VERSION)));

			IResource resource = findResource(configuration, generationDirectoryText.getText());
			if (resource != null)
				generationDirectoryText.setText(resource.getFullPath().toString());

		} catch (CoreException e) {
			Activator.logError("Error initalizing from LauncheConfig", e);
		}

	}

	/*
	 * (nicht-Javadoc)
	 * 
	 * @see
	 * org.eclipse.debug.ui.ILaunchConfigurationTab#performApply(org.eclipse.
	 * debug.core.ILaunchConfigurationWorkingCopy)
	 */
	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(GenerateUml2Solidity.MODEL_URI, modelText.getText());
		configuration.setAttribute(CONTRACT_FILE_HEADER, fileHeaderText.getText());
		configuration.setAttribute(GENERATE_MIX, btnGenerateMixConfig.getSelection());
		configuration.setAttribute(GENERATE_HTML, btnGenerateMixHtml.getSelection());
		configuration.setAttribute(GENERATE_CONTRACT_FILES, btnGenerateSolidityCode.getSelection());
		configuration.setAttribute(GENERATION_TARGET, generationDirectoryText.getText());
		configuration.setAttribute(CONTRACT_FILE_HEADER, fileHeaderText.getText());

		configuration.setAttribute(ENABLE_VERSION, btnVersionAbove.getSelection());
		configuration.setAttribute(VERSION_PRAGMA, versionText.getText());
	}

	/*
	 * (nicht-Javadoc)
	 * 
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#getName()
	 */
	@Override
	public String getName() {
		return "generate Solidity";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#getImage()
	 */
	@Override
	public Image getImage() {
		return Activator.getDefault().getImageRegistry().get("UML2Solidity");
	}

}
