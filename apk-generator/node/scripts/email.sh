#!/bin/bash
echo "Preparing to send the email"
MPATH=$1
EMAIL=$2
echo $3 "http://192.241.232.231/release/"$5 | mutt -s "Your App is Ready" $EMAIL -a $MPATH/open-event-android/android/releaseapk.apk
echo "mail sent"

