#!/usr/bin/env bash

if [ "$TRAVIS_PULL_REQUEST" != "false" -o "$TRAVIS_REPO_SLUG" != "fossasia/open-event-android" ]; then
    echo "Just a PR. Skip apk upload."
    exit 0
fi

mkdir $HOME/daily/
cp -R /home/travis/build/fossasia/open-event-android/android/app/build/outputs/apk/app-fdroid-debug.apk $HOME/daily/
# go to home and setup git
cd $HOME
git config --global user.email "noreply@travis.com"
git config --global user.name "Travis-CI"
  
git clone --quiet --branch=apk https://the-dagger:$GITHUB_API_KEY@github.com/fossasia/open-event-android  apk > /dev/null
cd apk
cp -Rf $HOME/daily/*  ./
mv app-fdroid-debug.apk sample-apk-${TRAVIS_BRANCH}.apk
echo $TRAVIS_COMMIT > meta/deployment/commit_hash
echo $TRAVIS_BRANCH > meta/deployment/branch

# Create a new branch that will contains only latest apk
git checkout --orphan latest-apk-only

# Add generated APKs.
git add -f .
git commit -m "Update Sample Apk generated from $TRAVIS_BRANCH branch."

# Delete current apk branch
git branch -D apk
# Rename current branch to apk
git branch -m apk

# Force push to origin since histories are unrelated
git push origin apk --force > /dev/null
