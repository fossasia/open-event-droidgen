import os
import json

import subprocess
import requests

jsonData = requests.get('https://raw.githubusercontent.com/mananwason/apk-generator/master/config').content

data = json.loads(jsonData)
email = data['Email']

directory = '/root/' + email

if not os.path.exists(directory):
    os.makedirs(directory)

with open(directory+"/config.json", "wb") as fo:
    fo.write(jsonData)

subprocess.call(['./test.sh', directory])

print "Script End"
