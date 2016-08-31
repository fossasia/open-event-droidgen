Some of the details in the app can be modified easily by editing configuration files (both for the Android App and the Apk Generator) <br>

Android App
=
The configuration file named [config.json](https://github.com/fossasia/open-event-android/blob/development/android/app/src/main/assets/config.json) is located inside the assets folder of the Android Source code and contains details like : 

Key  | Significance
------------- | -------------
Email  | Email of the user who created the app
App_Name  | Name of the App
Api_Link  | A REST API which will provide event's data to the app

App Generator
=
A similar config file named [config.json](https://github.com/fossasia/open-event-android/blob/development/apk-generator/scripts/config.json) is used in our App Generator which is located inside ```/var/www``` folder of the server. Its contents are : 

Key  | Significance
------------- | -------------
api  | API key received from SendGrid that will be used to send the email after app has been generated on the server
sender  | Email address of the sender (This email should be the one which was used to generate the API key above)
title  | Title of the sent email
body | Body of the sent email
orgaMessage | The message that should be displayed in the notification of the Orga Server Dashboard.
