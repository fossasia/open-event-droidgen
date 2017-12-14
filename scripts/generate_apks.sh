#!/usr/bin/bash

echo "Build for FOSSASIA17 Started"

gradle=./gradlew

$gradle build

if [ $? != 0 ]; then
    echo "Gradle build failed"
    exit 1
fi

#Prevent further builds if a PR
if [ "$TRAVIS_PULL_REQUEST" != "false" ]; then
    echo "Just a PR. Skip apk upload."
    cd ..
    echo "Returned to root directory"
    exit 0
fi

#Generate a folder to store apks of different sample
mkdir -p $HOME/apps

export srcdir=./app
export builddir=$srcdir/build/outputs/apk/fdroid/debug
export apkdir=$HOME/apps

#Backup config.json to config.txt
cp $srcdir/src/main/assets/config.json $srcdir/src/main/assets/config.txt

#Initial Build Using FOSSASIA17 Sample
cp -R $builddir/app-fdroid-debug.apk $apkdir/fossasia17-fdroid.apk

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
    echo "Generating ${apkname[$COUNTER]}"
    bash ../scripts/configedit.sh "${arr[$COUNTER]}" $ANDROID > config.json

    #Reconfigure BASE_URL
    mv config.json $srcdir/src/main/assets/

    #Remove all pre-exsiting APKs
    rm -R $builddir/*

    #Start Build
    $gradle assembleDebug

    #Move built apk to $HOME/daily/
    cp -R $builddir/app-fdroid-debug.apk $apkdir/"${apkname[$COUNTER]}"

    ((COUNTER+=1))
done

echo "Completed Generating Files..."

#Cleanup
mv $srcdir/src/main/assets/config.txt $srcdir/src/main/assets/config.json
echo "Restored original JSON"

cd ..
echo "Returned to root directory"
