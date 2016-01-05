/**
 * 
 */
package de.urszeidler.eclipse.solidity.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author urs
 *
 */
public class StartCompiler {

	public void startCompiler(File outFile, File src) {
		String command = "/usr/bin/solc";
		ProcessBuilder processBuilder = new ProcessBuilder(command,"--bin","--userdoc","--abi", "-o ",outFile.getAbsolutePath(),src.getAbsoluteFile().toString());
		processBuilder.directory(outFile);
		processBuilder.redirectError(new File("eth-error.log"));

		try {
			Process start = processBuilder.start();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(start.getInputStream()));
			BufferedReader bufferedReader1 = new BufferedReader(new InputStreamReader(start.getErrorStream()));
			String readLine = bufferedReader.readLine();
			System.out.println(readLine);
			System.out.println(bufferedReader1.readLine());
			int exitValue =start.waitFor();
//			int exitValue = start.exitValue();
			System.out.println("-->"+exitValue);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
