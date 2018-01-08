package fr.umlv.main ;

import fr.umlv.server.Server;
import fr.umlv.tools.WatchDir;
import io.vertx.core.Vertx;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class Main {// HAVE TO BE CHECKED
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Vertx vertx = Vertx.vertx();
		String pathLocation = "./";

		vertx.deployVerticle(Server.factoryServeur(new File(pathLocation)));

		try{
			
			vertx.deployVerticle( WatchDir.createWatchDir(Paths.get(pathLocation)));
		}catch(IOException e){
			throw new IllegalArgumentException("no such file "+pathLocation);
		}
	}	
}