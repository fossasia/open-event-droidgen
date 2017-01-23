#!/bin/bash

sudo rm -rf $1/open-event-android/android/app/src/main/assets/*

sudo touch $1/open-event-android/android/app/src/main/assets/config.json

#deletes the pre packages assets from the source code
