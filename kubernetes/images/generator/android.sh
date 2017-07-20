#!/bin/bash
# Install Android SDK
wget --output-document=/opt/android-sdk.zip https://dl.google.com/android/repository/tools_r25.2.3-linux.zip
unzip /opt/android-sdk.zip -d /opt/android-sdk-linux
rm -f /opt/android-sdk.zip
mkdir /opt/android-sdk-linux/platform-tools
chown -R $(whoami):$(whoami) /opt/android-sdk-linux
/opt/tools/android-accept-licenses.sh "/opt/android-sdk-linux/tools/android update sdk --all --no-ui --filter platform-tools,tools"
/opt/tools/android-accept-licenses.sh "/opt/android-sdk-linux/tools/android update sdk --all --no-ui --filter platform-tools,tools,build-tools-25.0.2,android-25,addon-google_apis-google-25,extra-android-support,extra-android-m2repository,extra-google-m2repository,extra-google-google_play_services"