/**
 * 
 */
package de.urszeidler.eclipse.solidity.compiler.handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import de.urszeidler.eclipse.solidity.compiler.builder.SolidityBuilder;
import de.urszeidler.eclipse.solidity.compiler.support.Activator;

/**
 * @author urs
 * loosy from http://codeandme.blogspot.de/2012/10/integrating-custom-builder.html
 *
 */
public class AddBuilder extends AbstractHandler implements IHandler {

	@Override
	public Object execute(final ExecutionEvent event) {
		final IProject project = getProject(event);

		if (project != null) {
			try {
				// verify already registered builders
				if (hasBuilder(project))
					// already enabled
					return null;

				// add builder to project properties
				IProjectDescription description = project.getDescription();
				final ICommand buildCommand = description.newCommand();
				buildCommand.setBuilderName(SolidityBuilder.BUILDER_ID);

				final List<ICommand> commands = new ArrayList<ICommand>();
				commands.addAll(Arrays.asList(description.getBuildSpec()));
				commands.add(buildCommand);

				description.setBuildSpec(commands.toArray(new ICommand[commands.size()]));
				project.setDescription(description, null);

			} catch (final CoreException e) {
				Activator.logError("Error adding solc builder", e);
			}
		}
		return null;
	}

	public static IProject getProject(final ExecutionEvent event) {
		final ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (selection instanceof IStructuredSelection) {
			final Object element = ((IStructuredSelection) selection).getFirstElement();

			return (IProject) Platform.getAdapterManager().getAdapter(element, IProject.class);
		}
		return null;
	}

	public static final boolean hasBuilder(final IProject project) {
		try {
			for (final ICommand buildSpec : project.getDescription().getBuildSpec()) {
				if (SolidityBuilder.BUILDER_ID.equals(buildSpec.getBuilderName()))
					return true;
			}
		} catch (final CoreException e) {
		}
		return false;
	}
}