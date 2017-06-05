#!/usr/bin/env bash

if [ "$TRAVIS_PULL_REQUEST" != "false" -o "$TRAVIS_REPO_SLUG" != "fossasia/open-event-android" ]; then
    echo "Just a PR. Skip google cloud deployment."
    exit 0
fi

export REPOSITORY="https://github.com/${TRAVIS_REPO_SLUG}.git"

sudo rm -f /usr/bin/git-credential-gcloud.sh
sudo rm -f /usr/bin/bq
sudo rm -f /usr/bin/gsutil
sudo rm -f /usr/bin/gcloud

curl https://sdk.cloud.google.com | bash;
source ~/.bashrc
gcloud components install kubectl

gcloud config set compute/zone us-west1-a
# Decrypt the credentials we added to the repo using the key we added with the Travis command line tool
openssl aes-256-cbc -K $encrypted_089594c81b43_key -iv $encrypted_089594c81b43_iv -in ./kubernetes/travis/eventyay-b6f445785c27.json.enc -out eventyay-b6f445785c27.json -d
mkdir -p lib
gcloud auth activate-service-account --key-file eventyay-b6f445785c27.json
export GOOGLE_APPLICATION_CREDENTIALS=$(pwd)/eventyay-b6f445785c27.json
gcloud config set project eventyay
gcloud container clusters get-credentials eventyay-cluster
cd kubernetes/images/generator
docker build --build-arg COMMIT_HASH=$COMMIT_HASH --build-arg BRANCH=$BRANCH --build-arg REPOSITORY=$REPOSITORY --no-cache -t eventyay/android-generator:$COMMIT_HASH .
docker login -u="$DOCKER_USERNAME" -p="$DOCKER_PASSWORD"
docker tag eventyay/android-generator:$COMMIT_HASH eventyay/android-generator:latest
docker push eventyay/android-generator
kubectl set image deployment/android-generator android-generator=eventyay/android-generator:$COMMIT_HASH
