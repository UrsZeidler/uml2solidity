/**
 * 
 */
package de.urszeidler.eclipse.solidity.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;

import de.urszeidler.eclipse.solidity.ui.Activator;
import de.urszeidler.eclipse.solidity.ui.preferences.PreferenceConstants;

/**
 * @author urs
 *
 */
public class StartCompiler {

	public static void startCompiler(File outFile, List<String> src) {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();

		String command = store.getString(PreferenceConstants.COMPILER_PROGRAMM);

		ArrayList<String> list = new ArrayList<String>();
		list.add(command);
		if (store.getBoolean(PreferenceConstants.COMPILER_BIN))
			list.add("--bin");
		if (store.getBoolean(PreferenceConstants.COMPILER_ABI))
			list.add("--abi");
		if (store.getBoolean(PreferenceConstants.COMPILER_AST))
			list.add("--ast");
		if (store.getBoolean(PreferenceConstants.COMPILER_AST_JSON))
			list.add("--ast-json");
		if (store.getBoolean(PreferenceConstants.COMPILER_ASM))
			list.add("--asm");
		if (store.getBoolean(PreferenceConstants.COMPILER_ASM_JSON))
			list.add("--asm-json");
		if (store.getBoolean(PreferenceConstants.COMPILER_INTERFACT))
			list.add("--interface");
		if (store.getBoolean(PreferenceConstants.COMPILER_USERDOC))
			list.add("--userdoc");
		if (store.getBoolean(PreferenceConstants.COMPILER_DEVDOC))
			list.add("--devdoc");
		if (store.getBoolean(PreferenceConstants.COMPILER_BIN_RUNTIME))
			list.add("--bin-runtime");
		if (store.getBoolean(PreferenceConstants.COMPILER_OPCODE))
			list.add("--opcode");
		if (store.getBoolean(PreferenceConstants.COMPILER_FORMAL))
			list.add("--formal");
		if (store.getBoolean(PreferenceConstants.COMPILER_HASHES))
			list.add("--hashes");
		
		list.add("-o");
		list.add(outFile.getAbsolutePath());
		list.addAll(src);
		System.out.println(list);

		ProcessBuilder processBuilder = new ProcessBuilder(list);

		try {
			Process start = processBuilder.start();
//			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(start.getInputStream()));
//			BufferedReader bufferedReader1 = new BufferedReader(new InputStreamReader(start.getErrorStream()));
//			String readLine = bufferedReader.readLine();
//			System.out.println(readLine);
//			System.out.println(bufferedReader1.readLine());
			// System.out.println(bufferedReader1.readLine());
			// while (bufferedReader.)
			// System.out.println(bufferedReader1.readLine());

			int exitValue = start.waitFor();
			// int exitValue = start.exitValue();
//			System.out.println("-->" + exitValue);
		} catch (IOException e) {
			Activator.logError("Error ", e);
		} catch (InterruptedException e) {
			Activator.logError("Error ", e);
		}

	}

}
