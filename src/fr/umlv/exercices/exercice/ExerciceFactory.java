package fr.umlv.exercices.exercice;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ExerciceFactory {
	
	/**
	 * Factory that create an Exercise with the path
	 * @param path Path to the file
	 * @return an Exercice
	 * @throws IOException If path doesn't exist
	 */
	public static Exercice createExercice(Path path) throws IOException {


		try (Stream<String> lines = Files.lines(path)) {

			List<String> htmlLines = Parser.parseMarkdownLinesToHtmlLines(lines);

			return new Exercice(htmlLines.get(0), createQuestions(htmlLines));
		}

	}

	static List<Question> createQuestions(List<String> htmlLines) { 
		List<String> questions = htmlLines.subList(1, htmlLines.size());

		return questions.stream().map(Question::new)
				.collect(Collectors.toList());
	}
}