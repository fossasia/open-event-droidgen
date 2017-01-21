#!/bin/bash

echo "Copy the signed apk for download"
path=$1
stamp=$2

mkdir /var/www/html/release/$2

cp $1releaseapk.apk /var/www/html/release/$2/
