# Open Event Android

[![Build Status](https://travis-ci.org/fossasia/open-event-android.svg?branch=development)](https://travis-ci.org/fossasia/open-event-android?branch=development)
[![codecov.io](https://codecov.io/github/fossasia/open-event-android/coverage.svg?branch=development)](https://codecov.io/github/fossasia/open-event-android?branch=development)
[![Join the chat at https://gitter.im/fossasia/open-event-android](https://badges.gitter.im/fossasia/open-event-android.svg)](https://gitter.im/fossasia/open-event-android?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
[![Preview the app](https://img.shields.io/badge/Preview-Appetize.io-orange.svg)](https://appetize.io/app/2rfx5pavny47jnb1qzwg204fr8)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/d32f87844a9346d09f3e8ad09600d3e1)](https://www.codacy.com/app/dev_19/open-event-android?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=fossasia/open-event-android&amp;utm_campaign=Badge_Grade)
[![Mailing List](https://img.shields.io/badge/Mailing%20List-FOSSASIA-blue.svg)](mailto: fossasia@googlegroups.com)
[![Hosted on DigitalOcean](/docs/badges/digitalocean.png)](http://digitalocean.com)


The Open Event Android project consists of two components. The **App Generator** is a web application that is hosted on a server and generates an event Android app from a zip with JSON and binary files ([examples here](http://github.com/fossasia/open-event)) or through an API. The second component we are developing in the project is a generic **Android app** - the output of the app generator. The mobile app can be installed on any Android device for browsing information about the event. Updates can be made automatically through API endpoint connections from an online source (e.g. server), which needs to defined in the provided event zip with the JSON files. The Android app has a standard configuration file, that sets the details of the app (e.g. color scheme, logo of event, link to JSON app data).

## Communication

Please join our mailing list to discuss questions regarding the project: https://groups.google.com/forum/#!forum/open-event

Our chat channel is on gitter here: https://gitter.im/fossasia/open-event-android

<br>

## 1. Android App

This is a generic app that has two parts:

A) A standard configuration file, that sets the details of the app (e.g. color scheme, logo of event, link to JSON app data). A sample of the JSON format is maintained in the [Open Event Repository](http://github.com/fossasia/open-event).

B) A JSON API provided by open-event-orga-servers server (Code maintained [here](https://github.com/fossasia/open-event-orga-server)).

### Sample App

A sample app for testing purposes is automatically built after commits in the repository. Please [download and test the app here](/sample-apk).

### Screenshots of a sample Android app

![alt-tag](docs/screenshots/ss.png)

### Android App Development Set up

Please find info about the set up of the Android app in your development environment [here](https://github.com/fossasia/open-event-android/blob/development/docs/ANDROID_APP_Setup.md).

### Data retrieval

- The orga-server provides the data which is stored in its backend database in a json format.
- The app on startup picks up data from a JSON file in its assets folder if the version number of data is -1, which happens when there is no internet connection
- If there is a valid internet connection, the data download starts from the server.
- Also there is a check on the version of data already there in the app's database. If data is stale then only it is downloaded.
- If database is empty then firstly JSON file in assets is accessed but if internet is available , latest data is downloaded.

### Libraries used and their documentation

- Otto [Docs](http://square.github.io/otto/1.x/otto/)
- Retrofit [Docs](http://square.github.io/retrofit/2.x/retrofit/)
- ButterKnife [Docs](http://jakewharton.github.io/butterknife/javadoc/)
- Timber [Docs](http://jakewharton.github.io/timber/)
- Google Gson [Docs](http://www.javadoc.io/doc/com.google.code.gson/gson/2.7)
- LeakCanary [Docs](https://github.com/square/leakcanary)
- Picasso [Docs](http://square.github.io/picasso/2.x/picasso/)

### Devices tested on

| Device        | Android Version           | Skin/ROM      |
| ------------- |:-------------|-------------|
| OnePlus 3     | Android 6.0  | OxygenOS |
| Nexus 5X    | Android 7.0      |  AOSP |
| Nexus 5X    | Android 6.0      |  CyanogenMod 13 |
| Nexus 5    | Android 4.4      |  AOSP |
| Redmi Note 3 | Android 5.0  | MIUI|
| Moto G4 Plus | Android 7.0  | Stock Android |

<br>

## 2. App Generator

#### Technology Stack
- Flask
- Celery
- JDK 8
- Android SDK
- Redis

Setup and installation instructions can be obtained [here](https://github.com/niranjan94/open-event-android/tree/development/apk-generator/v2).

## Contributions, Bug Reports, Feature Requests

This is an Open Source project and we would be happy to see contributors who report bugs and file feature requests submitting pull requests as well. Please report issues here https://github.com/fossasia/open-event-android/issues

## Branch Policy

We have the following branches
 * **development**
	 All development goes on in this branch. If you're making a contribution,
	 you are supposed to make a pull request to _development_.
	 PRs to master must pass a build check and a unit-test (_app/src/test_) check on Travis
 * **master**
   This contains shipped code. After significant features/bugfixes are accumulated on development, we make a version update, and make a release.
	 All tagged commits on _master_ branch will automatically generate a release on Github with a copy of ***fDroid-debug*** and ***GooglePlay-debug*** apks.

## Code Style
For contributions please read the [CODESTYLE](docs/CODESTYLE.md) carefully. Pull requests that do not match the style will be rejected.

## License
This project is licensed under the GNU General Public License v3. A copy of [LICENSE](LICENSE.md) is to be present along with the source code. To obtain the software under a different license, please contact FOSSASIA.

## Maintainers
The project is maintained by
- Harshit Dwivedi ([@the-dagger](https://github.com/the-dagger))
- Manan Wason ([@mananwason](https://github.com/mananwason))
- Mario Behling ([@mariobehling](http://github.com/mariobehling))
- Justin Lee ([@juslee](http://github.com/juslee))
