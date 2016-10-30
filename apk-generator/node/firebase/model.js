const Kefir=require('kefir').Kefir;
const firebase=require('firebase')
var config = {
  apiKey: "AIzaSyC_h1ca4ntwFr3j1Y56OwvTEj1geq3I-qQ",
  authDomain: "fossasia-a729e.firebaseapp.com",
  databaseURL: "https://fossasia-a729e.firebaseio.com",
  storageBucket: "fossasia-a729e.appspot.com",
};
firebase.initializeApp(config);
const database=firebase.database()

  var userData=function(timestamp) {
  	return Kefir.stream(function(emitter) {
            database.ref('users/'+timestamp).on('value',function(snapshot) {
            	emitter.emit(snapshot.val());
            })
  	})
  };

  exports.userData=userData
