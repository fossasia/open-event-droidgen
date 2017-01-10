#! /usr/bin/env python
import os
import json
import validators
import re
import sys
import urllib2
import urllib
from firebase import firebase
import requests
from tempfile import mkstemp
import subprocess
from tempfile import mkstemp
import zipfile
import shutil
from shutil import move, copyfile
from os import remove, close
from PIL import Image
reload(sys)
sys.setdefaultencoding('utf8')


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
print(path)
if not os.path.exists(path):
    os.makedirs(path)

firebase = firebase.FirebaseApplication(
    'https://app-generator.firebaseio.com', None)
result = firebase.get('/users', str(arg))

email = json.dumps(result['Email'])
email = email.replace('"', '')
api = json.dumps(result['Api_Link'])
api = api.replace('"', '')
mode = json.dumps(result['datasource'])
mode = mode.replace('"', '')
print(mode)
print(email)
directory = path + "/" + email
print(directory)
if mode == "jsonupload":
    # Set API link to null in case a zip was uploaded
    result['Api_Link'] = ""

jsonData = json.dumps(result)
with open('/var/www/config.json') as json_data:
    config = json.load(json_data)

conApi = config["api"]
conSender = config["sender"]
conTitle = config["title"]
conBody = config["body"]


if not os.path.exists(directory):
    os.makedirs(directory)
print(conApi)
subprocess.call(['/var/www/scripts/clone.sh', directory])


with open(directory + "/open-event-android/android/app/src/main/assets/config.json", "wb") as fo:
    fo.write(jsonData)

extractPath = directory + "/zip"
if not os.path.exists(extractPath):
    os.makedirs(extractPath)
print("/var/www/html/uploads/" + str(arg) + "/json.zip")
if os.path.exists("/var/www/html/uploads/" + str(arg) + "/json.zip"):
    subprocess.call(['/var/www/scripts/extractZip.sh',
                     "/var/www/html/uploads/" + str(arg) + "/json.zip", extractPath])

eventUrl = str(api) + "/event"

if mode == "eventapi":
    print(mode)
    request = urllib2.Request(eventUrl)
    event = urllib2.urlopen(request).read()
    eventJson = json.loads(event)
else:
    with open(directory + "/zip/event") as json_data:
        event = json.load(json_data)
    eventJson = event

app_name_orig = str(eventJson['name'])
app_name = re.sub('\W+', '', app_name_orig)
print app_name

back_image = str(object=eventJson['background_image'])
logo_path = eventJson['logo']
if back_image.startswith("/"):
    background = directory + "/zip" + back_image
    extension_back = background.rsplit('.', 1)
    back = Image.open(background)
    back.load()
    backNew = Image.new("RGB", back.size)
    backNew.paste(back)
    backNew = backNew.resize((600, 400), Image.ANTIALIAS)
    backNew.save(directory + "/zip/background.jpg", 'JPEG', quality=80)
    copyfile(directory + "/zip/background.jpg", directory +
             "/open-event-android/android/app/src/main/res/drawable/background." + extension_back[1])
elif back_image != "":
    urllib.urlretrieve(back_image, directory + "/background.png")
    back = Image.open(directory + "/background.png")
    back.load()
    backNew = Image.new("RGB", (back.size), (255, 255, 255))  #create a new image file
    backNew.paste(back)  # save existing image into this new file
    backNew = backNew.resize((600, 400), Image.ANTIALIAS)
    backNew.save(directory + "/background.jpg", 'JPEG', quality=80)  # Save it as jpeg file
    copyfile(directory + "/background.jpg", directory +
             "/open-event-android/android/app/src/main/res/drawable/background.jpg")
if logo_path == "":
    print("No logo specified")
    copyfile("/var/www/ic_launcher.png", directory +
             "/open-event-android/android/app/src/main/res/mipmap-hdpi/ic_launcher.png")
    copyfile("/var/www/ic_launcher.png", directory +
             "/open-event-android/android/app/src/main/res/mipmap-mdpi/ic_launcher.png")
    copyfile("/var/www/ic_launcher.png", directory +
             "/open-event-android/android/app/src/main/res/mipmap-xhdpi/ic_launcher.png")
    copyfile("/var/www/ic_launcher.png", directory +
             "/open-event-android/android/app/src/main/res/mipmap-xxhdpi/ic_launcher.png")
    copyfile("/var/www/ic_launcher.png", directory +
             "/open-event-android/android/app/src/main/res/mipmap-xxxhdpi/ic_launcher.png")
elif logo_path.startswith("/"):
    logo = directory + "/zip" + logo_path
    logo_ext = logo.rsplit('.', 1)
    os.remove(directory +
              "/open-event-android/android/app/src/main/res/mipmap-hdpi/ic_launcher.png")
    os.remove(directory +
              "/open-event-android/android/app/src/main/res/mipmap-mdpi/ic_launcher.png")
    os.remove(directory +
              "/open-event-android/android/app/src/main/res/mipmap-xhdpi/ic_launcher.png")
    os.remove(directory +
              "/open-event-android/android/app/src/main/res/mipmap-xxhdpi/ic_launcher.png")
    os.remove(directory +
              "/open-event-android/android/app/src/main/res/mipmap-xxxhdpi/ic_launcher.png")
    copyfile(logo, directory +
             "/open-event-android/android/app/src/main/res/mipmap-hdpi/ic_launcher." + logo_ext[1])
    copyfile(logo, directory +
             "/open-event-android/android/app/src/main/res/mipmap-mdpi/ic_launcher." + logo_ext[1])
    copyfile(logo, directory +
             "/open-event-android/android/app/src/main/res/mipmap-xhdpi/ic_launcher." + logo_ext[1])
    copyfile(logo, directory +
             "/open-event-android/android/app/src/main/res/mipmap-xxhdpi/ic_launcher." + logo_ext[1])
    copyfile(logo, directory +
             "/open-event-android/android/app/src/main/res/mipmap-xxxhdpi/ic_launcher." + logo_ext[1])
elif validators.url(logo_path):
    print(logo_path)
    urllib.urlretrieve(logo_path, directory + "/ic_launcher.png")
    img = Image.open(directory + "/ic_launcher.png")
    img.save('ic-launcher', 'png')
    img = Image.open(directory + "/ic_launcher.png")
    img.load()
    til = Image.new("RGB", img.size, (255, 255, 255))
    til.paste(img)
    til.save(directory + "/ic_launcher.jpg", 'JPEG', quality=80)
    os.remove(directory +
              "/open-event-android/android/app/src/main/res/mipmap-hdpi/ic_launcher.png")
    os.remove(directory +
              "/open-event-android/android/app/src/main/res/mipmap-mdpi/ic_launcher.png")
    os.remove(directory +
              "/open-event-android/android/app/src/main/res/mipmap-xhdpi/ic_launcher.png")
    os.remove(directory +
              "/open-event-android/android/app/src/main/res/mipmap-xxhdpi/ic_launcher.png")
    os.remove(directory +
              "/open-event-android/android/app/src/main/res/mipmap-xxxhdpi/ic_launcher.png")
    copyfile(directory + "/ic_launcher.png", directory +
             "/open-event-android/android/app/src/main/res/mipmap-hdpi/ic_launcher.jpg")
    copyfile(directory + "/ic_launcher.png", directory +
             "/open-event-android/android/app/src/main/res/mipmap-mdpi/ic_launcher.jpg")
    copyfile(directory + "/ic_launcher.png", directory +
             "/open-event-android/android/app/src/main/res/mipmap-xhdpi/ic_launcher.jpg")
    copyfile(directory + "/ic_launcher.png", directory +
             "/open-event-android/android/app/src/main/res/mipmap-xxhdpi/ic_launcher.jpg")
    copyfile(directory + "/ic_launcher.png", directory +
             "/open-event-android/android/app/src/main/res/mipmap-xxxhdpi/ic_launcher.jpg")

absDirectory = directory + "/open-event-android/android/"
replace(directory + "/open-event-android/android/app/src/main/res/values/strings.xml",
        'OpenEvent', app_name)
replace(directory + "/open-event-android/android/app/src/main/res/layout/nav_header.xml",
        'twitter', "background")
replace(directory + "/open-event-android/android/app/build.gradle",
        '"org.fossasia.openevent"', '"org.fossasia.openevent.' + app_name.replace(" ", "") + '"')
for f in os.listdir(directory + "/zip"):
    #       print "no" + f
    if os.path.isfile(os.path.join(directory + "/zip", f)):
        copyfile(directory + "/zip/" + f, directory +
                 "/open-event-android/android/app/src/main/assets/" + f)

subprocess.call(['/var/www/scripts/buildApk.sh', directory])
subprocess.call(['/var/www/scripts/copyApk.sh', absDirectory, arg])
subprocess.call(['/var/www/scripts/passapi.sh', arg, email])
subprocess.call(['python', '/var/www/scripts/sendNotif.py', email, arg])
shutil.rmtree(directory)
print("Script End")
