var $ = jQuery;
var timestamp = Number(new Date());
var form = document.querySelector("form");
var firebase = require("firebase");
var config = {
    apiKey: "AIzaSyC_h1ca4ntwFr3j1Y56OwvTEj1geq3I-qQ",
    authDomain: "fossasia-a729e.firebaseapp.com",
    databaseURL: "https://fossasia-a729e.firebaseio.com",
    storageBucket: "fossasia-a729e.appspot.com"
};

$("input:radio[name=\"datasource\"]").change(
    function () {
        if ($(this).is(":checked")) {

            if ($(this).val() === "mockjson") {
                $("#jsonupload-input").hide(100);
                $("#eventapi-input").hide(100);
            }

            if ($(this).val() === "jsonupload") {
                $("#jsonupload-input").show(100);
                $("#eventapi-input").hide(100);
            }

            if ($(this).val() === "eventapi") {
                $("#eventapi-input").show(100);
                $("#jsonupload-input").hide(100);
            }
        }
    });

firebase.initializeApp(config);
var database = firebase.database();
database.ref("users/1471606817051").on("value", function (snap) {
    console.log("enna");
    console.log(snap.val());
})
form.addEventListener("submit", function (event) {
    event.preventDefault();
    $(".progress").css("display", "block");
    $("#generator-progress").css("display", "block");
    var ary = $(form).serializeArray();
    var obj = {};
    for (var a = 0; a < ary.length; a++) obj[ary[a].name] = ary[a].value;
    console.log("JSON", obj);
    if (obj.Email === "" || obj.App_Name === "") {
        alert("It seems like you forgot to fill up your email address or app's name");
        setTimeout("location.reload(true);", 1);
    } else {
        alert("Please wait while we generate the app, meanwhile you can stick around to directly download it.The app will also be emailed to you.");
        var fileData = $("#uploadZip").prop("files")[0];
        var formData = new FormData();
        formData.append("file", fileData);
        firebase.database().ref("users/" + timestamp).set(obj);
        database.ref("users/" + timestamp).once("value").then(function (snapshot) {

            console.log("Received value", snapshot.val());
            var fileData = $("#uploadZip").prop("files")[0];

            function updatePercent(perc) {
                $("#generator-progress").animate({
                    "width": perc + "%"
                }, function () {
                    $("#generator-progress-val").html(perc + "%");
                });
            }

            function ajaxCall2() {
                updatePercent(50);
                $.ajax({
                    type: "POST",
                    url: "/build",
                    data: {
                        timestamp
                    },
                    success(response) {
                        console.log("Success", response);
                        updatePercent(100);
                        window.location = response;
                    }
                });
            }

            function ajaxCall1(uniqueName) {
                console.log(uniqueName);
                $.ajax({
                    type: "POST",
                    url: "/uploadhelper",
                    data: {
                        timestamp,
                        filename: uniqueName
                    },
                    success(response) {
                        updatePercent(20);
                        ajaxCall2();
                    }
                });
            }

            $.ajax({

                url: "/upload", // point to server-side PHP script
                cache: false,
                contentType: false,
                processData: false,
                data: formData,
                type: "POST",
                success: function (php_script_response) {
                    ajaxCall1(php_script_response);
                    updatePercent(10);
                }
            });
        });
    }
});