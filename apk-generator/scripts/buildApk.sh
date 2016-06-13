#!/bin/bash

#Change to new folder
cd $1

pwd
cd ./open-event-android/android
echo "Start building apk"
#chmod -R 777 $1/open-event-android/android/.gradle/2.11/ 
export ANDROID_HOME=/var/www/android-sdk-linux/
./gradlew assembleDebug --stacktrace
echo "done"


