/**
 * 
 */
package de.urszeidler.eclipse.solidity.compiler.propertytester;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Platform;

import de.urszeidler.eclipse.solidity.compiler.handler.AddBuilder;

/**
 * @author urs
 *
 */
public class HasBuilderTester extends PropertyTester {
	private static final String IS_ENABLED = "isEnabled";

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.expressions.IPropertyTester#test(java.lang.Object,
	 * java.lang.String, java.lang.Object[], java.lang.Object)
	 */
	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		if (IS_ENABLED.equals(property)) {
			final IProject project = (IProject) Platform.getAdapterManager().getAdapter(receiver, IProject.class);
			if (project != null)
				return AddBuilder.hasBuilder(project);
		}
		return false;
	}

}
