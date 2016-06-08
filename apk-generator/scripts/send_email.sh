#!/bin/bash
echo "Preparing to send the email"
EMAIL=$1
mutt -s "Test mail" $EMAIL -a /root/$EMAIL/open-event-android/android/app/build/output/apk/app-debug.apk < /root/mail_message.txt
echo "Mail sent"
