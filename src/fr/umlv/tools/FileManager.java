package fr.umlv.tools;
import java.io.File;


public class FileManager {
	
	/**
	 * Return true if the name respect convention .md
	 * @param fileName The name f the file
	 * @return boolean
	 */
	public static boolean isMdFileName(String fileName) {
		int sizeFileName = fileName.length();
		return (!fileName.startsWith("."))
				&& fileName.substring(sizeFileName - 3).equals(".md");
	}

	/**
	 * Return if the file is a md file and nor a directory witch end with .md
	 * @param theFile The name of the file
	 * @return boolean
	 */
	public static boolean isMdFile(File theFile) {

		return theFile.isFile() && isMdFileName(theFile.getName());

	}
}
