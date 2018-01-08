package fr.umlv.evaluation;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import jdk.jshell.Diag;
import jdk.jshell.JShell;
import jdk.jshell.Snippet;
import jdk.jshell.Snippet.Status;
import jdk.jshell.SourceCodeAnalysis.CompletionInfo;

public class EvaluationCode {
	
	private static CompletionInfo nextCpltion(CompletionInfo completionInfo,JShell jshell) {

		return jshell.sourceCodeAnalysis().analyzeCompletion(completionInfo.remaining);
	}

	private static void displayDiagnosticsError(Snippet snippet, JShell jshell) {
		final Object monitor = new Object();

		System.out.println("Error:");
		jshell.diagnostics(snippet).stream().filter(Diag::isError)
				.forEach(diag -> {
					synchronized (monitor) {
						System.out.println(diag);
					}
				});
	}

	/**
	 * evalCode
	 * 
	 * make the evaluation of all snippet in the code
	 * 
	 * @param theCode the string corresponding to the code that will be give to the
	 *            function
	 */
	private static void displayErrors(JShell jshell) {
		jshell.snippets().stream().filter(snip -> (jshell.status(snip) != Status.VALID))
				.forEach(snip -> displayDiagnosticsError(snip, jshell));
	}

	private static void evalCode(String totalCode) {
		try (JShell jshell = JShell.create()) {
			final Object monitor = new Object();

			CompletionInfo cpltion = jshell.sourceCodeAnalysis().analyzeCompletion(totalCode);

			/* those field ( source and reamnin ) is filed of jshell and his cas
			 giv the possibility to access those field  this specificity come from API Jshell */
			
			for (; cpltion.source.length() != cpltion.remaining.length(); cpltion = nextCpltion(cpltion, jshell)) {
				synchronized (monitor) {
					jshell.eval(cpltion.source);
				}
			}
			displayErrors(jshell);
			// close useless cause we do try-with-ressource
		}

	}

	/**Get the String result of the evaluation
	  * @param code The code to evaluate
	  * @return The String result
	  */
	public static String getStringEvaluation(String code) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		// Save the old System.out!//debug
		PrintStream old = System.out;
		// Tell Java to use your special stream//debug
		System.setOut(ps);
		evalCode(code);
		System.out.flush();
		System.setOut(old);
		return baos.toString();
	}

}