#! /usr/bin/env python
import os
import json,sys
from firebase import firebase
import requests
import subprocess

arg = sys.argv[1]
# Path to be created
path = "/var/www/files/"+str(arg)
print path
if not os.path.exists(path):
    os.makedirs(path)

firebase = firebase.FirebaseApplication('https://app-generator.firebaseio.com', None)
result = firebase.get('/users', str(arg))
jsonData = json.dumps(result)
email = json.dumps(result['Email'])
email = email.replace('"', '')
print email
directory = path + "/" + email
print directory

if not os.path.exists(directory):
    os.makedirs(directory)

subprocess.call(['./clone.sh', directory])

with open(directory+"/open-event-android/android/app/src/main/assets/config.json", "wb") as fo:
    fo.write(jsonData)
# subprocess.call(['./setPerm.sh', directory])
subprocess.call(['./buildApk.sh', directory])
subprocess.call(['./email.sh', directory, email])


print "Script End"

