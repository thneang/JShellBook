 
 $(document).ready(function() {


			function putImputToEvaluateCode(theStart,theEnd){
		
			for (var j = theStart; j < theEnd ; j++) {
				console.log('myForm'+j);

				const t = j;
			
				document.getElementById('myForm'+t).addEventListener('submit', function(e) {
			
					var $form = $( this )
					var term =$form.find( "textarea[name='bidule']" ).val();
					console.log("term : %s",term);
					$.ajax({ url : "/api/reponse", 
						type: "POST", 
						crossDomain: true,
						data : JSON.stringify({bidule: term}), 
						dataType : 'json', 
						success:function(data) {
							console.log("items : %s",data.bidule);
							console.log( '"#myAnswerEvaluated'+t+'"' ); 
							
							$( '#myAnswerEvaluated'+t ).empty().append(data.bidule);
						}, 
						error: function( error ) { 
							alert( "Error"+error );
						} 
					});
					e.preventDefault();

				}, true);


			} 
			}






    var element = $("#listExercise");
    var url = window.location.search;
  	var exerciseName = url.substring(url.lastIndexOf("=")+1);
	

		var thelength = 0 ;
		var exerciceRequest = '/exercise/'+exerciseName;
		
		/*
$(window).on('mouseover', (function () {
	alert("coucou");
    window.onbeforeunload = null;
}));
 */
 
 // --------------
 var str = document.location.href ;
 var prefixeUrl = str.substring(0,str.indexOf('exer'));

var eb = new vertx.EventBus(prefixeUrl+'eventbus');


 eb.onopen = function() {

 

     eb.registerHandler('server-client', function(message) {
		 	console.log("info recu pour changement 1");
      console.log('received a message: ' + JSON.stringify(message));
      var jsonDataNew = JSON.parse(message);
      console.log(jsonDataNew.type);
      console.log(jsonDataNew.name);
      
      // if is not the same name , we don't care
       if( ( jsonDataNew.name !==  exerciseName ) || ( jsonDataNew.type == 'ENTRY_CREATE' ) ) return ;
       // we don"'t check if is it a created exercices because is impossible that a browser on an exercice not yet added.
       
       
       
       
       console.log(jsonDataNew.type);
       if( jsonDataNew.type === 'ENTRY_DELETE') window.close();
       
       // ENTRY_MODIFY

		// modif introducing 
		$("#introducing").empty().append(jsonDataNew.newContain.introducing);
 
       	var a =  jsonDataNew.newContain.questions.length  ;
       	var b = $("li").length ;
		var minSize =( a > b ? b : a );
		console.log("info recu pour changement 2");
		// update question which are already on the page.
		for( var i = 0 ; i < minSize  ; i++ ){
			console.log($("li")[i]);
			$("#myLi"+i).empty().append(jsonDataNew.newContain.questions[i].question);
		}
       
       	var deb = i ;
	
		//if missize is the size of the new list question, that mean questions have been removed, so that we have to delete the N last question 
		// with N =  $("li").length  - jsonDataNew.newContain.questions.length, and we also have to remove the corresponding imput and listening.


		// add the new questions
		// that will stop here is ther is less question in the nous version than the old version
		for (var j = deb; j < jsonDataNew.newContain.questions.length ; j++) {
			element.append('<li id="myLi'+j+'">'+jsonDataNew.newContain.questions[j].question +'</li>' );
			element.append('<form id="myForm'+j+'"><br /> <input type="submit" value="Submit !" /> <br /><textarea class=answer rows=20 cols=50 name="bidule" value="Entrez un texte">Your answser !</textarea><p id ="myAnswerEvaluated'+j+'"></p></form>');
			

		}
		
       // on lance l'ecoute sur les nouvelle input
       putImputToEvaluateCode(deb,jsonDataNew.newContain.questions.length  );
       
       	var oldSize = $("li").length;
		for( var i = jsonDataNew.newContain.questions.length  ; i < oldSize  ; i++ ){
			// remove questions
			$("#myLi"+i).remove();
			
				// remove listening , it's necesseray ??
			/* ????????? */
		//	$(document).getElementById("myForm"+i).removeEventListener(); RISQUE DE PB AU NIVEAU DU LISTENER
			
			// remove imput
			$("#myForm"+i).remove();
		
		}
       
       
      /*
      if( (jsonData.type == "ENTRY_CREATE") || (jsonData.type == "ENTRY_DELETE" ) )
       $.get('/exercises/',function(list) {
        console.log(list);
       element.empty();
       element.append(list);
        });*/
    });
    
 };
 // --------------
 
 
 
 
 
	
window.onbeforeunload = function(){
		var closeExerciceRequest = '/closeexercise/'+exerciseName;
		$.get(closeExerciceRequest,function(list) {
		});

  return null;
};
	/*
	window.onunload = function() {
    alert('Bye.');
}	
	
	$(window).unload(function(){
  alert('Bye.');
});	
		*/
		
		
		
		
		$.get(exerciceRequest,function(list) {




			console.log(list);


			var jsonData = JSON.parse(list);
			thelength =  jsonData.questions.length;
	
			element.append('<h1 id="introducing">'+jsonData.introducing+'</h1>');
			for (var i = 0; i < jsonData.questions.length ; i++) {
				var counter = jsonData.questions[i];

				element.append('<li id="myLi'+i+'">'+counter.question +'</li>' );
				element.append('<form id="myForm'+i+'"><br /><input type="submit" value="Submit !" /><br /> <textarea class=answer rows=20 cols=50 name="bidule" value="Entrez un texte">Your answser !</textarea><p id ="myAnswerEvaluated'+i+'"></p> </form>');

			}

			
			putImputToEvaluateCode(0,jsonData.questions.length );
			
			



		});
     



   });
   
   
   
   
   
   




