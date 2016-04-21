package de.urszeidler.eclipse.solidity.compiler.support.nature;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;

public class SolidityProjectNature implements IProjectNature {
	final String BUILDER_ID = "de.urszeidler.eclipse.solidity.compiler.support.SolidityBuilder";
	
	private IProject project;

	private void addNature() throws CoreException {
		final IProjectDescription description = project.getDescription();
		String[] natures = {BUILDER_ID, "org.eclipse.xtext.ui.shared.xtextNature" };

		final Set<String> natureDescriptions = (new HashSet<String>( Arrays.asList(description.getNatureIds())));
		natureDescriptions.addAll(Arrays.asList(natures));
		description.setNatureIds((String[]) natureDescriptions.toArray(new String[natureDescriptions.size()]));
		project.setDescription(description, null);

	}
	
	@Override
	public void configure() throws CoreException {
//		   IProjectDescription desc = project.getDescription();
//		   ICommand[] commands = desc.getBuildSpec();
//		   boolean found = false;
//
//		   for (int i = 0; i < commands.length; ++i) {
//		      if (commands[i].getBuilderName().equals(BUILDER_ID)) {
//		         found = true;
//		         break;
//		      }
//		   }
//		   if (!found) { 
//		      //add builder to project
//		      ICommand command = desc.newCommand();
//		      command.setBuilderName(BUILDER_ID);
//		      ICommand[] newCommands = new ICommand[commands.length + 1];
//
//		      // Add it before other builders.
//		      System.arraycopy(commands, 0, newCommands, 1, commands.length);
//		      newCommands[0] = command;
//		      desc.setBuildSpec(newCommands);
//		      project.setDescription(desc, null);
//		   }
		addNature();
	}

	@Override
	public void deconfigure() throws CoreException {
		// TODO Automatisch generierter Methodenstub

	}

	@Override
	public IProject getProject() {
		return project;
	}

	@Override
	public void setProject(IProject project) {
		this.project = project;

	}

}
