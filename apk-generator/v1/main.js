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
var $ = jQuery;
var data = "false";
var ajax = new XMLHttpRequest();
var timestamp = Number(new Date());
var form = document.querySelector("form");
var config = {
    apiKey: "API-KEY",
    authDomain: "app-id.firebaseapp.com",
    databaseURL: "https://app-id.firebaseio.com",
    storageBucket: "app-id.appspot.com",
};
firebase.initializeApp(config);
var file = document.getElementById('uploadZip');
file.onchange = function(e) {
    var ext = this.value.match(/\.([^\.]+)$/)[1];
    switch (ext) {
        case 'zip':
            ajax.abort();
            $('.progress').show();
            $('#upload-progress').show();
            data = "true";
            $('#submit').prop("disabled", true);;
            uploadFile();
            break;
        default:
            alert('Only zip files are allowed');
            this.value = '';
    }
};
var database = firebase.database();
form.addEventListener("submit", function(event) {
    event.preventDefault();
    $('.progress').css('display', 'block');
    $('#generator-progress').css('display', 'block')
    $('#upload-progress').css('display', 'block')
    var ary = $(form).serializeArray();
    var emailse = $("#Email").val();
    var obj = {};
    for (var a = 0; a < ary.length; a++) obj[ary[a].name] = ary[a].value;
    console.log("JSON", obj);
    if (obj.Api_Link != "") {
        data = "true"
    }

    if (obj.Email == "" || data == "false") {
        alert("It seems like you forgot to fill up your email or data source");
        setTimeout("location.reload(true);", 1);
    } else if (!checkemail(emailse)) {
        alert("Please enter a valid email address");
        setTimeout("location.reload(true);", 1);
    }
else {
    updatePercentUpload(20)
    var file_data = $('#uploadZip').prop('files')[0];
    var form_data = new FormData();
    form_data.append('file', file_data);
    firebase.database().ref('users/' + timestamp).set(obj);
    database.ref('users/' + timestamp).once('value').then(function(snapshot) {
        updatePercentUpload(10);
        console.log("Received value", snapshot.val());
        document.getElementById("status").innerHTML = "Building the app. This might take a while...";
        ajaxCall1();
        $('#submit').prop("disabled", true);

        function ajaxCall1() {
            updatePercentUpload(35);
            $.ajax({
                type: "POST",
                url: "/runPy.php",
                data: {
                    timestamp: timestamp
                },
                success: function(response) {
                    console.log("Success", response);
                    updatePercentUpload(100);
                    $('#download').prop("disabled", false);
                    $('#download').click(function() {
                        window.location = response;
                    });
                    document.getElementById("status").innerHTML = "App build completed!";
                }
            });
        }
    });
}
}); //after this
function updatePercentUpload(perc) {
    $('#upload-progress').css('width', perc + '%');
    $('#upload-progress').html(parseInt(perc) + '%');
}

function uploadFile() {
    var file_data = $('#uploadZip').prop('files')[0];
    var form_data = new FormData();
    form_data.append('file', file_data);
    form_data.append('timestamp', timestamp);
    ajax.upload.addEventListener("progress", progressHandler, false);
    ajax.addEventListener("load", completeHandler, false);
    ajax.open("POST", "upload.php");
    ajax.send(form_data);

}

function progressHandler(event) {
    var percent = (event.loaded / event.total) * 100;
    updatePercentUpload(Math.round(percent))
    document.getElementById("status").innerHTML = Math.round(percent) + "% uploaded... please wait";
}

function completeHandler(event) {
    document.getElementById("status").innerHTML = "Upload Complete, click on the button below to build your app";
    $('#submit').prop("disabled", false);;
}

function checkemail(emailse) {
    var re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    return re.test(emailse);
}