"use strict";

$('input:radio[name="datasource"]').change(
  function() {
    if ($(this).is(':checked')) {

      if ($(this).val() === 'mockjson') {
        $('#jsonupload-input').hide(100);
        $('#eventapi-input').hide(100);
      }

      if ($(this).val() === 'jsonupload') {
        $('#jsonupload-input').show(100);
        $('#eventapi-input').hide(100);
      }

      if ($(this).val() === 'eventapi') {
        $('#eventapi-input').show(100);
        $('#jsonupload-input').hide(100);
      }
    }
  });
$('#btnGenerate').click(function () {
  var formData = getData();
});

function displayButtons (appPath) {
  var btnDownload = $('#btnDownload');
  var btnLive = $('#btnLive');
  btnDownload.css('display', 'block');
  btnLive.css('display', 'block');

  btnLive.click(function () {
    window.location.href = '/live/preview/' + appPath
  });

  btnDownload.click(function () {
    window.location.href = '/download/' + appPath
  })
}

function updateStatus (statusMsg) {
  $('#status').animate({'opacity': 0}, 500, function () {
    $(this).text(statusMsg);
  }).animate({'opacity': 1}, 500);
}

function getData () {
  var data = {};
  var formData = $('#form').serializeArray();
  formData.forEach( function(field) {
    if (field.name == 'name') {data.name = field.value }
    if (field.name == 'email') {data.email = field.value }
    if (field.name == 'theme') {data.theme = field.value }
    if (field.name == 'datasource') {data.datasource = field.value }
    if (field.name == 'apiendpoint') {data.apiendpoint = field.value }
    if (field.name == 'assetmode') {data.assetmode = field.value }});
  try {
    data.singlefileUpload = $('#singlefileUpload')[0].files[0];
    data.zipLength = $('#singlefileUpload')[0].files[0].size;
  } catch (err) {
    data.singlefileUpload = "";
    data.zipLength = 0;
  }


  // var $ = jQuery;
  var timestamp = Number(new Date());
  // var form = document.querySelector("form");
  var config = {
    apiKey: "AIzaSyDTo4TPzUkvYYN5dwvNnI4jVmB_eTc0Lpo",
    authDomain: "app-generator.firebaseapp.com",
    databaseURL: "https://app-generator.firebaseio.com",
    storageBucket: "app-generator.appspot.com",
  };
  // firebase.initializeApp(config);
  var userId = 1;
  var database = firebase.database();
  //     form.addEventListener("btnGenerate", function(event) {
  console.log("HERE")
  //       event.preventDefault();
  //       var ary = $(form).serializeArray();
  //       var obj = {};
        // for (var a = 0; a < ary.length; a++) obj[ary[a].name] = ary[a].value;
        //   console.log("JSON",obj);
        // var file_data = $('#uploadZip').prop('files')[0];
        // var storageRef = firebase.storage().ref(timestamp.toString());       
        // storageRef.put(file_data);
        // var form_data = new FormData();                  
        // form_data.append('file', file_data);                           
        // firebase.database().ref('users/' + timestamp).set(obj);
        // database.ref('users/' + timestamp).once('value').then(function(snapshot) {
        //   console.log("Received value",snapshot.val());
        //   alert("Please wait while we generate the app, meanwhile you can stick around to directly download it.The app will also be emailed to you."); 
  //         $.ajax({

  //                 url: '/upload.php', // point to server-side PHP script
  //                 cache: false,
  //                 contentType: false,
  //                 processData: false,
  //                 data: form_data,                         
  //                 type: 'post',
  //                 success: function(php_script_response){
  //                   ajaxCall1();
  //                 }
  //               });
  //         function ajaxCall1() {
  //           $.ajax({
  //             type: "POST",
  //             url: "/uploadHelper.php",
  //             data: { timestamp : timestamp },
  //             success: function(response){
  //               ajaxCall2();
  //             }
  //           });
  //         }
  //         function ajaxCall2(){      
  //           $.ajax({
  //             type: "POST",
  //             url: "/runPy.php",
  //             data: { timestamp : timestamp },
  //             success: function(response){
  //               console.log("Success",response);
  //               window.location = response;
  //             }
  //           });
  //         }
  //       });
  //     });

  console.log(data);

  return data;
}

