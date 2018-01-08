$(document).ready(function() {
  

    var element = $("#listExercise");
     
     $.get('/exercises/',function(list) {
      console.log(list);
     element.append(list);
      });

console.log(document.location.href);
var eb = new vertx.EventBus(document.location.href+'eventbus');


 eb.onopen = function() {

 

     eb.registerHandler('server-client', function(message) {
      console.log('received a message: ' + JSON.stringify(message));
      var jsonData = JSON.parse(message);
      console.log(jsonData.type);
      console.log(jsonData.name);
      if( (jsonData.type == "ENTRY_CREATE") || (jsonData.type == "ENTRY_DELETE" ) )
       $.get('/exercises/',function(list) {
        console.log(list);
       element.empty();
       element.append(list);
        });
    });
    
 };
     console.log("connection is open !");

 

//  eb.send('client-server', {type: 'tim', name: "truc"});
     
     
   });
