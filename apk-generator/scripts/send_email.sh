#!/bin/bash
echo "Preparing to send the email"
EMAIL=`cat email.txt`
mutt -s "Test mail" $EMAIL -a /root/dev@fossasia.org/open-event-android/android/app/build/output/apk/app-debug.apk < /root/mail_message.txt
echo "Mail sent"

