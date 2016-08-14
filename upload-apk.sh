#!/usr/bin/env bash

mkdir $HOME/daily/
cp -R /home/travis/build/fossasia/open-event-android/android/app/build/outputs/apk/app-fdroid-debug.apk $HOME/daily/
# go to home and setup git
cd $HOME
  git config --global user.email "harshithdwivedi@gmail.com"
  git config --global user.name "the-dagger"
  
git clone --quiet --branch=development https://the-dagger:$GITHUB_API_KEY@github.com/fossasia/open-event-android  development > /dev/null
cd development
cp -Rf $HOME/daily/*  sample-apk/
git add -f .
  # git remote rm origin
  # git remote add origin https://the-dagger:$GITHUB_API_KEY@github.com/the-dagger/open-event-android
  git add -f .
  git commit -m "Update Sample Apk [skip ci]"
  git push origin development > /dev/null
