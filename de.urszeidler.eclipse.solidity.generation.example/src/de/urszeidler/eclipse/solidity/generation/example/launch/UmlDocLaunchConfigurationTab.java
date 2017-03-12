/**
 * 
 */
package de.urszeidler.eclipse.solidity.generation.example.launch;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.swt.SWT;
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
	private Text text;
	private Button btnGenerateUmlDoc;

	/**
	 * @wbp.parser.entryPoint
	 */
	@Override
	public void createControl(Composite parent) {
		Composite mainComposite = new Composite(parent, SWT.NONE);
		mainComposite.setLayout(new GridLayout(1, true));
		setControl(mainComposite);
		
		Group grpUmlDoc = new Group(mainComposite, SWT.NONE);
		grpUmlDoc.setText("uml doc");
		grpUmlDoc.setLayout(new GridLayout(3, false));
		grpUmlDoc.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		btnGenerateUmlDoc = new Button(grpUmlDoc, SWT.CHECK);
		btnGenerateUmlDoc.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				validatePage();
			}
		});
		btnGenerateUmlDoc.setText("generate uml doc");
		new Label(grpUmlDoc, SWT.NONE);
		new Label(grpUmlDoc, SWT.NONE);
		
		Label lblNewLabel = new Label(grpUmlDoc, SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel.setText("document target");
		
		text = new Text(grpUmlDoc, SWT.BORDER);
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Button btnNewButton = new Button(grpUmlDoc, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleChooseContainer(text, "select target container");
			}
		});
		btnNewButton.setText("select");
		
	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(GENERATE_UML_DOC, false);
		configuration.setAttribute(GENERATE_UML_DOC_TARGET, "doc");
	}

	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		try {
			btnGenerateUmlDoc.setSelection(configuration.getAttribute(GENERATE_UML_DOC, false));
			text.setText(configuration.getAttribute(GENERATE_UML_DOC_TARGET, "doc"));
		} catch (CoreException e) {
			Activator.logError("Error while initalize launch config.", e);
		}
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(GENERATE_UML_DOC, btnGenerateUmlDoc.getSelection());
		configuration.setAttribute(GENERATE_UML_DOC_TARGET, text.getText());
	}

	@Override
	public String getName() {
		return "uml doc";
	}

}
