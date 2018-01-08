package fr.umlv.tools;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashMap;
import java.util.Map;

public class WatchDir extends AbstractVerticle{
  private final WatchService watcher ;
     private final Map<WatchKey,Path> keys = new HashMap<WatchKey,Path>();
     
     //// The filename is the
        // context of the event.
     //docs.oracle.com/javase/tutorial/essential/io/notification.html
     @SuppressWarnings("unchecked")
     private static <T> WatchEvent<T> cast(WatchEvent<?> event) {
         return (WatchEvent<T>)event;
     }
  
     /**
      * Register the given directory with the WatchService
      * @param dir The path to directory to be listen
      * @throws IOException If dir doesn't exist
      */
     private void register(Path dir) throws IOException {
         WatchKey key = dir.register(this.watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
         this.keys.put(key, dir);
     }
     

     
     /**
      * Creates a WatchService and registers the given directory
      * @param dir The path to directory to be listen
      * @return The watchdir
      * @throws IOException If dir doesn't exist
      */
     public static WatchDir createWatchDir( Path dir ) throws IOException{
      
      WatchService watcher = FileSystems.getDefault().newWatchService();
      WatchDir watchDir = new WatchDir(watcher);
      
      watchDir.register(dir);
      return watchDir;
     }
     
     private WatchDir( WatchService watcher )  {
   this.watcher = watcher;
      
     }
     
  //   We can factorise more this methode beacause the call in the loop are necessary
  // to validate the sepecific test of wachService Problem
  //( the sequest
     private void loopCheck() {
         while(true) {
  
             // wait for key to be signalled
             WatchKey key;

             try {
                 key = this.watcher.take();
             } catch (InterruptedException x) {
                 return; // ask to steeve or other if we have to do that or an other thing here !!!!!!!!!!!!!!!!!!!!!
             }
  
             Path dir = this.keys.get(key);
             if (dir == null) {
                 System.err.println("WatchKey not recognized!!");
                 continue;
             }
             
             for (WatchEvent<?> event: key.pollEvents()) {
                 WatchEvent.Kind<?> kind = event.kind();
                 
                 // TBD - provide example of how OVERFLOW event is handled
                 if (kind == OVERFLOW) {
                     continue;
                 }
                 
  
                 // Context for directory entry event is the file name of entry
                 WatchEvent<Path> ev = cast(event);
                 Path name = ev.context();
                 Path child = dir.resolve(name);

        

                 // if kind ise entry_delete, taht mean the file doesn't existe now so no check about that
                 if( (kind != ENTRY_DELETE) && ! FileManager.isMdFile(child.toFile()) ){// DUPLICATION AVEC L'AUTRE 
        
                  continue ;
                 }
    
                 if(kind == ENTRY_MODIFY){
                  int count = 0;

                  if(event.count() == 2)
                   count = 2;

                  if(event.count() == 1)
                   count++;

                  if(count == 2){
                   //your operations here
                   count = 0;
               
                   sendMessage(event,child);
                         continue;
                  }
                 }
        
                 sendMessage(event,child);
                
               
                
  
             }
  
             // reset key and remove from set if directory no longer accessible
             boolean valid = key.reset();
             if (!valid) {
                 this.keys.remove(key);
  
                 // all directories are inaccessible
                 if (this.keys.isEmpty()) {
                     break;
                 }
             }
         }
     }

 
 private  void sendMessage(WatchEvent<?> event, Path child) {
  Path  fileNamePath = child.getFileName() ;
  if( fileNamePath == null ) {
   throw new IllegalStateException();
  }
  JsonObject message = new JsonObject();
  message.put("type", event.kind().name());
  message.put("name",fileNamePath.toString());
  this.vertx.eventBus().send("server-update",message.toString());
 }




 /**
  * (non-Javadoc)
  * 
  * @see io.vertx.core.AbstractVerticle#start() Start a verticle that check
  * for update in the folder and send the type of modification and the name
  * of the file in format json with the eventbus
  */
 @Override
 public void start() {
  new Thread(this::loopCheck).start();

 }
}