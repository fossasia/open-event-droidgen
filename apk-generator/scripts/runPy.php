<?php
echo "Please wait while the app is being generated, it can take around 10 minutes for the build to finish.The completed app will be mailed to you ";
if(isset($_POST['timestamp']))
{
    $uid = $_POST['timestamp'];
    exec("sudo python /var/www/appgenserver.py $uid");
}
?>
