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
import java.util.ArrayList;
import java.util.List;

import org.eclipse.acceleo.engine.service.AbstractAcceleoGenerator;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.emf.common.util.BasicMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.preference.IPreferenceStore;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

import de.urszeidler.eclipse.solidity.compiler.support.util.StartCompiler;
import de.urszeidler.eclipse.solidity.templates.GenerateContracts;
import de.urszeidler.eclipse.solidity.ui.Activator;
import de.urszeidler.eclipse.solidity.util.Uml2Service;

/**
 * Main entry point of the 'Solidity' generation module.
 */
public class GenerateAll {

	private static final String UM2SOLIDITY_EXTENSION_POINT_ID = "de.urszeidler.eclipse.solidity.um2solidity.m2t";

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

	private class GeneratorAction {

		String id;
		String generatorName;
		IFolder targetFolder;
		AbstractAcceleoGenerator generator;
		int work;

		public GeneratorAction(String id, String generatorName, IFolder targetFolder,
				AbstractAcceleoGenerator generator, int work) {
			super();
			this.id = id;
			this.generatorName = generatorName;
			this.targetFolder = targetFolder;
			this.generator = generator;
			this.work = work;
		}
	}

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

	public void doGenerateByExtension(IProgressMonitor monitor1) {
		IPreferenceStore store = Uml2Service.getStore(null);

		List<GeneratorAction> list = getGeneratorList(monitor1, store);
		int completeWork = 0;
		for (GeneratorAction generatorAction : list) {
			completeWork += generatorAction.work;
		}

		SubMonitor monitor = SubMonitor.convert(monitor1, "start generation", completeWork);
		monitor.beginTask("start generation", completeWork);
		for (GeneratorAction generatorAction : list) {
			File file = generatorAction.targetFolder.getLocation().toFile();
			monitor.subTask("Initalize :" + generatorAction.generatorName);
			try {
				generatorAction.generator.initialize(modelURI, file, arguments);
			} catch (IOException e) {
				Activator.logError("Error while initalize generator:" + generatorAction.id, e);
			}
			monitor.worked(1);

			String id = generatorAction.generator.getClass().getCanonicalName();
			String generationID = org.eclipse.acceleo.engine.utils.AcceleoLaunchingUtil.computeUIProjectID(
					"de.urszeidler.eclipse.solidity.ui", id, modelURI.toString(),
					generatorAction.targetFolder.getFullPath().toString(), new ArrayList<String>());
			generatorAction.generator.setGenerationID(generationID);
			monitor.subTask("Generate :" + generatorAction.generatorName);
			try {
				generatorAction.generator.doGenerate(BasicMonitor.toMonitor(monitor));
			} catch (IOException e) {
				Activator.logError("Error while do generation:" + generatorAction.id);
			}
			if (monitor.isCanceled())
				break;

			monitor.worked(generatorAction.work);
		}

		Optional<GeneratorAction> first = FluentIterable.from(list).filter(new Predicate<GeneratorAction>() {
			public boolean apply(GeneratorAction input) {
				return "".equals(input);
			}
		}).first();
		if(first.isPresent())
			compileContracts(monitor, (GenerateContracts) first.get().generator);
	}

	/**
	 * Creates the list of enabled generators.
	 * 
	 * @param monitor
	 * @param store
	 * @return
	 */
	private List<GeneratorAction> getGeneratorList(IProgressMonitor monitor1, IPreferenceStore store) {
		IConfigurationElement[] configurationElements = Platform.getExtensionRegistry()
				.getConfigurationElementsFor(UM2SOLIDITY_EXTENSION_POINT_ID);
		SubMonitor monitor = SubMonitor.convert(monitor1, "prepare generation", 10);

		List<GeneratorAction> list = new ArrayList<GeneratorAction>();
		for (IConfigurationElement element : configurationElements) {
			try {
				AbstractAcceleoGenerator generator = (AbstractAcceleoGenerator) element
						.createExecutableExtension("generator_class");

				String id = element.getAttribute("generator_id");
				String name = element.getAttribute("generator_name");
				String work_str = element.getAttribute("estimated_work");
				String targetFolderPref = element.getAttribute("generator_target");

				IFolder folder = getGenerationTargetFolder(store, targetFolderPref, monitor);
				int work = 10;
				try {
					if (work_str != null)
						work = Integer.parseInt(work_str);
				} catch (NumberFormatException e) {
				}
				if (store.getBoolean(id) && folder != null && folder.getLocation().toFile().exists())
					list.add(new GeneratorAction(id, name, folder, generator, work));
			} catch (Exception e) {
				Activator.logError("Error while instantiate generator.", e);
			}
		}
		return list;
	}

	/**
	 * Get the folder for a generation target, will create the folder when not
	 * exist.
	 * 
	 * @param store
	 * @param targetDirectory
	 * @param monitor
	 * @return
	 * @throws CoreException
	 */
	private IFolder getGenerationTargetFolder(IPreferenceStore store, String targetDirectory, IProgressMonitor monitor)
			throws CoreException {
		final String docTarget = store.getString(targetDirectory);
		IFolder folder = null;
		if (docTarget.startsWith("/")) {
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			Path location = new Path(docTarget);
			folder = root.getFolder(location);
			if (!folder.exists()) {
				createFolders(ResourcesPlugin.getWorkspace().getRoot(), location, monitor, 0);
			}
		} else {
			folder = targetFolder.getProject().getFolder(docTarget);
			Path location = new Path(docTarget);
			if (!folder.exists())
				createFolders(targetFolder.getProject(), location, monitor, 1);
		}
		return folder;
	}

	/**
	 * Creates the folder.
	 * 
	 * @param root
	 * @param location
	 * @param monitor
	 * @param j
	 *            if relative path to project use 1, when relative to workspace
	 *            root use 0
	 * @throws CoreException
	 */
	private void createFolders(IContainer root, Path location, IProgressMonitor monitor, int j) throws CoreException {
		String[] segments = location.segments();
		if (segments.length < (3 - j)) {
			IFolder folder2 = root.getFolder(location);
			if (!folder2.exists())
				folder2.create(true, true, monitor);
			return;
		}

		IPath bPath = location.removeLastSegments(segments.length - (2 - j));
		IFolder folder = root.getFolder(bPath);
		if (!folder.exists())
			folder.create(true, true, monitor);

		for (int i = (2 - j); i < segments.length; i++) {
			String string = segments[i];
			IPath path = bPath.append(string);
			IFolder folder2 = root.getFolder(path);
			if (!folder2.exists())
				folder2.create(true, true, monitor);
		}
	}

	/**
	 * If enable compile the generated contracts.
	 * 
	 * @param monitor
	 * @param gen0
	 */
	private void compileContracts(IProgressMonitor monitor, GenerateContracts gen0) {
		if (gen0 == null)
			return;
		IPreferenceStore store1 = Uml2Service.getStore(null);
		if (store1.getBoolean(
				de.urszeidler.eclipse.solidity.compiler.support.preferences.PreferenceConstants.COMPILE_CONTRACTS)) {

			files = gen0.getFiles();
			if (files == null || files.isEmpty())
				return;
			String compile_folder = store1.getString(
					de.urszeidler.eclipse.solidity.compiler.support.preferences.PreferenceConstants.COMPILER_TARGET);

			//
			IContainer target = null;
			if (compile_folder.startsWith("/")) {
				target = (IContainer) ResourcesPlugin.getWorkspace().getRoot().findMember(new Path(compile_folder));
			} else
				target = targetFolder.getProject().getFolder(compile_folder);
			if (!target.getLocation().toFile().exists()) {
				target.getLocation().toFile().mkdirs();
			}
			if (monitor != null)
				monitor.subTask("compile code");
			StartCompiler.startCompiler(target.getLocation().toFile(), files, store1);
		}
	}

	public List<String> getFiles() {
		return files;
	}

}
