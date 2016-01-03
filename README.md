# open-event-android
[![Build Status](https://travis-ci.org/fossasia/open-event-android.svg)](https://travis-ci.org/fossasia/open-event-android)
The Open Event Android Client
[![Coverage Status](https://coveralls.io/repos/fossasia/open-event-android/badge.svg?branch=master&service=github)](https://coveralls.io/github/fossasia/open-event-android?branch=master)

The Android client is a generic app that has two parts:
a) A standard configuration file, that sets the details of the app (e.g. color scheme, logo of event, link to JSON app data)
b) The Android app itself
This app uses the json api provided by a server maintained [here](https://github.com/fossasia/open-event-orga-server). 

## Development Setup
Before you begin, you should already have the Android Studio SDK downloaded and set up correctly. You can find a guide on how to do this here: [Setting up Android Studio](http://developer.android.com/sdk/installing/index.html?pkg=studio)

### Setting up the Android Project
1. Download the *open-event-android* project source. You can do this either by forking and cloning the repository (recommended if you plan on pushing changes) or by downloading it as a ZIP file and extracting it.

2. Open Android Studio, you will see a **Welcome to Android** window. Under Quick Start, select *Import Project (Eclipse ADT, Gradle, etc.)*

3. Navigate to the directory where you saved the open-event-android project, select the root folder of the project (the folder named "open-event-android"), and hit OK. Android Studio should now begin building the project with Gradle.

4. Once this process is complete and Android Studio opens, check the Console for any build errors. 

	- *Note:* If you recieve a Gradle sync error titled, "failed to find ...", you should click on the link below the error message (if avaliable) that says *Install missing platform(s) and sync project* and allow Android studio to fetch you what is missing.

5. Once all build errors have been resolved, you should be all set to build the app and test it.

6. To Build the app, go to *Build>Make Project* (or alternatively press the Make Project icon in the toolbar).

7. If the app was built succesfully, you can test it by running it on either a real device or an emulated one by going to *Run>Run 'app'* or presing the Run icon in the toolbar.

### Configuring the app

**Configuring Server and Web-App Urls**
- Browse the project directories and open (with Android Studio): *app/src/main/java/org/fossasia/openevent/api/Urls.java*
- In this file you will see several constant variables that allow you to set useful properties of the app, these include:
	* API_VERSION: Server API version. (Example: "v1")
	* EVENT_ID: ID of the event to load from server. (Example: 1)
	* BASE_URL: The base URL of the server. (Example: "http://springboard.championswimmer.in")
	* BASE_GET_URL_ALT: An alternative server base URL for testing. (Example: "https://raw.githubusercontent.com/fossasia/open-event/master/testapi")
	* WEB_APP_URL_BASIC: The full URL of the web app. (Example: "http://fossasia.github.io/open-event-webapp/#/"
	* SPEAKERS: The file-name of the speakers page of the web app. Added to the end of WEB_APP_URL_BASIC to form full link. (Example: "speakers")
	* TRACKS: The file-name of the tracks page of the web app. Added to the end of WEB_APP_URL_BASIC to form full link. (Example: "tracks")
	* SESSIONS: The file-name of the sessions page of the web app. Added to the end of WEB_APP_URL_BASIC to form full link. (Example: "sessions")
	* MAP: The file-name of the map page of the web app. Added to the end of WEB_APP_URL_BASIC to form full link. (Example: "map")

**Configuring App Theme / Localizations**
- The styles.xml files have been configured to allow easy customization of app themes.
- You can configure themes by changing various components found in the styles.xml files, found at:
	* */app/src/main/res/values/styles.xml*
	* */app/src/main/res/values-v21/styles.xml*

- Using *Theme Editor*:
	* You can also configure the theme of the app using Android Studio's *Theme Editor*. 
	* Go to *Tools>Android>Theme Editor* to open the Theme Editor.
	* From there you can configure the colors and styles of in-app elements using a neat UI.

- *Translations Editor*:
	* You can configure the string localizaions / translations using Android Studio's *Translations Editor*. 
	* Find /app/src/main/res/values/strings.xml
	* Right click on the file, and select *Open Translations Editor*.

- Editing Manually:
	* You can find the configuration files for the app for manual editing here:
	* */app/src/main/res/values/*
	* */app/src/main/res/values-v21/*
	* */app/src/main/res/values-w820dp/*

## Data retrieval
- The orga-server provides the data which is stored in its backend database in a json format.
- The app on startup picks up data from a json file in it's assets folder if the version number of data is -1 which happens when there is no internet connection
- If there is a valid internet connection, the data download starts from the server.
- Also there is a check on the version of data already there in the app's database. If data is stale then only it is downloaded.
- If database is empty then firstly json file in assets is accessed but if internet is available , latest data is downloaded.

##License
This project is currently licensed under the GNU General Public License v3. A copy of LICENSE.md should be present along with the source code. To obtain the software under a different license, please contact FOSSASIA.
