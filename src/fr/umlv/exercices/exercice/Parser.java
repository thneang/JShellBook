package fr.umlv.exercices.exercice;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.pegdown.PegDownProcessor;

public class Parser {

	// we have to delete "\n", if else we will ave problem with the jsonn format 
	private static String securMessage ( String message ){
		return message.replaceAll("\"", "").replaceAll("'", "").replaceAll("\n", "");
	}
	private static String parseMarkdownToHtml(String markdownMessage) {
		PegDownProcessor p = new PegDownProcessor();

		return securMessage(p.markdownToHtml(markdownMessage));
	}


	private static String removeBalisesFromStringHtml( String exoHtml , String[] balises ){
		String theExoHtml = exoHtml ;
		for (String balise : balises) {
			theExoHtml = theExoHtml.replaceAll(balise,"");
		}
		return theExoHtml ;
	}

	private static List<String> parseHtmlLinesByBalise(String exoInHtml) {
		String [] balises = {"</ol>","</li>","<li>","<h2>","</h2>","<p>","</p>"} ;

		return Arrays.asList(removeBalisesFromStringHtml(exoInHtml,balises).split("<ol>"));


	}
	

	
	static List<String> parseMarkdownLinesToHtmlLines(Stream<String> lines) {
		
		return parseHtmlLinesByBalise(lines
				.map(Parser::parseMarkdownToHtml)
				.reduce("", (x, y) -> x + y));
	}


}
