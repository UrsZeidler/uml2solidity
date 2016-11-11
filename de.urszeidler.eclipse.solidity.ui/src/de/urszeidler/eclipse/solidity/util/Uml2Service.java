/**
 * 
 */
package de.urszeidler.eclipse.solidity.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.Stereotype;
import org.eclipse.uml2.uml.Type;

import de.urszeidler.eclipse.solidity.ui.preferences.PreferenceConstants;

/**
 * Simple generation helper.
 * 
 * @author urs
 *
 */
public class Uml2Service {

	private static IPreferenceStore store = null;

	/**
	 * Returns true when the stereotype is applied to the {@link Element}.
	 * 
	 * @param clazz
	 * @param stereotypeName
	 * @return
	 */
	public static boolean hasStereotype(Element clazz, String stereotypeName) {
		if(clazz==null) return false;
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
		if(clazz==null) return null;
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
	public static int getIndexInContainer(Element clazz) {
		EObject eContainer = clazz.eContainer();
		EStructuralFeature eContainingFeature = clazz.eContainingFeature();
		List<?> eGet = (List<?>) eContainer.eGet(eContainingFeature);
		return eGet.indexOf(clazz);
	}

	/**
	 * Returns the header for a solidity file.
	 * 
	 * @param an
	 *            element
	 * @return
	 */
	public static String getSolidityFileHeader(NamedElement clazz) {
		IPreferenceStore store = getStore(clazz);
		return store.getString(PreferenceConstants.CONTRACT_FILE_HEADER);
	}
	
	/**
	 * Returns the header for a js file.
	 * 
	 * @param an
	 *            element
	 * @return
	 */
	public static String getJsFileHeader(NamedElement clazz) {
		IPreferenceStore store = getStore(clazz);
		return store.getString(PreferenceConstants.JS_FILE_HEADER);
	}

	/**
	 * Write the Pragma in the contract code.
	 * 
	 * @param an
	 *            element
	 * @return
	 */
	public static Boolean enableVersion(NamedElement clazz) {
		IPreferenceStore store = getStore(clazz);
		return store.getBoolean(PreferenceConstants.ENABLE_VERSION);
	}


	/**
	 * Returns the header for a js file.
	 * 
	 * @param an
	 *            element
	 * @return
	 */
	public static String getVersionPragma(NamedElement clazz) {
		IPreferenceStore store = getStore(clazz);
		return store.getString(PreferenceConstants.VERSION_PRAGMA);
	}

	/**
	 * Returns the directory for the js file.
	 * 
	 * @param an
	 *            element
	 * @return
	 */
	public static String getJsControllerDirectory(NamedElement clazz) {
		IPreferenceStore store = getStore(clazz);
		String c_target = store.getString(PreferenceConstants.GENERATION_TARGET);
		Path c_path = new Path(c_target);
		
		String js_target = store.getString(PreferenceConstants.GENERATE_JS_CONTROLLER_TARGET);
		Path js_path = new Path(js_target);
		
		IPath makeRelativeTo = js_path.makeRelativeTo(c_path);
		
		if (makeRelativeTo!=null) {
			return makeRelativeTo.toString();
		}
		return js_target;
	}

	/**
	 * Should the js controller being generated.
	 * 
	 * @param an
	 *            element
	 * @return
	 */
	public static Boolean generateJsController(NamedElement clazz) {
		IPreferenceStore store = getStore(clazz);
		return store.getBoolean(PreferenceConstants.GENERATE_JS_CONTROLLER);
	}

	/**
	 * Should the js tests being generated.
	 * 
	 * @param an
	 *            element
	 * @return
	 */
	public static Boolean generateJsTests(NamedElement clazz) {
		IPreferenceStore store = getStore(clazz);
		return store.getBoolean(PreferenceConstants.GENERATE_JS_TEST);
	}

	/**
	 * Should the js controller being generated.
	 * 
	 * @param an
	 *            element
	 * @return
	 */
	public static Boolean generateContractCode(NamedElement clazz) {
		IPreferenceStore store = getStore(clazz);
		return store.getBoolean(PreferenceConstants.GENERATE_CONTRACT_FILES);
	}

	
	/**
	 * Returns the directory for the tests generation.
	 * 
	 * @param an
	 *            element
	 * @return
	 */
	public static String getJsTestsDirectory(NamedElement clazz) {
		IPreferenceStore store = getStore(clazz);
		return store.getString(PreferenceConstants.GENERATE_JS_TEST_TARGET);
	}

	/**
	 * Should abi files for each class being generated.
	 * 
	 * @param an
	 *            element
	 * @return
	 */
	public static Boolean generateAbi(NamedElement clazz) {
		IPreferenceStore store = getStore(clazz);
		return store.getBoolean(PreferenceConstants.GENERATE_ABI);
	}

	/**
	 * Returns the directory for the abi generation.
	 * 
	 * @param an
	 *            element
	 * @return
	 */
	public static String getAbiDirectory(NamedElement clazz) {
		IPreferenceStore store = getStore(clazz);
		return store.getString(PreferenceConstants.GENERATE_ABI_TARGET);
	}

	/**
	 * Should web3 file being generated.
	 * 
	 * @param an
	 *            element
	 * @return
	 */
	public static Boolean generateWeb3(NamedElement clazz) {
		IPreferenceStore store = getStore(clazz);
		return store.getBoolean(PreferenceConstants.GENERATE_WEB3);
	}

	/**
	 * Should web3 file being generated.
	 * 
	 * @param an
	 *            element
	 * @return
	 */
	public static String solidity2javaType(Type type) {
		IPreferenceStore store = getStore(type);		
		String typeName="";
		if(hasStereotype(type, "Contract")||hasStereotype(type, "Library"))
			typeName = store.getString(PreferenceConstants.GENERATION_JAVA_2_SOLIDITY_TYPE_PREFIX+"address");
		else
			typeName = store.getString(PreferenceConstants.GENERATION_JAVA_2_SOLIDITY_TYPE_PREFIX+type.getName());
		if(typeName==null)
			return "String";
		
		return typeName;
	}

	/**
	 * Returns the directory for the abi generation.
	 * 
	 * @param an
	 *            element
	 * @return
	 */
	public static String getInterfacePackagePrefix(NamedElement clazz) {
		IPreferenceStore store = getStore(clazz);
		return store.getString(PreferenceConstants.GENERATION_JAVA_INTERFACE_PACKAGE_PREFIX);
	}

	/**
	 * Should abi files for each class being generated.
	 * 
	 * @param an
	 *            element
	 * @return
	 */
	public static Boolean generateJavaTests(NamedElement clazz) {
		IPreferenceStore store = getStore(clazz);
		return store.getBoolean(PreferenceConstants.GENERATE_JAVA_TESTS);
	}

	/**
	 * Returns the directory for the abi generation.
	 * 
	 * @param an
	 *            element
	 * @return
	 */
	public static String getJavaTestDirectory(NamedElement clazz) {
		IPreferenceStore store = getStore(clazz);
		return store.getString(PreferenceConstants.GENERATION_JAVA_TEST_TARGET);
	}

	/**
	 * Returns the directory where the junit tests can pickup the contract code.
	 * 
	 * @param an
	 *            element
	 * @return
	 */
	public static String getContractPathForJava(NamedElement clazz) {
		IPreferenceStore store = getStore(clazz);
		String co = store.getString(PreferenceConstants.GENERATION_TARGET);
		Path path = new Path(co);
		String javaTestDirectory = getJavaTestDirectory(clazz);
		Path path2 = new Path(javaTestDirectory);
		IPath makeRelativeTo = path.makeRelativeTo(path2);
		//TODO: this is a hack only working in the default test setup
		if(makeRelativeTo.segmentCount()>2)
			makeRelativeTo = makeRelativeTo.removeFirstSegments(2);
		else if(makeRelativeTo.segmentCount()==2)
			return "";
		return makeRelativeTo.toString();
	}

	/**
	 * Get the preference store:
	 * 
	 * @param clazz
	 * @return
	 */
	public static IPreferenceStore getStore(NamedElement clazz) {
		if (Uml2Service.store != null)
			return Uml2Service.store;

		IProject project = null;
		if (clazz != null) {
			URI eUri = clazz.eResource().getURI();
			if (eUri.isPlatformResource()) {
				String platformString = eUri.toPlatformString(true);
				project = ResourcesPlugin.getWorkspace().getRoot().findMember(platformString).getProject();
			}
		}
		IPreferenceStore store = PreferenceConstants.getPreferenceStore(project);
		return store;
	}

	public static void setStore(IPreferenceStore store) {
		Uml2Service.store = store;
	}
}
