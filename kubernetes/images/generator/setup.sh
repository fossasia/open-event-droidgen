#!/bin/bash
# Prepare repository
git clone ${REPOSITORY} open_event_android
cd open_event_android
git checkout ${BRANCH}

# Set environment variable
export GENERATOR_WORKING_DIR=${WORKSPACE}/temp/
export KEYSTORE_PATH=${WORKSPACE}/generator.keystore
export KEYSTORE_PASSWORD=generator
export KEY_ALIAS=generator

# Prepare working folders
export GENERATOR_WORKING_DIR=${WORKSPACE}/temp/
mkdir -p ${GENERATOR_WORKING_DIR}uploads/
chmod 0777 -R ${GENERATOR_WORKING_DIR}

keytool -genkey -noprompt \
 -alias ${KEY_ALIAS} \
 -dname "CN=eventyay.com, OU=SG, O=FOSSASIA, L=Singapore, S=Singapore, C=SG" \
 -keystore ${KEYSTORE_PATH} \
 -storepass ${KEYSTORE_PASSWORD} \
 -keypass ${KEYSTORE_PASSWORD} \
 -keyalg RSA \
 -keysize 2048 \
 -validity 10000

cd apk-generator/v2
pip install --no-cache-dir -r requirements.txt