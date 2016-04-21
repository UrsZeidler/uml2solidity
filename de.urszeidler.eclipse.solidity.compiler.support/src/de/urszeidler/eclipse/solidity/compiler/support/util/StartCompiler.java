/**
 * 
 */
package de.urszeidler.eclipse.solidity.compiler.support.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import org.eclipse.jface.preference.IPreferenceStore;

import de.urszeidler.eclipse.solidity.compiler.support.Activator;
import de.urszeidler.eclipse.solidity.compiler.support.preferences.PreferenceConstants;

/**
 * @author urs
 *
 */
public class StartCompiler {

	private static final class CallableImplementation implements Callable<String> {
		private final BufferedReader reader;

		private CallableImplementation(BufferedReader errorReader) {
			this.reader = errorReader;
		}

		//@Override
		public String call() throws Exception {
			StringBuffer buffer = new StringBuffer();
			String s = reader.readLine();
			while (s != null) {
				buffer.append(s);
				buffer.append("\n");
				s = reader.readLine();
			}
			return buffer.toString();
		}
	}

	public static void startCompiler(File outFile, List<String> src) {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();

		String command = store.getString(PreferenceConstants.COMPILER_PROGRAMM);
		startCompiler(outFile, src, store, command);
	}

	public static void startCompiler(File outFile, List<String> src, IPreferenceStore store, String command) {
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

		startCompiler(outFile, src, list);
	}

	public static void startCompiler(File outFile, List<String> src, List<String> list) {
		list.add("-o");
		list.add(outFile.getAbsolutePath());
		list.addAll(src);

		ProcessBuilder processBuilder = new ProcessBuilder(list);
		try {
			Process start = processBuilder.start();
			final BufferedReader errorReader = new BufferedReader(new InputStreamReader(start.getErrorStream()));
			final BufferedReader outReader = new BufferedReader(new InputStreamReader(start.getInputStream()));

			FutureTask<String> futureTaskErrorReader = new FutureTask<String>(new CallableImplementation(errorReader));
			futureTaskErrorReader.run();
			FutureTask<String> futureTaskOutReader = new FutureTask<String>(new CallableImplementation(outReader));
			futureTaskOutReader.run();

			int exitValue = start.waitFor();
			if (exitValue != 0) {
				String errorMessage = futureTaskErrorReader.get();
				Activator.logInfo(errorMessage);
			}
			String message = futureTaskOutReader.get();
			if(!message.isEmpty())
				Activator.logInfo(message);
		} catch (IOException e) {
			Activator.logError("Error ", e);
		} catch (InterruptedException e) {
			Activator.logError("Error ", e);
		} catch (ExecutionException e) {
			Activator.logError("Error ", e);
		}
	}

}
