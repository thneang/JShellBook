package fr.umlv.exercices;

import io.vertx.core.json.JsonObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Objects;

public class ExercisesManager {

	// the field is well used, findbug found a warning here because the filed is
	// used only in param of methode
	private final Exercices exercices;

	/**
	 * ExercisesManager
	 * 
	 * Create an exerciseManarde to do modification on exercise
	 * 
	 * @param dir the directory where we to our selection of file to build exercice
	 * @return The ExerciseMangaer to update exercise
	 */
	public static ExercisesManager createExercisesManager(File dir) {
		Objects.requireNonNull(dir);
		return new ExercisesManager(Exercices.createExercices(dir));
	}

	private ExercisesManager(Exercices exercices) {
		this.exercices = exercices;
	}

	private String readapteType(String idExercice, String type) {
		boolean isExistingFile = this.exercices.isAlreadyExisting(idExercice);
		if (type.equals("ENTRY_CREATE") && isExistingFile) {
			return "ENTRY_MODIFY";
		} else if (type.equals("ENTRY_MODIFY") && !isExistingFile) {
			return "ENTRY_CREATE";
		}
		return type;
	}

	/**
	 * treatMessage
	 * 
	 * acording the the message , the method will product aor indicate to
	 * product some change to consutumeur and to exercises
	 * 
	 * @param message
	 *            The message receive
	 * @return JsonObjet The JsonObject wit hthe response
	 * @throws IOException Because of the Path.get
	 */
	public JsonObject treatMessage(Object message) throws IOException {
		JsonObject json = new JsonObject(message.toString());
	
	
		String name = json.getString("name");

		String type = readapteType(name, json.getString("type"));

		json.remove("type");
		json.put("type", type);
		
		return treatJsonExercice(json, name, type);
	}

	private JsonObject treatJsonExercice(JsonObject json, String name,String type) throws IOException {
		JsonObject noJson = new JsonObject().put("empty", " ");
		switch (type) {
		case "ENTRY_CREATE":
			this.exercices.addIdExercice(name);break;
		case "ENTRY_MODIFY":
			if (!this.exercices.isAldreadyLoaded(name))  return noJson;
			this.exercices.getOrGetAddExerciceById(name).updatByParsing(Paths.get("./" + name));
			JsonObject jsonTmp = new JsonObject(this.exercices.getOrGetAddExerciceById(name).toJSon());
			json.put("newContain", jsonTmp);
			break;	
		case "ENTRY_DELETE":
			this.exercices.removeExercice(name);break;
		default: return noJson;
		}
		return json;
	}

	private static String buildUrlExercice(int port, String idExercice) {
		return "<a href =\"http://localhost:" + port
				+ "/exercise.html?exercise=" + idExercice
				+ "\" target =\"_blank\" id=\"" + idExercice + "\">"
				+ idExercice + "</a>" + "<br>";
	}

	/**
	 * getListTotalIdExercices
	 * 
	 * rerturn a string with all exercices ( which are in markdown )
	 * 
	 * @param port The port of the Server to create url
	 * @return A String corresponding of the url
	 */
	public String getMessageListExercice(int port) {
		return this.exercices.getListTotalIdExercices().stream()
				.map(name -> buildUrlExercice(port, name))
				.reduce("", (x, y) -> x + y);
	}

	/**
	 * 
	 * getMessageExercice
	 * 
	 * return and string of toJson exercice
	 * 
	 * 
	 * @param idExercice
	 *            identifier to an exercice
	 * @return String The exercise in Json
	 * @throws IOException Because of a Path.get in Exercises
	 */
	public String getMessageExercice(String idExercice) throws IOException {
		return this.exercices.getOrGetAddExerciceById(idExercice).toJSon();
	}

	/**
	 * closeExerciceOnWindow
	 * 
	 * call a function to manage eventual freeing of exercice insgtance if all
	 * window has been cleosed
	 * 
	 * @param idExercice
	 *            identifier to an exercice
	 */
	public void closeExerciceOnWindow(String idExercice) {
		this.exercices.closeWindowOfExercicese(idExercice);
	}

}