/**
 * 
 */
package de.urszeidler.eclipse.solidity.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.Stereotype;

/**
 * Simple generation helper.
 * 
 * @author urs
 *
 */
public class Uml2Service {
	
	/**
	 * Returns true when the stereotype is applied to the {@link Element}.
	 * @param clazz
	 * @param stereotypeName
	 * @return
	 */
	public boolean hasStereotype(Element clazz, String stereotypeName) {
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
	 * @param clazz
	 * @param stereotypeName
	 * @return
	 */
	public Stereotype getStereotype(Element clazz, String stereotypeName) {
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
	 * @param clazz
	 * @param stereotypeName 
	 * @param propertyName
	 * @return
	 */
	public Object getStereotypeValue(Element clazz, String stereotypeName, String propertyName) {
		Stereotype stereotype = getStereotype(clazz, stereotypeName);
		if(stereotype!=null){
			Object value =  clazz.getValue(stereotype, propertyName);
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
	public List<?> getStereotypeListValue(Element clazz, String stereotypeName, String propertyName) {
		Stereotype stereotype = getStereotype(clazz, stereotypeName);
		if(stereotype!=null){
			try {
				Object value =  clazz.getValue(stereotype, propertyName);
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
	 * @param clazz
	 * @return
	 */
	public int getIndexInContainer(NamedElement clazz) {
		EObject eContainer = clazz.eContainer();
		EStructuralFeature eContainingFeature = clazz.eContainingFeature();
		List<?> eGet = (List<?>) eContainer.eGet(eContainingFeature);
		return eGet.indexOf(clazz);
	}
}
