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
package de.urszeidler.eclipse.solidity.ui.common;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.function.Consumer;

import org.eclipse.acceleo.engine.service.AbstractAcceleoGenerator;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IProgressMonitorWithBlocking;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.util.BasicMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.preference.IPreferenceStore;
import org.osgi.framework.Bundle;

import com.google.common.base.Function;

import de.urszeidler.eclipse.solidity.compiler.support.util.StartCompiler;
import de.urszeidler.eclipse.solidity.templates.GenerateContracts;
import de.urszeidler.eclipse.solidity.templates.GenerateHtml;
import de.urszeidler.eclipse.solidity.templates.GenerateJsCode;
import de.urszeidler.eclipse.solidity.templates.GenerateJsTestCode;
import de.urszeidler.eclipse.solidity.templates.GenerateMarkDown;
import de.urszeidler.eclipse.solidity.templates.GenerateMixConfig;
import de.urszeidler.eclipse.solidity.templates.GenerateSingleAbiFiles;
import de.urszeidler.eclipse.solidity.templates.GenerateWeb3Contract;
import de.urszeidler.eclipse.solidity.ui.Activator;
import de.urszeidler.eclipse.solidity.ui.preferences.PreferenceConstants;
import de.urszeidler.eclipse.solidity.util.Uml2Service;

/**
 * Main entry point of the 'Solidity' generation module.
 */
public class GenerateAll {

	/**
	 * The model URI.
	 */
	private URI modelURI;

	/**
	 * The output folder.
	 */
	private IContainer targetFolder;

	/**
	 * The other arguments.
	 */
	List<? extends Object> arguments;

	private List<String> files;

	/**
	 * Constructor.
	 * 
	 * @param modelURI
	 *            is the URI of the model.
	 * @param targetFolder
	 *            is the output folder
	 * @param arguments
	 *            are the other arguments
	 * @throws IOException
	 *             Thrown when the output cannot be saved.
	 * @generated
	 */
	public GenerateAll(URI modelURI, IContainer targetFolder, List<? extends Object> arguments) {
		this.modelURI = modelURI;
		this.targetFolder = targetFolder;
		this.arguments = arguments;
	}

	/**
	 * Launches the generation.
	 *
	 * @param monitor
	 *            This will be used to display progress information to the user.
	 * @throws IOException
	 *             Thrown when the output cannot be saved.
	 * @generated not
	 */
	public void doGenerate(IProgressMonitor monitor) throws IOException {
		if (!targetFolder.getLocation().toFile().exists()) {
			targetFolder.getLocation().toFile().mkdirs();
		}
		IPreferenceStore store = Uml2Service.getStore(null); // PreferenceConstants.getPreferenceStore(targetFolder.getProject());
		if (monitor != null)
			monitor.subTask("Loading...");

		try {
			AbstractAcceleoGenerator solGenerator = doGenerate1(store, PreferenceConstants.GENERATION_TARGET, PreferenceConstants.GENERATE_CONTRACT_FILES,
					"de.urszeidler.eclipse.solidity.templates.GenerateContracts", monitor,
					new Function<File, AbstractAcceleoGenerator>() {

						public AbstractAcceleoGenerator apply(File input) {
							try {
								return new GenerateContracts(modelURI, input, arguments);
							} catch (IOException e) {
							}
							return null;
						}
					});
			doGenerate1(store, PreferenceConstants.GENERATION_TARGET, PreferenceConstants.GENERATE_MIX,
					"de.urszeidler.eclipse.solidity.templates.GenerateMixConfig", monitor,
					new Function<File, AbstractAcceleoGenerator>() {

						public AbstractAcceleoGenerator apply(File input) {
							try {
								return new GenerateMixConfig(modelURI, input, arguments);
							} catch (IOException e) {
							}
							return null;
						}
					});
			doGenerate1(store, PreferenceConstants.GENERATION_TARGET, PreferenceConstants.GENERATE_HTML,
					"de.urszeidler.eclipse.solidity.templates.GenerateHtml", monitor,
					new Function<File, AbstractAcceleoGenerator>() {

						public AbstractAcceleoGenerator apply(File input) {
							try {
								return new GenerateHtml(modelURI, input, arguments);
							} catch (IOException e) {
							}
							return null;
						}
					});
			doGenerate1(store, PreferenceConstants.GENERATION_TARGET_DOC, PreferenceConstants.GENERATE_MARKDOWN,
					"de.urszeidler.eclipse.solidity.templates.GenerateMarkDown", monitor,
					new Function<File, AbstractAcceleoGenerator>() {

						public AbstractAcceleoGenerator apply(File input) {
							try {
								return new GenerateMarkDown(modelURI, input, arguments);
							} catch (IOException e) {
							}
							return null;
						}
					});

			doGenerate1(store, PreferenceConstants.GENERATE_JS_TEST_TARGET, PreferenceConstants.GENERATE_JS_TEST,
					"de.urszeidler.eclipse.solidity.templates.GenerateJsTestCode", monitor,
					new Function<File, AbstractAcceleoGenerator>() {

						public AbstractAcceleoGenerator apply(File input) {
							try {
								return new GenerateJsTestCode(modelURI, input, arguments);
							} catch (IOException e) {
							}
							return null;
						}
					});
			doGenerate1(store, PreferenceConstants.GENERATE_JS_CONTROLLER_TARGET,
					PreferenceConstants.GENERATE_JS_CONTROLLER,
					"de.urszeidler.eclipse.solidity.templates.GenerateJsCode", monitor,
					new Function<File, AbstractAcceleoGenerator>() {

						public AbstractAcceleoGenerator apply(File input) {
							try {
								return new GenerateJsCode(modelURI, input, arguments);
							} catch (IOException e) {
							}
							return null;
						}
					});
			doGenerate1(store, PreferenceConstants.GENERATE_JS_CONTROLLER_TARGET, PreferenceConstants.GENERATE_WEB3,
					"de.urszeidler.eclipse.solidity.templates.GenerateWeb3Contract", monitor,
					new Function<File, AbstractAcceleoGenerator>() {

						public AbstractAcceleoGenerator apply(File input) {
							try {
								return new GenerateWeb3Contract(modelURI, input, arguments);
							} catch (IOException e) {
							}
							return null;
						}
					});

			doGenerate1(store, PreferenceConstants.GENERATE_ABI_TARGET,
					PreferenceConstants.GENERATE_ABI,
					"de.urszeidler.eclipse.solidity.templates.GenerateSingleAbiFiles", monitor,
					new Function<File, AbstractAcceleoGenerator>() {

						public AbstractAcceleoGenerator apply(File input) {
							try {
								return new GenerateSingleAbiFiles(modelURI, input, arguments);
							} catch (IOException e) {
							}
							return null;
						}
					});
			
			compileContracts(monitor,(GenerateContracts) solGenerator );
		} catch (CoreException e1) {
			Activator.logError("", e1);
		}
	}

	private AbstractAcceleoGenerator doGenerate1(IPreferenceStore store, String targetDirectory, String runGenerator, String gen_id,
			IProgressMonitor monitor, Function<File, AbstractAcceleoGenerator> factory)
			throws IOException, CoreException {
		if (monitor != null)
			if (monitor.isCanceled())
				return null;

		if (store.getBoolean(runGenerator)) {
			final String docTarget = store.getString(targetDirectory);
			IFolder folder = null;
			if (docTarget.startsWith("/")) {
				IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
				Path location = new Path(docTarget);
				folder = root.getFolder(location);
				if (!folder.exists())
					folder.create(true, true, monitor);
			} else {
				folder = targetFolder.getProject().getFolder(docTarget);
				if (!folder.exists())
					folder.create(true, true, monitor);
			}
			if (monitor != null)
				monitor.worked(1);

			AbstractAcceleoGenerator acceleoGenerator = factory.apply(folder.getLocation().toFile());
			if(acceleoGenerator==null)
				return null;

			String generationID = org.eclipse.acceleo.engine.utils.AcceleoLaunchingUtil.computeUIProjectID(
					"de.urszeidler.eclipse.solidity.ui", gen_id, modelURI.toString(), folder.getFullPath().toString(),
					new ArrayList<String>());
			acceleoGenerator.setGenerationID(generationID);
			if (monitor != null)
				monitor.subTask("generate markdown");
			acceleoGenerator.doGenerate(BasicMonitor.toMonitor(monitor));
			
			return acceleoGenerator;
		}
		return null;
	}

	/**
	 * If enable compile the generated contracts.
	 * 
	 * @param monitor
	 * @param gen0
	 */
	private void compileContracts(IProgressMonitor monitor, GenerateContracts gen0) {
		IPreferenceStore store1 = de.urszeidler.eclipse.solidity.compiler.support.preferences.PreferenceConstants
				.getPreferenceStore(targetFolder.getProject());
		if (store1.getBoolean(
				de.urszeidler.eclipse.solidity.compiler.support.preferences.PreferenceConstants.COMPILE_CONTRACTS)) {

			files = gen0.getFiles();
			if (files.isEmpty())
				return;
			String compile_folder = store1.getString(
					de.urszeidler.eclipse.solidity.compiler.support.preferences.PreferenceConstants.COMPILER_TARGET);
			IContainer target = targetFolder.getProject().getFolder(compile_folder);
			if (!target.getLocation().toFile().exists()) {
				target.getLocation().toFile().mkdirs();
			}
			if (monitor != null)
				monitor.subTask("compile code");
			StartCompiler.startCompiler(target.getLocation().toFile(), files, store1);
		}
	}

	/**
	 * Finds the template in the plug-in. Returns the template plug-in URI.
	 * 
	 * @param bundleID
	 *            is the plug-in ID
	 * @param relativePath
	 *            is the relative path of the template in the plug-in
	 * @return the template URI
	 * @throws IOException
	 * @generated
	 */
	private URI getTemplateURI(String bundleID, IPath relativePath) throws IOException {
		Bundle bundle = Platform.getBundle(bundleID);
		if (bundle == null) {
			// no need to go any further
			return URI.createPlatformResourceURI(new Path(bundleID).append(relativePath).toString(), false);
		}
		URL url = bundle.getEntry(relativePath.toString());
		if (url == null && relativePath.segmentCount() > 1) {
			Enumeration<URL> entries = bundle.findEntries("/", "*.emtl", true);
			if (entries != null) {
				String[] segmentsRelativePath = relativePath.segments();
				while (url == null && entries.hasMoreElements()) {
					URL entry = entries.nextElement();
					IPath path = new Path(entry.getPath());
					if (path.segmentCount() > relativePath.segmentCount()) {
						path = path.removeFirstSegments(path.segmentCount() - relativePath.segmentCount());
					}
					String[] segmentsPath = path.segments();
					boolean equals = segmentsPath.length == segmentsRelativePath.length;
					for (int i = 0; equals && i < segmentsPath.length; i++) {
						equals = segmentsPath[i].equals(segmentsRelativePath[i]);
					}
					if (equals) {
						url = bundle.getEntry(entry.getPath());
					}
				}
			}
		}
		URI result;
		if (url != null) {
			result = URI.createPlatformPluginURI(new Path(bundleID).append(new Path(url.getPath())).toString(), false);
		} else {
			result = URI.createPlatformResourceURI(new Path(bundleID).append(relativePath).toString(), false);
		}
		return result;
	}

	public List<String> getFiles() {
		return files;
	}

}
