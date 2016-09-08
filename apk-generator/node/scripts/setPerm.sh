#!bin/bash

#Set read, write and exec permissions for android SDK

chmod -R  777 /var/www/android-sdk-linux

#Set read, write and exec permissions for files inside /var/www/html

chmod -R 777 /var/www/html/

#Set read, write and exec permissions for the apk output directory

chmod 777 /var/www/files/
