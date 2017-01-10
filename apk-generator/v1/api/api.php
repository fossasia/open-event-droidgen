<?php



function sendResponse($data) {

    header('Content-type: application/json');

    echo json_encode($data);

    die();

}



if ($_SERVER['REQUEST_METHOD'] != 'POST') {

        sendResponse([

        "status"=>"error",

        "code"=>405,

        "message"=>"Method Not Allowed",

    ]);

}



$body = json_decode(file_get_contents('php://input'), true);



if (!array_key_exists('email', $body) || !array_key_exists('app_name', $body) || !array_key_exists('endpoint', $body)) {

    sendResponse([

        "status"=>"error",

        "code"=>422,

        "message"=>"Unprocessable entity",

    ]);

}



$uid = mt_rand(1000,9999).time();

$email = escapeshellcmd($body['email']);

$appName = escapeshellcmd($body["app_name"]);

$endpoint = escapeshellcmd($body["endpoint"]);

$appName = preg_replace('/\s+/', '_', $appName);

exec("sudo python /var/www/html/api/appgenserver.py $email $appName $endpoint $uid");