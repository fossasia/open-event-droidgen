#! /usr/bin/env python
import os
import json,sys
from firebase import firebase
import requests
from tempfile import mkstemp
import subprocess
from tempfile import mkstemp
import zipfile
import shutil
from shutil import move, copyfile
from os import remove, close

def replace(file_path, pattern, subst):
    #Create temp file
    fh, abs_path = mkstemp()
    with open(abs_path,'w') as new_file:
        with open(file_path) as old_file:
            for line in old_file:
                new_file.write(line.replace(pattern, subst))
    close(fh)
    #Remove original file
    remove(file_path)
    #Move new file
    move(abs_path, file_path)

arg = sys.argv[1]
# Path to be created
path = "/var/www/files/"+str(arg)
print path
if not os.path.exists(path):
    os.makedirs(path)

firebase = firebase.FirebaseApplication('https://app-generator.firebaseio.com', None)
result = firebase.get('/users', str(arg))
#firebase2 = firebase.FirebaseApplication('gs://app-generator.appspot.com', None)
#resulted = firebase.get(str(arg),None)
#print resulted
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
# subprocess.call(['/var/www/html/setPerm.sh', directory])
with open(directory+"/open-event-android/android/app/src/main/assets/config.json", "wb") as fo:
    fo.write(jsonData)

absDirectory = directory + "/open-event-android/android/"
# subprocess.call(['./setPerm.sh', directory])
replace(directory+"/open-event-android/android/app/build.gradle", '"org.fossasia.openevent"', '"org.fossasia.openevent.'+app_name.replace(" ", "")+'"')
replace(directory+"/open-event-android/android/app/src/main/res/values/strings.xml", 'OpenEvent', app_name)
extractPath = directory + "/zip"
if not os.path.exists(extractPath):
    os.makedirs(extractPath)
print "/var/www/html/uploads/" + str(arg) + "/json.zip"
if os.path.exists("/var/www/html/uploads/" + str(arg) + "/json.zip"):
	with zipfile.ZipFile("/var/www/html/uploads/" + str(arg) + "/json.zip") as zip_file:
    		for member in zip_file.namelist():
        		filename = os.path.basename(member)
        		# skip directoriesi
			print filename
        		if not filename:
       				continue
			if filename.startswith("."):
				continue

        # copy file (taken from zipfile's extract)
        		source = zip_file.open(member)
        		target = file(os.path.join(directory + "/zip", filename), "wb")
        		with source, target:
            			shutil.copyfileobj(source, target)
#zip_ref = zipfile.ZipFile("/var/www/html/uploads/" + str(arg) + "/json.zip", 'r')
#zip_ref.extractall(directory + "/zip")
#zip_ref.close()
for f in os.listdir(directory + "/zip"):
#	print "no" + f
 	if f.endswith('.json'):
 		copyfile(directory + "/zip/" + f, directory + "/open-event-android/android/app/src/main/assets/"+f)
		print f
	elif f.endswith('.png'):
 		copyfile(f, directory + "open-event-android/android/app/src/main/res/drawable"+f)
        replace(directory+"/open-event-android/android/app/src/main/res/values/strings.xml", 'mipmap/ic_launcher', 'drawable/' + f)

subprocess.call(['/var/www/scripts/buildApk.sh', directory])
subprocess.call(['/var/www/scripts/copyApk.sh', absDirectory, arg])
#subprocess.call(['/var/www/scripts/email.sh', directory, email, conBody, conTitle, arg])
subprocess.call(['/var/www/scripts/passapi.sh', arg, email])
print "Script End"
print "consender" + conSender
