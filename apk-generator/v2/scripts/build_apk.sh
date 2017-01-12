#!/bin/bash
./gradlew assembleRelease --info
jarsigner -keystore ${KEYSTORE_PATH} -storepass ${KEYSTORE_PASSWORD} app/build/outputs/apk/app-googleplay-release-unsigned.apk ${KEY_ALIAS}
${1}/zipalign -v 4 app/build/outputs/apk/app-googleplay-release-unsigned.apk release.apk
echo "done"