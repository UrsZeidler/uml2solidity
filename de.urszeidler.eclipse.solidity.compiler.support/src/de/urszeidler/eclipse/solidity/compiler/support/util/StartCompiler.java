/**
 * 
 */
package de.urszeidler.eclipse.solidity.compiler.support.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
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

		// @Override
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

	/**
	 * 
	 *
	 */
	public interface CompilerCallback {
		/**
		 * Called after the compiler is called.
		 * The input is null when an error occurs.
		 * 
		 * @param input the string of the input stream
		 * @param error the string for the error stream
		 * @param exception if an exception is raised
		 */
		void compiled(String input, String error, Exception exception);
	}

	public static void startCompiler(File outFile, List<String> src) {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		startCompiler(outFile, src, store);
	}

	public static void startCompiler(File outFile, List<String> src, IPreferenceStore store) {
		startCompiler(outFile, src, store, null);
	}

	public static void startCompiler(File outFile, List<String> src, IPreferenceStore store,
			CompilerCallback callback) {
		if (src == null || src.isEmpty())
			return;
		String command = store.getString(PreferenceConstants.COMPILER_PROGRAMM);
		if (command == null || command.isEmpty())
			return;
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
		if (store.getBoolean(PreferenceConstants.COMBINED_JSON)){
			list.add("--combined-json");
			list.add(store.getString(PreferenceConstants.COMBINED_JSON_OPTIONS));
		}
		
		
		startCompiler(outFile, src, callback, list);
	}

	/**
	 * @param outFile
	 * @param src
	 * @param store
	 * @param callback
	 * @param options
	 */
	public static void startCompiler(File outFile, List<String> src, CompilerCallback callback,
			List<String> options) {
		options.add("-o");
		options.add(outFile.getAbsolutePath());
		startCompiler(src, callback, options);
	}

	/**
	 * @param src
	 * @param callback
	 * @param options
	 */
	public static void startCompiler(List<String> src, CompilerCallback callback, List<String> options) {
		options.addAll(src);

		ProcessBuilder processBuilder = new ProcessBuilder(options);
		try {
			Process start = processBuilder.start();
			final BufferedReader errorReader = new BufferedReader(new InputStreamReader(start.getErrorStream()));
			final BufferedReader outReader = new BufferedReader(new InputStreamReader(start.getInputStream()));

			FutureTask<String> futureTaskErrorReader = new FutureTask<String>(new CallableImplementation(errorReader));
			Executors.newSingleThreadExecutor().execute(futureTaskErrorReader);
			FutureTask<String> futureTaskOutReader = new FutureTask<String>(new CallableImplementation(outReader));
			Executors.newSingleThreadExecutor().execute(futureTaskOutReader);

			int exitValue = start.waitFor();
			String errorMessage = futureTaskErrorReader.get();
			String message = futureTaskOutReader.get();
			if (exitValue != 0) {
				if (callback != null)
					callback.compiled(null, errorMessage, null);
				else
					Activator.logInfo(errorMessage);
			}
			if (!message.isEmpty()) {
				
				if (callback != null)
					callback.compiled(message, errorMessage, null);
				else
					Activator.logInfo(message);
			}
		} catch (Exception e) {
			if (callback != null)
				callback.compiled(null, null, e);
			else
				Activator.logError("Error ", e);
		}
	}

}
