# How to install the Open Event Android App Generator on AWS

## Build Server Requirements

* Operating system Ubuntu 14.04 or above
* [Android SDK for linux](http://stackoverflow.com/a/19416222/5471095)
* [Apache and PHP installed](https://www.digitalocean.com/community/tutorials/how-to-install-linux-apache-mysql-php-lamp-stack-on-ubuntu)
* Mutt and MSMTP [installed and configured](https://opev.wordpress.com/2016/06/15/sending-e-mail-from-linux-terminal/)
* Python 2.7 or 3.4 [installed](http://askubuntu.com/questions/350751/install-and-run-python-3-at-the-same-time-than-python-2)
* [Python-Firebase interface](https://pypi.python.org/pypi/python-firebase/1.2)
* Recommended: A server with 2GB RAM, 4 Core CPU and 40GB of storage space is recommended <br>

## Required Components
* **[index.html](https://github.com/fossasia/open-event-android/blob/master/apk-generator/index.html)** A html file which can server as the landing page for your server.
* **[runPy.php](https://github.com/fossasia/open-event-android/blob/master/apk-generator/scripts/runPy.php)** A php script that will launch python script by taking input from index.html.
* **[appgenserver.py](https://github.com/fossasia/open-event-android/blob/master/apk-generator/scripts/appgenserver.py)** A python script containg commands to compile and generate the app
* **[clone.sh](https://github.com/fossasia/open-event-android/blob/master/apk-generator/scripts/clone.sh)** A bash script used for cloning the source code for android app onto the server.
* **[buildApk.sh](https://github.com/fossasia/open-event-android/blob/master/apk-generator/scripts/buildApk.sh)** A bash script used for initiating the app's build and signing it once the app has been generated.
* **[email.sh](https://github.com/fossasia/open-event-android/blob/master/apk-generator/scripts/email.sh)** A bash script used to send the email containing the generated app as an attachment.
* **[upload.php](https://github.com/fossasia/open-event-android/blob/development/apk-generator/scripts/upload.php)** A php script that will upload the json zip that is chosed by the user.
* **[uploadhelper.php](https://github.com/fossasia/open-event-android/blob/development/apk-generator/scripts/uploadHelper.php)** Takes in the timestamp of user as input and moves the uploaded script to a unique location for each user.
* **[api.php](https://github.com/fossasia/open-event-android/blob/development/apk-generator/api/api.php)** A php script that serves as an API and handles incoming requests to the server.
* **[copyapk.sh](https://github.com/fossasia/open-event-android/blob/development/apk-generator/scripts/copyApk.sh)** A bash script that will copy the generated and signed apk to the release folder in `public-html` of the server.

## Server Setup



## Glosarry

| Term        | Meaning           |
| ------------- |:-------------|
| Build Server     | Backend which generates the app when your details are submitted on the app-generator website  |
| Script    | Executable files containing code which help in compiling the app      |
| Root User | User who is currently accessing the build server directly      | 
| www-data  | Public user who accesses the server via the webpage  |
| Terminal | Tool for accessing the computer remotely  |
| Mutt/MSMTP | Tools used for sending the email from terminal to the user  |
| var/www | Your server's public directory (Anyone can read the stuff placed here)  |
| Firebase | An online data storage service used for storing the data input by the user and later retrieving it at the server|
