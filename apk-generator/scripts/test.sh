#!/bin/bash

#Change to new folder
cd $1

echo "Start cloning"
git clone git@github.com:fossasia/open-event-android.git
pwd
cd ./open-event-android/android
echo "Start building apk"
./gradlew assembleDebug
echo "done"
