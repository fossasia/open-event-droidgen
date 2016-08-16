<?php
if(isset($_POST['timestamp']))
{
    $uid = escapeshellcmd($_POST['timestamp']);
    exec("sudo python /var/www/appgenserver.py $uid");
    echo "http://ENTER.IP.ADDRESS.OF.SERVER.HERE/release/".$uid."/releaseapk.apk";
}
?>
