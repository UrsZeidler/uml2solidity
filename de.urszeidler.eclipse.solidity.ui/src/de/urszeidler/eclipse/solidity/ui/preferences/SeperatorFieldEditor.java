/*******************************************************************************
 * Copyright (c) 2016 Urs Zeidler
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Urs Zeidler - initial API and implementation
 *******************************************************************************/
/**
 * 
 */
package de.urszeidler.eclipse.solidity.ui.preferences;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * @author uzeidler
 *
 */
public class SeperatorFieldEditor extends FieldEditor {

	/**
	 * 
	 */
	public SeperatorFieldEditor() {
	}

	/**
	 * @param name
	 * @param labelText
	 * @param parent
	 */
	public SeperatorFieldEditor(String name, String labelText, Composite parent) {
		super(name, labelText, parent);
	}

	/* (nicht-Javadoc)
	 * @see org.eclipse.jface.preference.FieldEditor#adjustForNumColumns(int)
	 */
	@Override
	protected void adjustForNumColumns(int numColumns) {

	}

	/* (nicht-Javadoc)
	 * @see org.eclipse.jface.preference.FieldEditor#doFillIntoGrid(org.eclipse.swt.widgets.Composite, int)
	 */
	@Override
	protected void doFillIntoGrid(Composite parent, int numColumns) {
		Label label = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
        GridData gd = new GridData();
        gd.horizontalSpan = numColumns;
        gd.horizontalAlignment = SWT.FILL;
        label.setLayoutData(gd);
	}

	/* (nicht-Javadoc)
	 * @see org.eclipse.jface.preference.FieldEditor#doLoad()
	 */
	@Override
	protected void doLoad() {

	}

	/* (nicht-Javadoc)
	 * @see org.eclipse.jface.preference.FieldEditor#doLoadDefault()
	 */
	@Override
	protected void doLoadDefault() {

	}

	/* (nicht-Javadoc)
	 * @see org.eclipse.jface.preference.FieldEditor#doStore()
	 */
	@Override
	protected void doStore() {

	}

	/* (nicht-Javadoc)
	 * @see org.eclipse.jface.preference.FieldEditor#getNumberOfControls()
	 */
	@Override
	public int getNumberOfControls() {
		return 2;
	}

}
