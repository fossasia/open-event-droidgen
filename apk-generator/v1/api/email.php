<?php
// If you are using Composer
namespace SendGrid;
require 'vendor/autoload.php';
$uid = $argv[1];
$receiver = $argv[2];
$str = file_get_contents('/var/www/config.json');
$jsonData = json_decode($str,true);
$from = new Email(null, $jsonData['sender']);
$subject = "Hello, your app is ready!";
$to = new Email(null,$argv[2]);
$content = new Content("text/plain",$jsonData['body'].$uid);
$mail = new Mail($from, $subject, $to, $content);
$path = "/var/www/html/release/".$uid."/releaseapk.apk";
$type = pathinfo($path, PATHINFO_EXTENSION);
$data = file_get_contents($path);
$attachment = new Attachment();
$attachment->setContent(base64_encode($data));
$attachment->setType($type);
$attachment->setFilename("release.apk");
$attachment->setDisposition("attachment");
$attachment->setContentId("release");
$mail->addAttachment($attachment);
$apiKey = $jsonData['api'];
$sg = new \SendGrid($apiKey);
$response = $sg->client->mail()->send()->post($mail);
echo $response->statusCode();
echo $response->headers();
echo $response->body();
