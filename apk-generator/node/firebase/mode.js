const Kefir=require('kefir').Kefir;
const firebase=require('firebase')
  var config = {
    databaseURL: "https://fossasia-a729e.firebaseio.com"
  };
const database=firebase.database()
  firebase.initializeApp(config);
  var userData=function(timestamp) {
  	return Kefir.stream(function(emitter) {
            database.ref('users/'+timestamp).on('value',function(snapshot) {
            	emitter.emit(snapshot.val());
            })
  	})
  };

  exports.userData=userData