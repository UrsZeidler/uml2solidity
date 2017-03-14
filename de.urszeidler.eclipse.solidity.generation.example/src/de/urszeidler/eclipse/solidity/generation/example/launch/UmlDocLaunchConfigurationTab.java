/**
 * 
 */
package de.urszeidler.eclipse.solidity.generation.example.launch;

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

import de.urszeidler.eclipse.solidity.generation.example.Activator;
import de.urszeidler.eclipse.solidity.laucher.ui.AbstractUml2SolidityLaunchConfigurationTab;

/**
 * @author urs
 *
 */
public class UmlDocLaunchConfigurationTab extends AbstractUml2SolidityLaunchConfigurationTab {
	private static final String GENERATE_UML_DOC_TARGET = "de.urszeidler.eclipse.solidity.generation.example.usecaseDocTarget";
	private static final String GENERATE_UML_DOC = "de.urszeidler.eclipse.solidity.generation.example.useCaseDoc";
	private static final String GENERATE_UML_DOC_IMAGE_URL = "GENERATE_UML_DOC_IMAGE_URL";
	private Text targetText;
	private Button btnGenerateUmlDoc;
	private Text imageUrlText;

	/**
	 * @wbp.parser.entryPoint
	 */
	@Override
	public void createControl(Composite parent) {
		Composite mainComposite = new Composite(parent, SWT.NONE);
		mainComposite.setLayout(new GridLayout(1, true));
		setControl(mainComposite);
		
		Group grpUmlDoc = new Group(mainComposite, SWT.NONE);
		grpUmlDoc.setText("uml use case doc");
		grpUmlDoc.setLayout(new GridLayout(3, false));
		grpUmlDoc.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		btnGenerateUmlDoc = new Button(grpUmlDoc, SWT.CHECK);
		btnGenerateUmlDoc.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		btnGenerateUmlDoc.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				validatePage();
			}
		});
		btnGenerateUmlDoc.setText("generate uml doc");
		new Label(grpUmlDoc, SWT.NONE);
		
		Label lblNewLabel = new Label(grpUmlDoc, SWT.NONE);
		lblNewLabel.setText("document target");
		
		targetText = new Text(grpUmlDoc, SWT.BORDER);
		targetText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				validatePage();
			}
		});
		targetText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Button btnNewButton = new Button(grpUmlDoc, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleChooseContainer(targetText, "select target container");
			}
		});
		btnNewButton.setText("select");
		
		Label lblNewLabel_1 = new Label(grpUmlDoc, SWT.NONE);
		lblNewLabel_1.setText("image url");
		
		imageUrlText = new Text(grpUmlDoc, SWT.BORDER);
		imageUrlText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				validatePage();
			}
		});
		imageUrlText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		
	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(GENERATE_UML_DOC, false);
		configuration.setAttribute(GENERATE_UML_DOC_TARGET, "doc");
		configuration.setAttribute(GENERATE_UML_DOC_IMAGE_URL, "");
	}

	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		try {
			btnGenerateUmlDoc.setSelection(configuration.getAttribute(GENERATE_UML_DOC, false));
			targetText.setText(configuration.getAttribute(GENERATE_UML_DOC_TARGET, "doc"));
			imageUrlText.setText(configuration.getAttribute(GENERATE_UML_DOC_IMAGE_URL, ""));
		} catch (CoreException e) {
			Activator.logError("Error while initialize launch config.", e);
		}
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(GENERATE_UML_DOC, btnGenerateUmlDoc.getSelection());
		configuration.setAttribute(GENERATE_UML_DOC_TARGET, targetText.getText());
		configuration.setAttribute(GENERATE_UML_DOC_IMAGE_URL, imageUrlText.getText());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#getName()
	 */
	@Override
	public String getName() {
		return "uml usecase doc";
	}
}	
