[![Build Status](https://travis-ci.org/fossasia/open-event-android.svg?branch=master)](https://travis-ci.org/fossasia/open-event-android)
[![codecov.io](https://codecov.io/github/fossasia/open-event-android/coverage.svg?branch=master)](https://codecov.io/github/fossasia/open-event-android?branch=master)
[![todofy badge](https://todofy.org/b/fossasia/open-event-android)](https://todofy.org/r/fossasia/open-event-android)
[![Join the chat at https://gitter.im/fossasia/open-event-android](https://badges.gitter.im/fossasia/open-event-android.svg)](https://gitter.im/fossasia/open-event-android?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
[![Preview the app](http://i.imgur.com/iuWpLuX.png)](https://appetize.io/app/2rfx5pavny47jnb1qzwg204fr8)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/d32f87844a9346d09f3e8ad09600d3e1)](https://www.codacy.com/app/dev_19/open-event-android?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=fossasia/open-event-android&amp;utm_campaign=Badge_Grade) <br>
[![Get it on GPlay](http://i.imgur.com/rZYxvAo.png)](https://play.google.com/store/apps/details?id=org.fossasia.openevent&hl=en)

## Open Event Android 

Open Event Android consists of 2 main parts : <br>

1. [App Generator](https://github.com/fossasia/open-event-android/blob/master/docs/ApkGenerator.md) hosted on a server
2. [Android client](https://github.com/fossasia/open-event-android/blob/master/docs/AndroidApp.md) that is can be installed on any Android device for browsing any event

Follow the HyperLinks above for documentation on how to setup the server and android app.

###Video Walkthrough
[![FOSSASIA](https://img.youtube.com/vi/n5G4yw3t--U/0.jpg)](https://www.youtube.com/watch?v=n5G4yw3t--U)

### Screenshots  
![alt-tag](android/screenshots/ss2.PNG)

![alt-tag](android/screenshots/ss1.PNG)

![alt-tag](android/screenshots/ss3.PNG)

![alt-tag](android/screenshots/ss4.PNG)

## Documentation

You can find the apk generator docs [here](docs/AndroidApp.md) and the android app docs [here](docs/ApkGenerator.md)

## Branches and Contribution policy
We have the following branches   
 * **development**   
	 All development goes on in this branch. If you're making a contribution,
	 you are supposed to make a pull request to _development_.
	 PRs to master must pass a build check and a unit-test (_app/src/test_) check on Travis
 * **master**   
   This contains shipped code. After significant features/bugfixes are accumulated on development, we make a version update, and make a release.
	 All tagged commits on _master_ branch will automatically generate a release on Github with a copy of ***fDroid-debug*** and ***GooglePlay-debug*** apks.

## A note about Codestyle
Please read our [CODESTYLE](CODESTYLE.md) carefully. Pull requests that do not match the style will be rejected.

## License
This project is currently licensed under the GNU General Public License v3. A copy of LICENSE.md should be present along with the source code. To obtain the software under a different license, please contact FOSSASIA.
