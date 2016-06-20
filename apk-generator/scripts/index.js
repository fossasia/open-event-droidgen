<script src="https://www.gstatic.com/firebasejs/live/3.0/firebase.js"></script>
<script src="https://code.jquery.com/jquery-1.10.2.js"></script>
<script src="https://code.jquery.com/ui/1.11.2/jquery-ui.js"></script>
<script>
    var $ = jQuery;
    var timestamp = Number(new Date());
    var form = document.querySelector("form");
    var config = {
      apiKey: "AIzaSyDTo4TPzUkvYYN5dwvNnI4jVmB_eTc0Lpo",
      authDomain: "app-generator.firebaseapp.com",
      databaseURL: "https://app-generator.firebaseio.com",
      storageBucket: "app-generator.appspot.com",
    };
    firebase.initializeApp(config);
    // var userId = 1;
    var database = firebase.database();
    form.addEventListener("submit", function(event) {
      event.preventDefault();
      var ary = $(form).serializeArray();
      var obj = {};
      for (var a = 0; a < ary.length; a++) obj[ary[a].name] = ary[a].value;
      console.log("JSON",obj);
      firebase.database().ref('users/' + timestamp).set(obj);
      database.ref('users/' + timestamp).once('value').then(function(snapshot) {
      console.log("Received value",snapshot.val());
      $.ajax({
        type: "POST",
        url: "/test.php",
        //url: "/root/Final/appgenserver.py > /root/1.out",
        data: { timestamp : timestamp },
        success: function(response){
          console.log("Success",response);
          alert(response);
        }
      });
      });
      });
</script>


