#!/bin/bash
echo "Preparing to send the email"
MPATH=$1
EMAIL=$2
mutt -s "Test mail" $EMAIL -a $MPATH/open-event-android/android/app/build/outputs/apk/app-googleplay-debug.apk < /var/www/mail_message.txt
echo "mail sent"


