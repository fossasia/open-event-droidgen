<?php

    if ( 0 < $_FILES['file']['error'] ) {
        echo 'Error: ' . $_FILES['file']['error'] . '<br>';
    }
    else {
        mkdir("/var/www/html/uploads/".$_POST['timestamp'], 0700);
        move_uploaded_file($_FILES['file']['tmp_name'], "/var/www/html/uploads/".$_POST['timestamp']."/json.zip");
    }
?>
