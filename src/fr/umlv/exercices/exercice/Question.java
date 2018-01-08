package fr.umlv.exercices.exercice;

import java.util.Objects;

class Question {
	private String question;

	Question(String question) {
		this.question = Objects.requireNonNull(question);
	}

	String toJsonQuestion() {
		
		return "{\"question\":\"" + this.question + "\" }";

	}

}