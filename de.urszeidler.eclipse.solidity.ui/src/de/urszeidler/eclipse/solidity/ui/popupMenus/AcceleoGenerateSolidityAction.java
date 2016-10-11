/*******************************************************************************
 * Copyright (c) 2008, 2012 Obeo.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Obeo - initial API and implementation
 *******************************************************************************/
package de.urszeidler.eclipse.solidity.ui.popupMenus;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionDelegate;

import de.urszeidler.eclipse.solidity.ui.Activator;
import de.urszeidler.eclipse.solidity.ui.common.GenerateAll;
import de.urszeidler.eclipse.solidity.ui.preferences.PreferenceConstants;

/**
 * Solidity code generation.
 */
public class AcceleoGenerateSolidityAction extends ActionDelegate implements IActionDelegate {

	/**
	 * Selected model files.
	 */
	protected List<IFile> files;

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.ui.actions.ActionDelegate#selectionChanged(org.eclipse.jface.action.IAction,
	 *      org.eclipse.jface.viewers.ISelection)
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	public void selectionChanged(IAction action, ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			files = ((IStructuredSelection) selection).toList();
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.ui.actions.ActionDelegate#run(org.eclipse.jface.action.IAction)
	 * @generated
	 */
	public void run(IAction action) {
		if (files != null) {
			IRunnableWithProgress operation = new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) {
					try {
						Iterator<IFile> filesIt = files.iterator();
						while (filesIt.hasNext()) {
							IFile model = (IFile) filesIt.next();
							IPreferenceStore store =  PreferenceConstants.getPreferenceStore(model.getProject());	// Activator.getDefault().getPreferenceStore();
							final String gtarget = store.getString(PreferenceConstants.GENERATION_TARGET);	
							URI modelURI = URI.createPlatformResourceURI(model.getFullPath().toString(), true);
							modelTransform(gtarget, monitor, model, modelURI,getArguments());
						}
					} catch (CoreException e) {
						IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e);
						Activator.getDefault().getLog().log(status);
					}
				}
			};
			try {
				PlatformUI.getWorkbench().getProgressService().run(true, true, operation);
			} catch (InvocationTargetException e) {
				IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e);
				Activator.getDefault().getLog().log(status);
			} catch (InterruptedException e) {
				IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e);
				Activator.getDefault().getLog().log(status);
			}
		}
	}

	/**
	 * Computes the arguments of the generator.
	 * 
	 * @return the arguments
	 * @generated
	 */
	protected List<? extends Object> getArguments() {
		return new ArrayList<String>();
	}

	public static void modelTransform(final String gtarget, IProgressMonitor monitor, IFile model, URI modelURI, List<? extends Object> arguments)
			throws CoreException {
		try {
			IContainer target = model.getProject().getFolder(gtarget);
			GenerateAll generator = new GenerateAll(modelURI, target, arguments);
			generator.doGenerate(monitor);
		} catch (IOException e) {
			IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e);
			Activator.getDefault().getLog().log(status);
		} finally {
			model.getProject().refreshLocal(IResource.DEPTH_INFINITE, monitor);
		}
	}

	public static void modelTransform(IContainer target, IProgressMonitor monitor, URI modelURI, List<? extends Object> arguments)
			throws CoreException {
		try {
			GenerateAll generator = new GenerateAll(modelURI, target, arguments);
			generator.doGenerate(monitor);
		} catch (IOException e) {
			IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e);
			Activator.getDefault().getLog().log(status);
		} finally {
			target.getProject().refreshLocal(IResource.DEPTH_INFINITE, monitor);
		}
	}

}