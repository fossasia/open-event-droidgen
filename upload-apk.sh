#!/usr/bin/env bash

mkdir $HOME/daily/
cp -R /home/travis/build/fossasia/open-event-android/android/app/build/outputs/apk/app-fdroid-debug.apk $HOME/daily/
# go to home and setup git
cd $HOME
  git config --global user.email "noreply@travis.com"
  git config --global user.name "Travis-CI"
  
git clone --quiet --branch=apk https://the-dagger:$GITHUB_API_KEY@github.com/fossasia/open-event-android  apk > /dev/null
cd apk
cp -Rf $HOME/daily/*  ./
git add -f .
# git remote rm origin
# git remote add origin https://the-dagger:$GITHUB_API_KEY@github.com/the-dagger/open-event-android
git add -f .
git commit -m "Update Sample Apk"
git push origin apk > /dev/null
