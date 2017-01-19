#!/bin/bash
# Install Android SDK
cd /opt
wget --output-document=android-sdk.zip https://dl.google.com/android/repository/tools_r25.2.3-linux.zip
unzip android-sdk.zip -d /opt/android-sdk-linux
rm -f android-sdk.zip
mkdir /opt/android-sdk-linux/platform-tools
chown -R root.root android-sdk-linux
/opt/tools/android-accept-licenses.sh "android-sdk-linux/tools/android update sdk --all --no-ui --filter platform-tools,tools"
/opt/tools/android-accept-licenses.sh "android-sdk-linux/tools/android update sdk --all --no-ui --filter platform-tools,tools,build-tools-24.0.2,android-24,addon-google_apis-google-24,extra-android-support,extra-android-m2repository,extra-google-m2repository,extra-google-google_play_services"