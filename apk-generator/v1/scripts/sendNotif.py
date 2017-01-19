#! /usr/bin/env python

import requests
import json
import sys

response = requests.request("POST",
                            'http://open-event-dev.herokuapp.com/api/v2/login',
                            data=json.dumps({
                                'email': "harshithdwivedi@gmail.com",
                                'password': "fossasia"
                            }),
                            headers={'content-type': 'application/json'}
                            )
print(response.text)

token = json.loads(response.text)['access_token']

email = sys.argv[1]

data = {
    "message": "string",
    "action": "string",
    "email": "rafal.arte@gmail.com",
    "title": "string"
}
with open('/var/www/config.json') as json_data:
    config = json.load(json_data)

conTitle = config["title"]
orgaBody = config["orgaMessage"]

data['email'] = email
data['message'] = orgaBody + str(sys.argv[2])
data['title'] = conTitle
print(data)

response = requests.request("POST",
                            'http://open-event-dev.herokuapp.com/api/v2/events/126/notifications',
                            data=json.dumps(data),
                            headers={
                                'content-type': 'application/json',
                                'Authorization': 'JWT %s' % token
                            }
                            )

print(response.text)
