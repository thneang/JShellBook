package fr.umlv.exercices.exercice;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public class Exercice {
	private String introducing;
	private List<Question> questions;
	private final Object lock = new Object();


	private String toPartialJSon() {
		synchronized(this.lock){
			if (this.questions.isEmpty() ){
				return "";

			}
			return this.questions.stream().map(question -> question.toJsonQuestion())
					.reduce("", (x, y) -> x + "," + y).substring(1);
					
		}
	}





	Exercice(String introducing, List<Question> questions) {
		this.questions = questions;  // null already checked in create factory
		this.introducing = introducing; // null already checked in create factory

	}

	/**
	 * 
	 * @return The String that represent the Object
	 */
	public String toJSon() {

		synchronized(this.lock){
			return "{\"introducing\":\"" + this.introducing + "\",\"questions\":["
					+ toPartialJSon() + "]}";
		}
	}


	/**
	 * Update exercise with parse,change field instead of create a new instance.
	 * @param path The path for the file
	 * @throws IOException If path doesn't exist
	 */
	public void updatByParsing(Path path) throws IOException {// comme ça on ne refait pas un new a chaque fois
		synchronized(this.lock){
			try (Stream<String> lines = Files.lines(path)) {
				List<String> htmlLines = Parser.parseMarkdownLinesToHtmlLines(lines);	
				
				update(htmlLines.get(0),ExerciceFactory.createQuestions(htmlLines));
	
			}
		}
	
	}
	private void update(String introducing, List<Question> questions) {
		synchronized(this.lock){
			this.questions = questions; // null already checked
			this.introducing = introducing; // null already checked

		}
	}

}