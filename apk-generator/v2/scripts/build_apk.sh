#!/bin/bash
./gradlew assembleRelease --info
echo "signing"
jarsigner -keystore ${KEYSTORE_PATH} -storepass ${KEYSTORE_PASSWORD} app/build/outputs/apk/app-googleplay-release-unsigned.apk ${KEY_ALIAS}
echo "zipaligning"
${1}/zipalign -v 4 app/build/outputs/apk/app-googleplay-release-unsigned.apk release.apk
echo "done"