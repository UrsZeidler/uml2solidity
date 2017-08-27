/**
 * 
 */
package de.urszeidler.eclipse.solidity.generation.primefaces.launch;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import de.urszeidler.eclipse.solidity.generation.primefaces.Activator;
import de.urszeidler.eclipse.solidity.laucher.ui.AbstractUml2SolidityLaunchConfigurationTab;

/**
 * @author urszeidler
 *
 */
public class PrimefacesLaunchConfigurationTab extends AbstractUml2SolidityLaunchConfigurationTab {

	private static final String DEFAULT_TABLIB_TARGET = "src/main/resources/META-INF/resources/taglib";
	private static final String GENERATE_TAG_LIBS = "de.urszeidler.eclipse.solidity.generation.primefaces.taglib.generator";
	private static final String GENERATE_TAG_LIBS_TARGET = "de.urszeidler.eclipse.solidity.generation.primefaces.taglib.target";
	private static final String GENERATE_CONTRACT_BEANS = "de.urszeidler.eclipse.solidity.generation.primefaces.contractBean.generator";
	private static final String GENERATE_CONTRACT_BEANS_TARGET = "de.urszeidler.eclipse.solidity.generation.primefaces.contractBean.target";
	private static final String DEFAULT_CONTRACT_BEANS_TARGET = "src/";
	private static final String BASE_PACKAGE = "BASE_PACKAGE";
	private static final String GENERATE_APPLICATION_FILES = "de.urszeidler.eclipse.solidity.generation.primefaces.GenerateApplicationFiles";
	private static final String GENERATE_APPLICATION_FILES_TARGET = "de.urszeidler.eclipse.solidity.generation.primefaces.GenerateApplicationFiles.target";
	private static final String DEFAULT_GENERATE_APPLICATION_FILES_TARGET = "src/main";
	private Button btnGenerateTagLibs;
	private Text generateTagLibTargetText;
	private Button btnGenerateContractBeans;
	private Text generateContractBeansTargetText;
	private Text basePackageText;
	private Button btnGenerateApplicationFiles;
	private Text generateApplicationTargetText;

	/**
	 * @wbp.parser.entryPoint
	 */
	@Override
	public void createControl(Composite parent) {
		Composite mainComposite = new Composite(parent, SWT.NONE);
		mainComposite.setLayout(new GridLayout(1, true));
		setControl(mainComposite);

		Group grpPrimefaces = new Group(mainComposite, SWT.NONE);
		grpPrimefaces.setText("primefaces jsf components");
		grpPrimefaces.setLayout(new GridLayout(3, false));
		grpPrimefaces.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		btnGenerateTagLibs = new Button(grpPrimefaces, SWT.CHECK);
		btnGenerateTagLibs.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		btnGenerateTagLibs.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				validatePage();
			}
		});
		btnGenerateTagLibs.setText("generate taglibs");
		new Label(grpPrimefaces, SWT.NONE);

		Label lblNewLabel = new Label(grpPrimefaces, SWT.NONE);
		lblNewLabel.setText("taglib target");

		generateTagLibTargetText = new Text(grpPrimefaces, SWT.BORDER);
		generateTagLibTargetText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				validatePage();
			}
		});
		generateTagLibTargetText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Button btnNewButton = new Button(grpPrimefaces, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleChooseContainer(generateTagLibTargetText, "select target container");
			}
		});
		btnNewButton.setText("select");

		Group grpContractBeans = new Group(mainComposite, SWT.NONE);
		grpContractBeans.setText("contract Beans");
		grpContractBeans.setLayout(new GridLayout(3, false));
		grpContractBeans.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

		btnGenerateContractBeans = new Button(grpContractBeans, SWT.CHECK);
		btnGenerateContractBeans.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
		btnGenerateContractBeans.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				validatePage();
			}
		});
		btnGenerateContractBeans.setText("generate contract beans");

		Label lblNewLabel1 = new Label(grpContractBeans, SWT.NONE);
		lblNewLabel1.setText("contract beans target");

		generateContractBeansTargetText = new Text(grpContractBeans, SWT.BORDER);
		generateContractBeansTargetText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		generateContractBeansTargetText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				validatePage();
			}
		});

		btnNewButton = new Button(grpContractBeans, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleChooseContainer(generateContractBeansTargetText, "select target container");
			}
		});
		btnNewButton.setText("select");

		Label lblNewLabel_1 = new Label(grpContractBeans, SWT.NONE);
		lblNewLabel_1.setSize(86, 20);
		lblNewLabel_1.setText("base package");

		basePackageText = new Text(grpContractBeans, SWT.BORDER);
		basePackageText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(grpContractBeans, SWT.NONE);

		Group grpApplicationFiles = new Group(mainComposite, SWT.NONE);
		grpApplicationFiles.setLayout(new GridLayout(3, false));
		grpApplicationFiles.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		grpApplicationFiles.setText("Application Files");

		btnGenerateApplicationFiles = new Button(grpApplicationFiles, SWT.CHECK);
		btnGenerateApplicationFiles.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
		btnGenerateApplicationFiles.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				validatePage();
			}
		});
		btnGenerateApplicationFiles.setText("generate Application files");

		Label lblNewLabel_2 = new Label(grpApplicationFiles, SWT.NONE);
		lblNewLabel_2.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_2.setText("target");

		generateApplicationTargetText = new Text(grpApplicationFiles, SWT.BORDER);
		generateApplicationTargetText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Button btnNewButton_1 = new Button(grpApplicationFiles, SWT.NONE);
		btnNewButton_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleChooseContainer(generateApplicationTargetText, "select target container");
			}
		});
		btnNewButton_1.setText("select");
	}

	@Override
	public String getName() {
		return "Primeface jsf";
	}

	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		try {
			btnGenerateTagLibs.setSelection(configuration.getAttribute(GENERATE_TAG_LIBS, false));
			generateTagLibTargetText
					.setText(configuration.getAttribute(GENERATE_TAG_LIBS_TARGET, DEFAULT_TABLIB_TARGET));
			btnGenerateContractBeans.setSelection(configuration.getAttribute(GENERATE_CONTRACT_BEANS, false));
			generateContractBeansTargetText
					.setText(configuration.getAttribute(GENERATE_CONTRACT_BEANS_TARGET, DEFAULT_CONTRACT_BEANS_TARGET));
			basePackageText.setText(configuration.getAttribute(BASE_PACKAGE, ""));
			btnGenerateApplicationFiles.setSelection(configuration.getAttribute(GENERATE_APPLICATION_FILES, false));
			generateApplicationTargetText.setText(configuration.getAttribute(GENERATE_APPLICATION_FILES_TARGET,
					DEFAULT_GENERATE_APPLICATION_FILES_TARGET));
		} catch (CoreException e) {
			Activator.logError("Error while initialize launch config.", e);
		}
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(GENERATE_TAG_LIBS, btnGenerateTagLibs.getSelection());
		configuration.setAttribute(GENERATE_TAG_LIBS_TARGET, generateTagLibTargetText.getText());
		configuration.setAttribute(GENERATE_CONTRACT_BEANS, btnGenerateContractBeans.getSelection());
		configuration.setAttribute(GENERATE_CONTRACT_BEANS_TARGET, generateContractBeansTargetText.getText());
		configuration.setAttribute(BASE_PACKAGE, basePackageText.getText());
		configuration.setAttribute(GENERATE_APPLICATION_FILES, btnGenerateApplicationFiles.getSelection());
		configuration.setAttribute(GENERATE_APPLICATION_FILES_TARGET, generateApplicationTargetText.getText());
	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(GENERATE_TAG_LIBS, false);
		configuration.setAttribute(GENERATE_TAG_LIBS_TARGET, DEFAULT_TABLIB_TARGET);
		configuration.setAttribute(GENERATE_CONTRACT_BEANS, false);
		configuration.setAttribute(GENERATE_CONTRACT_BEANS_TARGET, DEFAULT_CONTRACT_BEANS_TARGET);
		configuration.setAttribute(BASE_PACKAGE, "");
		configuration.setAttribute(GENERATE_APPLICATION_FILES, false);
		configuration.setAttribute(GENERATE_APPLICATION_FILES_TARGET, DEFAULT_GENERATE_APPLICATION_FILES_TARGET);
	}

}
