/**
 * 
 */
package de.urszeidler.eclipse.solidity.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.Stereotype;

import de.urszeidler.eclipse.solidity.ui.preferences.PreferenceConstants;

/**
 * Simple generation helper.
 * 
 * @author urs
 *
 */
public class Uml2Service {

	/**
	 * Returns true when the stereotype is applied to the {@link Element}.
	 * 
	 * @param clazz
	 * @param stereotypeName
	 * @return
	 */
	public static boolean hasStereotype(Element clazz, String stereotypeName) {
		List<Stereotype> stereotypes = clazz.getAppliedStereotypes();
		for (Stereotype stereotype : stereotypes) {
			if (stereotype.getName().equals(stereotypeName)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the stereotype.
	 * 
	 * @param clazz
	 * @param stereotypeName
	 * @return
	 */
	public static Stereotype getStereotype(Element clazz, String stereotypeName) {
		List<Stereotype> stereotypes = clazz.getAppliedStereotypes();
		for (Stereotype stereotype : stereotypes) {
			if (stereotype.getName().equals(stereotypeName)) {
				return stereotype;
			}
		}
		return null;
	}

	/**
	 * Return the value of the the stereotype property.
	 * 
	 * @param clazz
	 * @param stereotypeName
	 * @param propertyName
	 * @return
	 */
	public static Object getStereotypeValue(Element clazz, String stereotypeName, String propertyName) {
		Stereotype stereotype = getStereotype(clazz, stereotypeName);
		if (stereotype != null) {
			Object value = clazz.getValue(stereotype, propertyName);
			return value;
		}
		return null;
	}

	/**
	 * Returns the value cast as an List.
	 * 
	 * @param clazz
	 * @param stereotypeName
	 * @param propertyName
	 * @return
	 */
	public static List<?> getStereotypeListValue(Element clazz, String stereotypeName, String propertyName) {
		Stereotype stereotype = getStereotype(clazz, stereotypeName);
		if (stereotype != null) {
			try {
				Object value = clazz.getValue(stereotype, propertyName);
				if (value instanceof List) {
					List<?> new_name = (List<?>) value;
					return new_name;
				}
			} catch (IllegalArgumentException e) {
			}

		}
		return new ArrayList<Object>();
	}

	/**
	 * Get the index of the given {@link NamedElement} of its container.
	 * 
	 * @param clazz
	 * @return
	 */
	public static int getIndexInContainer(NamedElement clazz) {
		EObject eContainer = clazz.eContainer();
		EStructuralFeature eContainingFeature = clazz.eContainingFeature();
		List<?> eGet = (List<?>) eContainer.eGet(eContainingFeature);
		return eGet.indexOf(clazz);
	}

	/**
	 * Returns the header for a solidity file.
	 * @param an element
	 * @return
	 */
	public static String getSolidityFileHeader(NamedElement clazz) {
		clazz.eResource().getURI().isPlatform();
		IProject project = null;
		URI eUri = clazz.eResource().getURI();
		if (eUri.isPlatformResource()) {
			String platformString = eUri.toPlatformString(true);
			project = ResourcesPlugin.getWorkspace().getRoot().findMember(platformString).getProject();
		}
		IPreferenceStore store = PreferenceConstants.getPreferenceStore(project);

		return store.getString(PreferenceConstants.CONTRACT_FILE_HEADER);
	}
}
