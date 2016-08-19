<?php

$uid = escapeshellcmd($_POST['timestamp']);

mkdir("/var/www/html/uploads/".$uid, 0700);

rename("/var/www/html/uploads/upload.zip", "/var/www/html/uploads/".$uid."/json.zip");

?>
