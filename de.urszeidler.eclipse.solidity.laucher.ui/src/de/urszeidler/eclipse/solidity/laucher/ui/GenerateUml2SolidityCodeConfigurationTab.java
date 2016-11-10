/**
 * 
 */
package de.urszeidler.eclipse.solidity.laucher.ui;

import static de.urszeidler.eclipse.solidity.compiler.support.preferences.PreferenceConstants.COMPILER_PROGRAMM;
import static de.urszeidler.eclipse.solidity.compiler.support.preferences.PreferenceConstants.COMPILER_TARGET;
import static de.urszeidler.eclipse.solidity.compiler.support.preferences.PreferenceConstants.COMPILE_CONTRACTS;
import static de.urszeidler.eclipse.solidity.ui.preferences.PreferenceConstants.CONTRACT_FILE_HEADER;
import static de.urszeidler.eclipse.solidity.ui.preferences.PreferenceConstants.ENABLE_VERSION;
import static de.urszeidler.eclipse.solidity.ui.preferences.PreferenceConstants.GENERATE_CONTRACT_FILES;
import static de.urszeidler.eclipse.solidity.ui.preferences.PreferenceConstants.GENERATE_HTML;
import static de.urszeidler.eclipse.solidity.ui.preferences.PreferenceConstants.GENERATE_MIX;
import static de.urszeidler.eclipse.solidity.ui.preferences.PreferenceConstants.GENERATE_WEB3;
import static de.urszeidler.eclipse.solidity.ui.preferences.PreferenceConstants.GENERATION_TARGET;
import static de.urszeidler.eclipse.solidity.ui.preferences.PreferenceConstants.VERSION_PRAGMA;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
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
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ResourceListSelectionDialog;

import de.urszeidler.eclipse.solidity.laucher.Activator;
import de.urszeidler.eclipse.solidity.laucher.core.GenerateUml2Solidity;
import de.urszeidler.eclipse.solidity.ui.preferences.PreferenceConstants;

/**
 * @author uzeidler
 *
 */
public class GenerateUml2SolidityCodeConfigurationTab extends AbstractUml2SolidityLaunchConfigurationTab {
	private Text modelText;
	private Text generationDirectoryText;
	private Text fileHeaderText;
	private Button btnGenerateSolidityCode;
	private Button btnGenerateMixConfig;
	private Button btnGenerateMixHtml;
	private Text versionText;
	private Button btnVersionAbove;
	private Text compiler_text;
	private Text compiler_out_text;
	private Button btnCompile;
	private Map<String,Button> compileOptions = new HashMap<String,Button>(20);

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

					validatePage();
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
				validatePage();
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
				validatePage();
			}
		});
		btnVersionAbove.setText("version above 0.4.0");

		versionText = new Text(grpSolidity, SWT.BORDER);
		versionText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				validatePage();
			}
		});
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
				handleChooseContainer(generationDirectoryText, "select contract folder");
			}
		});
		btnNewButton.setText("select");

		Label lblSolidityFileHeader = new Label(grpSolidity, SWT.NONE);
		lblSolidityFileHeader.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
		lblSolidityFileHeader.setText("solidity file header");

		fileHeaderText = new Text(grpSolidity, SWT.BORDER | SWT.MULTI);
		fileHeaderText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				validatePage();
			}
		});
		GridData gd_text_2 = new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1);
		gd_text_2.heightHint = 115;
		fileHeaderText.setLayoutData(gd_text_2);

		btnGenerateMixConfig = new Button(grpSolidity, SWT.CHECK);
		btnGenerateMixConfig.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				validatePage();
			}
		});
		btnGenerateMixConfig.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
		btnGenerateMixConfig.setText("generate mix config");

		btnGenerateMixHtml = new Button(grpSolidity, SWT.CHECK);
		btnGenerateMixHtml.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				validatePage();
			}
		});
		btnGenerateMixHtml.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
		btnGenerateMixHtml.setText("generate mix html");
		
		Group grpCompile = new Group(mainComposite, SWT.NONE);
		grpCompile.setText("compile");
		grpCompile.setLayout(new GridLayout(3, false));
		grpCompile.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
		
		btnCompile = new Button(grpCompile, SWT.CHECK);
		btnCompile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				validatePage();
			}
		});
		btnCompile.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		btnCompile.setText("compile generated contracts");
		new Label(grpCompile, SWT.NONE);
		
		Label lblNewLabel_1 = new Label(grpCompile, SWT.NONE);
		lblNewLabel_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_1.setText("compiler:");
		
		compiler_text = new Text(grpCompile, SWT.BORDER);
		compiler_text.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				validatePage();
			}
		});
		compiler_text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		
		Label lblNewLabel_2 = new Label(grpCompile, SWT.NONE);
		lblNewLabel_2.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_2.setText("compiler output");
		
		compiler_out_text = new Text(grpCompile, SWT.BORDER);
		compiler_out_text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Button btnNewButton_1 = new Button(grpCompile, SWT.NONE);
		btnNewButton_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleChooseContainer(compiler_out_text, "select compiler output");
			}
		});
		btnNewButton_1.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		btnNewButton_1.setText("select");
		
		ExpandBar expandBar = new ExpandBar(grpCompile, SWT.NONE);
		expandBar.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 3, 1));
		
		ExpandItem xpndtmNewExpanditem = new ExpandItem(expandBar, SWT.NONE);
		xpndtmNewExpanditem.setExpanded(true);
		xpndtmNewExpanditem.setText("compiler options");
		
		Composite composite = new Composite(expandBar, SWT.NONE);
		xpndtmNewExpanditem.setControl(composite);
		composite.setLayout(new GridLayout(1, false));
		
		String[] compileSwitches = de.urszeidler.eclipse.solidity.compiler.support.preferences.PreferenceConstants.COMPILE_SWITCHES;
		for (String sw : compileSwitches) {
			Button btnCheckButton = new Button(composite, SWT.CHECK);
			btnCheckButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					validatePage();
				}
				
			});
			btnCheckButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
			btnCheckButton.setText(sw);
			compileOptions.put(sw,btnCheckButton);
		}
		xpndtmNewExpanditem.setHeight(xpndtmNewExpanditem.getControl().computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		

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
		IPreferenceStore store1 = de.urszeidler.eclipse.solidity.compiler.support.preferences.PreferenceConstants.getPreferenceStore(null);
		configuration.setAttribute(GenerateUml2Solidity.MODEL_URI, "");

		configuration.setAttribute(GENERATE_CONTRACT_FILES, store.getBoolean(GENERATE_CONTRACT_FILES));
		configuration.setAttribute(GENERATION_TARGET, store.getString(GENERATION_TARGET));
		configuration.setAttribute(CONTRACT_FILE_HEADER, store.getString(CONTRACT_FILE_HEADER));
		configuration.setAttribute(GENERATE_MIX, store.getBoolean(GENERATE_MIX));
		configuration.setAttribute(GENERATE_WEB3, store.getBoolean(GENERATE_WEB3));
		configuration.setAttribute(ENABLE_VERSION, store.getBoolean(ENABLE_VERSION));
		configuration.setAttribute(VERSION_PRAGMA, store.getString(VERSION_PRAGMA));
		
		configuration.setAttribute(COMPILE_CONTRACTS, store1.getBoolean(COMPILE_CONTRACTS));
		configuration.setAttribute(COMPILER_PROGRAMM, store1.getString(COMPILER_PROGRAMM));
		configuration.setAttribute(COMPILER_TARGET, store1.getString(COMPILER_TARGET));
		String[] compileSwitches = de.urszeidler.eclipse.solidity.compiler.support.preferences.PreferenceConstants.COMPILE_SWITCHES;
		for (String sw : compileSwitches) {
			configuration.setAttribute(sw, store1.getString(sw));
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
		IPreferenceStore store1 = de.urszeidler.eclipse.solidity.compiler.support.preferences.PreferenceConstants.getPreferenceStore(null);
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
			btnVersionAbove.setSelection(configuration.getAttribute(ENABLE_VERSION, store.getBoolean(ENABLE_VERSION)));
			versionText.setText(configuration.getAttribute(VERSION_PRAGMA, store.getString(VERSION_PRAGMA)));
			versionText.setEnabled(configuration.getAttribute(ENABLE_VERSION, store.getBoolean(ENABLE_VERSION)));

			btnCompile.setSelection(configuration.getAttribute(COMPILE_CONTRACTS, store1.getBoolean(COMPILE_CONTRACTS)));
			compiler_text.setText(configuration.getAttribute(COMPILER_PROGRAMM, store1.getString(COMPILER_PROGRAMM)));
			compiler_out_text.setText(configuration.getAttribute(COMPILER_TARGET, store1.getString(COMPILER_TARGET)));
			
			for (Entry<String, Button> e : compileOptions.entrySet()) {
				e.getValue().setSelection(configuration.getAttribute(e.getKey(), store1.getBoolean(e.getKey())));
			}
			
			IResource resource = findResource(configuration, generationDirectoryText.getText());
			if (resource != null)
				generationDirectoryText.setText(resource.getFullPath().toString());

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
		configuration.setAttribute(GenerateUml2Solidity.MODEL_URI, modelText.getText());
		configuration.setAttribute(CONTRACT_FILE_HEADER, fileHeaderText.getText());
		configuration.setAttribute(GENERATE_MIX, btnGenerateMixConfig.getSelection());
		configuration.setAttribute(GENERATE_HTML, btnGenerateMixHtml.getSelection());
		configuration.setAttribute(GENERATE_CONTRACT_FILES, btnGenerateSolidityCode.getSelection());
		configuration.setAttribute(GENERATION_TARGET, generationDirectoryText.getText());
		configuration.setAttribute(CONTRACT_FILE_HEADER, fileHeaderText.getText());

		configuration.setAttribute(ENABLE_VERSION, btnVersionAbove.getSelection());
		configuration.setAttribute(VERSION_PRAGMA, versionText.getText());
		
		configuration.setAttribute(COMPILE_CONTRACTS, btnCompile.getSelection());
		configuration.setAttribute(COMPILER_PROGRAMM, compiler_text.getText());
		configuration.setAttribute(COMPILER_TARGET, compiler_out_text.getText());
		
		for (Entry<String, Button> e : compileOptions.entrySet()) {
			configuration.setAttribute(e.getKey(), e.getValue().getSelection());
		}

	}

	@Override
	protected void validatePage() {
		StringBuffer b = new StringBuffer();
		if (modelText.getText() == null || modelText.getText().isEmpty()) {
			b.append("select an uml file.\n");
		}
		if (btnGenerateSolidityCode.getSelection())
			if (generationDirectoryText.getText() == null || generationDirectoryText.getText().isEmpty()) {
				b.append("select a container where to store the contract files in.\n");
			}
		
		if(btnCompile.getSelection()){
			if (compiler_out_text.getText() == null || compiler_out_text.getText().isEmpty()) {
				b.append("Define the compiler output path.\n");
			}
			if (compiler_text.getText() == null || compiler_text.getText().isEmpty()) {
				b.append("Define the compiler executable.\n");
			}else{
				File file = new File(compiler_text.getText());
				if(!file.exists())
					b.append("The executable don't exist.\n");
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
		return "generate Solidity code";
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
