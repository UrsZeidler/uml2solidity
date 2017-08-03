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
public class PrimefacesLaunchConfigurationTab
		extends 
		AbstractUml2SolidityLaunchConfigurationTab {

	private static final String DEFAULT_TABLIB_TARGET = "src/main/resources/META-INF/resources/taglib";
	private static final String GENERATE_TAG_LIBS = "de.urszeidler.eclipse.solidity.generation.primefaces.taglib.generator";
	private static final String GENERATE_TAG_LIBS_TARGET = "de.urszeidler.eclipse.solidity.generation.primefaces.taglib.target";
	private static final String GENERATE_CONTRACT_BEANS = "de.urszeidler.eclipse.solidity.generation.primefaces.contractBean.generator";
	private static final String GENERATE_CONTRACT_BEANS_TARGET = "de.urszeidler.eclipse.solidity.generation.primefaces.contractBean.target";
	private static final String DEFAULT_CONTRACT_BEANS_TARGET = "src/";
	private static final String BASE_PACKAGE = "BASE_PACKAGE";
	private Button btnGenerateTagLibs;
	private Text generateTagLibTargetText;
	private Button btnGenerateContractBeans;
	private Text generateContractBeansTargetText;
	private Label lblNewLabel_1;
	private Text basePackageText;

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

		btnGenerateContractBeans = new Button(grpPrimefaces, SWT.CHECK);
		btnGenerateContractBeans.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		btnGenerateContractBeans.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				validatePage();
			}
		});
		btnGenerateContractBeans.setText("generate contract beans");
		new Label(grpPrimefaces, SWT.NONE);
		
		Label lblNewLabel1 = new Label(grpPrimefaces, SWT.NONE);
		lblNewLabel1.setText("contract beans target target");
		
		generateContractBeansTargetText = new Text(grpPrimefaces, SWT.BORDER);
		generateContractBeansTargetText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				validatePage();
			}
		});
		generateContractBeansTargetText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		btnNewButton = new Button(grpPrimefaces, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleChooseContainer(generateContractBeansTargetText, "select target container");
			}
		});
		btnNewButton.setText("select");
		
		lblNewLabel_1 = new Label(grpPrimefaces, SWT.NONE);
		lblNewLabel_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_1.setText("base package");
		
		basePackageText = new Text(grpPrimefaces, SWT.BORDER);
		basePackageText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(grpPrimefaces, SWT.NONE);
	}

	@Override
	public String getName() {
		return "Primeface jsf";
	}

	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		try {
			btnGenerateTagLibs.setSelection(configuration.getAttribute(GENERATE_TAG_LIBS, false));
			generateTagLibTargetText.setText(configuration.getAttribute(GENERATE_TAG_LIBS_TARGET, DEFAULT_TABLIB_TARGET));
			btnGenerateContractBeans.setSelection(configuration.getAttribute(GENERATE_CONTRACT_BEANS, false));
			generateContractBeansTargetText.setText(configuration.getAttribute(GENERATE_CONTRACT_BEANS_TARGET, DEFAULT_CONTRACT_BEANS_TARGET));
			basePackageText.setText(configuration.getAttribute(BASE_PACKAGE, ""));
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
	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(GENERATE_TAG_LIBS, false);
		configuration.setAttribute(GENERATE_TAG_LIBS_TARGET, DEFAULT_TABLIB_TARGET);
		configuration.setAttribute(GENERATE_CONTRACT_BEANS, false);
		configuration.setAttribute(GENERATE_CONTRACT_BEANS_TARGET, DEFAULT_CONTRACT_BEANS_TARGET);
		configuration.setAttribute(BASE_PACKAGE, "");
	}

}
