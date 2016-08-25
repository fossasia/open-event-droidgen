# How to install the Open Event Android App Generator on my local machine

# Build Server Requirements

- Operating system Ubuntu 14.04 or above is preferred
- [Android SDK for linux](http://stackoverflow.com/a/19416222/5471095)
- [Apache and PHP installed](https://www.digitalocean.com/community/tutorials/how-to-install-linux-apache-mysql-php-lamp-stack-on-ubuntu)
- [SendGrid PHP library](https://github.com/sendgrid/sendgrid-php) configured.
- Python 2.7 or 3.4 [installed](http://askubuntu.com/questions/350751/install-and-run-python-3-at-the-same-time-than-python-2)
- [Python-Firebase interface](https://pypi.python.org/pypi/python-firebase/1.2)
- Recommended: A server with 2GB RAM, 4 Core CPU and 40GB of storage space is recommended<br>

## Required Components

- **[index.html](https://github.com/fossasia/open-event-android/blob/master/apk-generator/index.html)** A html file which can server as the landing page for your server.
- **[runPy.php](https://github.com/fossasia/open-event-android/blob/master/apk-generator/scripts/runPy.php)** A php script that will launch python script by taking input from index.html.
- **[appgenserver.py](https://github.com/fossasia/open-event-android/blob/master/apk-generator/scripts/appgenserver.py)** A python script containg commands to compile and generate the app
- **[clone.sh](https://github.com/fossasia/open-event-android/blob/master/apk-generator/scripts/clone.sh)** A bash script used for cloning the source code for android app onto the server.
- **[buildApk.sh](https://github.com/fossasia/open-event-android/blob/master/apk-generator/scripts/buildApk.sh)** A bash script used for initiating the app's build and signing it once the app has been generated.
- **[email.php](https://github.com/fossasia/open-event-android/blob/master/apk-generator/api/email.sh)** A php script used to send the email containing the generated app as an attachment.
- **[upload.php](https://github.com/fossasia/open-event-android/blob/development/apk-generator/scripts/upload.php)** A php script that will upload the json zip that is chosen by the user into a unique folder based on his timestamp.<br>
  **[api.php](https://github.com/fossasia/open-event-android/blob/development/apk-generator/api/api.php)** A php script that serves as an API and handles incoming requests to the server.
- **[copyapk.sh](https://github.com/fossasia/open-event-android/blob/development/apk-generator/scripts/copyApk.sh)** A bash script that will copy the generated and signed apk to the release folder in `public-html` of the server.

## Server Setup

1. Navigate to server's `/var/www/` and install the Android SDK here.
2. You can do that by following this [thread](http://stackoverflow.com/questions/17963508/how-to-install-android-sdk-build-tools-on-the-command-line/) on stackoverflow.
3. Edit the .bashrc file located in /root to reference to the SDK you have just installed

  To do this, type in `nano /root/.bashrc` and press enter.<br>
  Navigae to the bottom of the file that opens and add following code there<br>
  `export ANDROID_HOME=/var/www/android-sdk-linux`<br>
  `export PATH=${PATH}:$ANDROID_HOME/tools:$ANDROID_HOME/platform-tools`

4. Download the template app icon that is to be used during compilation if user hasn't provided us with any logo.<br>
  `sudo apt-get install wget`<br>
  `wget image_link_here`
5. Move all the files mentioned in the `scripts` folder (Except `runPy.php`, `upload.php` and `index.html`) into `/root/scripts`.
6. Move `runPy.php`, `upload.php`, and `index.html` to `/var/www/html`
7. [Install and setup](https://www.digitalocean.com/community/tutorials/how-to-install-java-with-apt-get-on-ubuntu-16-04) JAVA on your machine.
8. Next, install PHP.This can be done by simply writing `sudo apt-get install php` in your terminal.<br>
  Now [modify the maximum upload limit of php](http://stackoverflow.com/questions/2184513/php-change-the-maximum-upload-file-size) to atleast `500MB`. (Default is 2MB)
9. Mdify your sudoers list to allow www-data to run the runPy.php script as root You can do this by typing the following command `sudo visudo` and hitting enter<br>
  Next scroll to the bottom of the file and add `www-data ALL = NOPASSWD: /var/www/html/runPy.php`
10. Create a [firebase account](firebase.google.com) and add your API key to index.html<br>
  Navigate to your Account and then create a new project (Name it whatever you want)<br>
  Open this newly created project and click the Pink colored button saying _"Add Firebase to your web app"_<br>
  Copy the code listed there and paste it to [index.html](https://github.com/fossasia/open-event-android/blob/master/apk-generator/index.html#L76) Also add your server's IP address to trusted sources in the [Firebase console](https://console.firebase.google.com/project/app-generator/authentication/providers) by clicking on _Add Domain_
11. Create another folder in your public-html named `api` and move contents of the folder `api` here.
12. The sender's address, message title and the body of email is defined in `conig.json` inside `apk-generator/scripts`. Changing this will affect the email that is sent.

## Glosarry

Term         | Meaning
------------ | :---------------------------------------------------------------------------------------------------------------
Build Server | Backend which generates the app when your details are submitted on the app-generator website
Script       | Executable files containing code which help in compiling the app
Root User    | User who is currently accessing the build server directly
www-data     | Public user who accesses the server via the webpage
Terminal     | Tool for accessing the computer remotely
Sendgrid     | Tool used for sending the email from terminal to the user
var/www      | Your server's public directory (Anyone can read the stuff placed here)
Firebase     | An online data storage service used for storing the data input by the user and later retrieving it at the server
