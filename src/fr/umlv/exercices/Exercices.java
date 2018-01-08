package fr.umlv.exercices;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import fr.umlv.exercices.exercice.Exercice;
import fr.umlv.exercices.exercice.ExerciceFactory;
import fr.umlv.tools.FileManager;


public class Exercices {
	
	// id of each exercices, even if one of theme are note used , and values are used to know how many user used a exercice
	// Integer -> to know how many browser to the exercice.
	
	
	private final ConcurrentHashMap<String,Integer> mapTotalIdExercices; 
	
	// used exercuces
	private final ConcurrentHashMap<String, Exercice> mapUsedExercices = new ConcurrentHashMap<>() ;
	private final Object monitor = new Object();
	
	/**Factory, create exercices with dir.
	 * 
	 * 
	 * @param dir The directory containing the exercises
	 * @throws NullPointerException if dir is null
	 * @throws IllegalStateException if dir is and ompty directory
	 * @return Exercices
	 */
	public static Exercices createExercices ( File dir ){

		Objects.requireNonNull(dir);
		File[] theFiles =  dir.listFiles(FileManager::isMdFile);
		if(theFiles == null ){
			throw new IllegalStateException("Empty dir");
		}
		ConcurrentHashMap<String,Integer> tmpMaptotalIdExercices= new ConcurrentHashMap<>();
		
		Arrays.asList(theFiles).stream().map(File::getName)
		.forEach( idFile -> tmpMaptotalIdExercices.put(idFile, 0) );

		return new Exercices( tmpMaptotalIdExercices );
	}
	private Exercices(  ConcurrentHashMap<String,Integer> maptotalIdExercices ) {
		this.mapTotalIdExercices = maptotalIdExercices ;
	}
	
	private Exercice addUsedExercice( String idExercice , Exercice exercice ){
		synchronized(this.monitor){
			Objects.requireNonNull(idExercice);//have to be checked
			Objects.requireNonNull(this.mapUsedExercices);//have to be checked
			this.mapUsedExercices.put(idExercice, exercice);
			
			return exercice ;
		}
	}
	
	/**Add a new Exercise in the map
	 * 
	 * @param id The name of the exercise
	 */
	public void addIdExercice( String id ){
		this.mapTotalIdExercices.put(id, 0);
	}
	
	
	/**
	 * Return a unmodifiable list of all the exercise
	 * @return The list of all exercises in the directory
	 */
	public List<String> getListTotalIdExercices ( ){
		synchronized(this.monitor){
			List<String> listTotalIdExercices = new ArrayList<String>();
			this.mapTotalIdExercices.forEachKey(1, idExercice ->  listTotalIdExercices.add(idExercice));

			return Collections.unmodifiableList(listTotalIdExercices);
		}
	}
	
	

	/**
	 * Get the exercises if load or add it to the list and return it
	 * @param idExercice The name of the exercise
	 * @return The exercise
	 * @throws IOException Because of the path.get in the method
	 */
	public Exercice getOrGetAddExerciceById( String idExercice ) throws IOException{
		synchronized(this.monitor){
			this.mapTotalIdExercices.merge(idExercice, 1, Integer::sum);
			
			return this.mapUsedExercices.getOrDefault(idExercice, 
					addUsedExercice(idExercice, ExerciceFactory.createExercice(Paths.get("./"+idExercice)) ));
		}
		
	}
	/**
	 * Return the exercise
	 * @param idExercice The name of the exercise
	 * @return The exercise
	 */
	public Exercice getExercice(String idExercice  ){
		return this.mapUsedExercices.get(idExercice);
	}

	/**
	 * Unload the exercise
	 * @param idExercice The name
	 */
	public void removeUsedExercice( String idExercice ){
		
		this.mapUsedExercices.remove(idExercice);
	}
	
	/**
	 * Remove the exercises if remove from disk
	 * @param idExercice The name
	 */
	public void removeExercice( String idExercice ){
		synchronized(this.monitor){
			removeUsedExercice(idExercice);
	
			this.mapTotalIdExercices.remove(idExercice);
		}
	
	}
	
	/**
	 * If delete exercises unload it and close all windows
	 * @param idExercice The name
	 */
	public void closeWindowOfExercicese(String idExercice ){
		
		synchronized(this.monitor){

			
			// if exercise has been deleted or doesn'nt existe we can't to nexts actions in this method 
			if( ! this.mapTotalIdExercices.containsKey(idExercice) ) return ;
			
			this.mapTotalIdExercices.replace(idExercice, this.mapTotalIdExercices.get(idExercice)-1 );
			if( 0 == this.mapTotalIdExercices.get(idExercice) ){
				removeUsedExercice(idExercice);
			}
			
		}
	}
	
	/**
	 * Return if the file is existing
	 * @param idExercice the name
	 * @return true if existing in the map
	 */
	public boolean isAlreadyExisting(String idExercice ) {
		return this.mapTotalIdExercices.containsKey(idExercice);
	}
	
	/**
	 * Check if the exercise is load
	 * @param idExercice The name
	 * @return True if already load
	 */
	public boolean isAldreadyLoaded(String idExercice ){
		return this.mapUsedExercices.containsKey(idExercice);
	}
	
}