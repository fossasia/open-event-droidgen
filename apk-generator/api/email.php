<?php
// If you are using Composer
namespace SendGrid;
require 'vendor/autoload.php';

$uid = $argv[1];
$receiver = $argv[2];
$str = file_get_contents('/var/www/config.json');
$jsonData = json_decode($str,true);
echo $jsonData['api'];
$from = new Email(null, $jsonData['sender']);
$subject = "Hello World from the SendGrid PHP Library!";
$to = new Email(null,$argv[2]);
$content = new Content("text/plain",$jsonData['body'].$uid);
$mail = new Mail($from, $subject, $to, $content);
$attachment = new Attachment();
    $attachment->setContent(base64_encode("/var/www/html/release".$uid."/releaseapk.apk"));
    $attachment->setFilename("balance_001.apk");
    $attachment->setDisposition("attachment");
    $attachment->setType("application/vnd.android.package-archive");
    $attachment->setContentId("app");
    $mail->addAttachment($attachment);
$apiKey = $jsonData['api'];

$sg = new \SendGrid($apiKey);

$response = $sg->client->mail()->send()->post($mail);
echo $response->statusCode();
echo $response->headers();
echo $response->body();
