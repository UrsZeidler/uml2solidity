/**
 * 
 */
package de.urszeidler.eclipse.solidity.ui.common;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.papyrus.uml.tools.utils.UMLUtil;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.uml2.uml.Element;

import de.urszeidler.eclipse.solidity.ui.Activator;
import de.urszeidler.eclipse.solidity.ui.popupMenus.AcceleoGenerateSolidityAction;
import de.urszeidler.eclipse.solidity.ui.preferences.PreferenceConstants;

/**
 * @author urs
 *
 */
public class GenerateSolidityCommand extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		final ISelection selection = HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().getSelection();
		   if (selection != null & selection instanceof IStructuredSelection) {
		    if (! selection.isEmpty()){
		    	
				IRunnableWithProgress operation = new IRunnableWithProgress() {
					public void run(IProgressMonitor monitor) {
						try {
					        Object selectedElement = ((IStructuredSelection)selection).getFirstElement();
					        
					        Element resolveUMLElement = UMLUtil.resolveUMLElement(selectedElement);
					        if(resolveUMLElement!=null){
					    		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
					    		final String gtarget = store.getString(PreferenceConstants.GENERATION_TARGET);
					        	Resource eResource = resolveUMLElement.eResource();
					        	Path model = new Path(eResource.getURI().toPlatformString(true));
								
					        	IFile iModelFile = ResourcesPlugin.getWorkspace().getRoot().getFile(model);
								AcceleoGenerateSolidityAction.modelTransform(gtarget, monitor, iModelFile , eResource.getURI(), new ArrayList<Object>());
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
		return null;
	}

}
