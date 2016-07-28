$(document).ready(function() {

  let $email =$("#email");
  let $name  =$("#name"); 
  let $url  =$("#url");
  let $theme =$("#theme");

  $("#btn").click(function() { 

  var form = document.querySelector("form");
  var fd = new FormData(form);

  var files ={
    "sessionfile":  $( "#sessionfile" )[0].files[0],
    "speakerfile":$( "#speakerfile" )[0].files[0],
    "trackfile":$( "#trackfile" )[0].files[0] ,
    "locationfile":$( "#locationfile" )[0].files[0]
  }
  
  for (var key in files) {
    //console.log(key, files[key]);
    fd.append(key, files[key]);
  }

  var obj={
    name:$name.val(),
    email:$email.val(),
    url :$url.val(),
    theme:$theme.val(),
    files:fd
    
  };

  $.ajax({
    type: "POST",
    url:  " ",
    data: obj,
    processData: false,
    contentType: false,
    
    success :function(data){
      console.log(obj);
    }
       
    });

  });
});




  
