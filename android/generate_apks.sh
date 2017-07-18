#!/usr/bin/env bash
echo Build for FOSSASIA17 Started

./gradlew build
./gradlew assembleRelease

#Prevent further builds if a PR
if [ "$TRAVIS_PULL_REQUEST" != "false" ]; then
    echo "Just a PR. Skip apk upload."
    exit 0
fi

#Generate a folder to store apks of different sample
mkdir $HOME/daily/

#Backup config.json to config.txt
cp app/src/main/assets/config.json app/src/main/assets/config.txt

#Initial Build Using FOSSASIA17 Sample
cp -R app/build/outputs/apk/app-fdroid-debug.apk $HOME/daily/fossasia17-fdroid.apk

#BASE_URLS for Samples
GOOGLEIO17="\"Api_Link\":\"https://raw.githubusercontent.com/fossasia/open-event/master/sample/GoogleIO17\"}"
MozillaAllHands17="\"Api_Link\":\"https://raw.githubusercontent.com/fossasia/open-event/master/sample/MozillaAllHands17\"}"
FBF817="\"Api_Link\":\"https://raw.githubusercontent.com/mahikaw/open-event/fbf8/sample/F8\"}"

#ApkNames
GOOGLEIO17Apk=googleio17-fdroid.apk
MozillaAllHands17Apk=mozillaAllHands17-fdroid.apk
FBF817Apk=fbf817-fdroid.apk

COUNTER=0

declare -a arr=($GOOGLEIO17 $MozillaAllHands17 $FBF817)
declare -a apkname=($GOOGLEIO17Apk $MozillaAllHands17Apk $FBF817Apk)

while [ $COUNTER -le 2 ];
do
./configedit.sh "${arr[$COUNTER]}" $ANDROID > config.json

#Reconfigure BASE_URL
mv config.json app/src/main/assets/

#Remove all pre-exsiting APKs
rm app/build/outputs/apk/*

#Start Build
./gradlew build
./gradlew assembleRelease

#Move built apk to $HOME/daily/
cp -R app/build/outputs/apk/app-fdroid-debug.apk $HOME/daily/"${apkname[$COUNTER]}"

((COUNTER+=1))
done

#Cleanup
mv app/src/main/assets/config.txt app/src/main/assets/config.json
