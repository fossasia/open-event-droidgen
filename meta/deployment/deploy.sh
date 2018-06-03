#!/usr/bin/env bash

export DEPLOY_BRANCH=${DEPLOY_BRANCH:-master}

if [ "$TRAVIS_PULL_REQUEST" != "false" -o "$TRAVIS_REPO_SLUG" != "fossasia/open-event-android" -o  "$BRANCH" != "$DEPLOY_BRANCH" ]; then
    echo "Skip production deployment for a very good reason."
    exit 0
fi

git clone https://github.com/fossasia/open-event-android.git && cd open-event-android
git checkout $DEPLOY_BRANCH
bash kubernetes/travis/deploy.sh