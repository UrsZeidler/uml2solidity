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

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.util.BasicMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.preference.IPreferenceStore;
import org.osgi.framework.Bundle;

import de.urszeidler.eclipse.solidity.compiler.support.util.StartCompiler;
import de.urszeidler.eclipse.solidity.templates.GenerateContracts;
import de.urszeidler.eclipse.solidity.templates.GenerateHtml;
import de.urszeidler.eclipse.solidity.templates.GenerateMarkDown;
import de.urszeidler.eclipse.solidity.templates.GenerateMixConfig;
import de.urszeidler.eclipse.solidity.templates.GenerateWeb3Contract;
import de.urszeidler.eclipse.solidity.ui.preferences.PreferenceConstants;

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
		IPreferenceStore store = PreferenceConstants.getPreferenceStore(targetFolder.getProject());		
		
		monitor.subTask("Loading...");
		GenerateContracts gen0 = new GenerateContracts(modelURI, targetFolder.getLocation().toFile(), arguments);
		monitor.worked(1);
		String generationID = org.eclipse.acceleo.engine.utils.AcceleoLaunchingUtil.computeUIProjectID(
				"de.urszeidler.eclipse.solidity.ui", "de.urszeidler.eclipse.solidity.templates.GenerateContracts",
				modelURI.toString(), targetFolder.getFullPath().toString(), new ArrayList<String>());
		gen0.setGenerationID(generationID);
		monitor.subTask("generate solidity");
		gen0.doGenerate(BasicMonitor.toMonitor(monitor));
		if(monitor.isCanceled())return;
		if (store.getBoolean(PreferenceConstants.GENERATE_HTML)) {
			GenerateHtml generateHtml = new GenerateHtml(modelURI, targetFolder.getLocation().toFile(), arguments);
			monitor.worked(1);
			generationID = org.eclipse.acceleo.engine.utils.AcceleoLaunchingUtil.computeUIProjectID(
					"de.urszeidler.eclipse.solidity.ui", "de.urszeidler.eclipse.solidity.templates.GenerateHtml",
					modelURI.toString(), targetFolder.getFullPath().toString(), new ArrayList<String>());
			generateHtml.setGenerationID(generationID);
			monitor.subTask("generate html");
			generateHtml.doGenerate(BasicMonitor.toMonitor(monitor));
		}
		if(monitor.isCanceled())return;
		if (store.getBoolean(PreferenceConstants.GENERATE_WEB3)) {
			GenerateWeb3Contract generateWeb3Contract = new GenerateWeb3Contract(modelURI,
					targetFolder.getLocation().toFile(), arguments);
			monitor.worked(1);
			generationID = org.eclipse.acceleo.engine.utils.AcceleoLaunchingUtil.computeUIProjectID(
					"de.urszeidler.eclipse.solidity.ui",
					"de.urszeidler.eclipse.solidity.templates.GenerateWeb3Contract", modelURI.toString(),
					targetFolder.getFullPath().toString(), new ArrayList<String>());
			generateWeb3Contract.setGenerationID(generationID);
			monitor.subTask("generate web3");
			generateWeb3Contract.doGenerate(BasicMonitor.toMonitor(monitor));
		}
		if(monitor.isCanceled())return;
		if (store.getBoolean(PreferenceConstants.GENERATE_MIX)) {
			GenerateMixConfig generateWeb3Contract = new GenerateMixConfig(modelURI,
					targetFolder.getLocation().toFile(), arguments);
			monitor.worked(1);
			generationID = org.eclipse.acceleo.engine.utils.AcceleoLaunchingUtil.computeUIProjectID(
					"de.urszeidler.eclipse.solidity.ui", "de.urszeidler.eclipse.solidity.templates.GenerateMixConfig",
					modelURI.toString(), targetFolder.getFullPath().toString(), new ArrayList<String>());
			generateWeb3Contract.setGenerationID(generationID);
			monitor.subTask("generate mix");
			generateWeb3Contract.doGenerate(BasicMonitor.toMonitor(monitor));
		}

		final String docTarget = store.getString(PreferenceConstants.GENERATION_TARGET_DOC);
		IContainer target1 = targetFolder.getProject().getFolder(docTarget);
		if (!target1.getLocation().toFile().exists()) {
			target1.getLocation().toFile().mkdirs();
		}

		if (store.getBoolean(PreferenceConstants.GENERATE_MARKDOWN)) {
			GenerateMarkDown generateWeb3Contract = new GenerateMarkDown(modelURI, target1.getLocation().toFile(),
					arguments);
			monitor.worked(1);
			generationID = org.eclipse.acceleo.engine.utils.AcceleoLaunchingUtil.computeUIProjectID(
					"de.urszeidler.eclipse.solidity.ui", "de.urszeidler.eclipse.solidity.templates.GenerateMarkDown",
					modelURI.toString(), targetFolder.getFullPath().toString(), new ArrayList<String>());
			generateWeb3Contract.setGenerationID(generationID);
			monitor.subTask("generate markdown");
			generateWeb3Contract.doGenerate(BasicMonitor.toMonitor(monitor));
		}
		IPreferenceStore store1 = de.urszeidler.eclipse.solidity.compiler.support.preferences.PreferenceConstants.getPreferenceStore(targetFolder.getProject());
		if (store1.getBoolean(
				de.urszeidler.eclipse.solidity.compiler.support.preferences.PreferenceConstants.COMPILE_CONTRACTS)) {
			
			files = gen0.getFiles();
			if(files.isEmpty())return;
			String compile_folder = store1.getString(
							de.urszeidler.eclipse.solidity.compiler.support.preferences.PreferenceConstants.COMPILER_TARGET);
			IContainer target = targetFolder.getProject().getFolder(compile_folder);
			if (!target.getLocation().toFile().exists()) {
				target.getLocation().toFile().mkdirs();
			}
			monitor.subTask("compile code");
			StartCompiler.startCompiler(target.getLocation().toFile(), files,store1);
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
