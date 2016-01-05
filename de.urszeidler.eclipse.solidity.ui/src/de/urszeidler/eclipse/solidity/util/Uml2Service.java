/**
 * 
 */
package de.urszeidler.eclipse.solidity.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.Stereotype;

/**
 * Simple generation helper.
 * 
 * @author urs
 *
 */
public class Uml2Service {
	
	public boolean hasStereotype(NamedElement clazz, String stereotypeName) {
		List<Stereotype> stereotypes = clazz.getAppliedStereotypes();
		for (Stereotype stereotype : stereotypes) {
			if (stereotype.getName().equals(stereotypeName)) {
				return true;
			}
		}
		return false;
	}
	
	public Stereotype getStereotype(NamedElement clazz, String stereotypeName) {
		List<Stereotype> stereotypes = clazz.getAppliedStereotypes();
		for (Stereotype stereotype : stereotypes) {
			if (stereotype.getName().equals(stereotypeName)) {
				return stereotype;
			}
		}
		return null;
	}
	
	public Object getStereotypeValue(NamedElement clazz, String stereotypeName, String propertyName) {
		Stereotype stereotype = getStereotype(clazz, stereotypeName);
		if(stereotype!=null){
			Object value =  clazz.getValue(stereotype, propertyName);
			return value;
		}
		return null;
	}
	
	public List<?> getStereotypeListValue(NamedElement clazz, String stereotypeName, String propertyName) {
		Stereotype stereotype = getStereotype(clazz, stereotypeName);
		if(stereotype!=null){
			try {
				Object value =  clazz.getValue(stereotype, propertyName);
				if (value instanceof List) {
					List<?> new_name = (List<?>) value;
					return new_name;
				}
			} catch (IllegalArgumentException e) {
				// TODO: handle exception
			}
			
		}
		return new ArrayList<Object>();
	}
	
	public int getIndexInContainer(NamedElement clazz) {
		EObject eContainer = clazz.eContainer();
		EStructuralFeature eContainingFeature = clazz.eContainingFeature();
		List<?> eGet = (List<?>) eContainer.eGet(eContainingFeature);
		return eGet.indexOf(clazz);
	}
}
