#! /usr/bin/env python
import os
import json
import sys
from firebase import firebase
import requests
from tempfile import mkstemp
import subprocess
from tempfile import mkstemp
from shutil import move
from os import remove, close


def replace(file_path, pattern, subst):
    # Create temp file
    fh, abs_path = mkstemp()
    with open(abs_path, 'w') as new_file:
        with open(file_path) as old_file:
            for line in old_file:
                new_file.write(line.replace(pattern, subst))
    close(fh)
    # Remove original file
    remove(file_path)
    # Move new file
    move(abs_path, file_path)

arg = sys.argv[1]
# Path to be created
path = "/var/www/files/" + str(arg)
print path
if not os.path.exists(path):
    os.makedirs(path)

firebase = firebase.FirebaseApplication(
    'https://app-generator.firebaseio.com', None)
result = firebase.get('/users', str(arg))
jsonData = json.dumps(result)
email = json.dumps(result['Email'])
email = email.replace('"', '')
app_name = json.dumps(result['App_Name'])
app_name = app_name.replace('"', '')
print app_name
print email
directory = path + "/" + email
print directory

with open('/var/www/config.json') as json_data:
    config = json.load(json_data)

conApi = config["api"]
conSender = config["sender"]
conTitle = config["title"]
conBody = config["body"]


if not os.path.exists(directory):
    os.makedirs(directory)
print conApi
subprocess.call(['/var/www/scripts/clone.sh', directory])
with open(directory + "/open-event-android/android/app/src/main/assets/config.json", "wb") as fo:
    fo.write(jsonData)

absDirectory = directory + "/open-event-android/android/"
replace(directory + "/open-event-android/android/app/build.gradle",
        '"org.fossasia.openevent"', '"org.fossasia.openevent.' + app_name.split()[0] + '"')
replace(directory + "/open-event-android/android/app/src/main/res/values/strings.xml",
        'OpenEvent', app_name)
subprocess.call(['/var/www/scripts/buildApk.sh', directory])
subprocess.call(['/var/www/scripts/copyApk.sh', absDirectory, arg])
# subprocess.call(['/var/www/scripts/email.sh', directory, email, conBody,
# conTitle, arg])  #use this with mutt
# use this for sending the UID and email to php file for sendgrid
subprocess.call(['/var/www/scripts/passapi.sh', arg, email])
print "Script End"
