[![Build Status](https://travis-ci.org/fossasia/open-event-android.svg?branch=development)](https://travis-ci.org/fossasia/open-event-android?branch=development)
[![codecov.io](https://codecov.io/github/fossasia/open-event-android/coverage.svg?branch=development)](https://codecov.io/github/fossasia/open-event-android?branch=development)
[![todofy badge](https://todofy.org/b/fossasia/open-event-android)](https://todofy.org/r/fossasia/open-event-android)
[![Join the chat at https://gitter.im/fossasia/open-event-android](https://badges.gitter.im/fossasia/open-event-android.svg)](https://gitter.im/fossasia/open-event-android?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
[![Preview the app](http://i.imgur.com/iuWpLuX.png)](https://appetize.io/app/2rfx5pavny47jnb1qzwg204fr8)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/d32f87844a9346d09f3e8ad09600d3e1)](https://www.codacy.com/app/dev_19/open-event-android?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=fossasia/open-event-android&amp;utm_campaign=Badge_Grade) <br>
[![Get it on GPlay](http://i.imgur.com/rZYxvAo.png)](https://play.google.com/store/apps/details?id=org.fossasia.openevent&hl=en) <br>
[![Hosted on DigitalOcean](http://i.imgur.com/dfRvhAG.png)](http://192.241.232.231/)
## Open Event Android 

Open Event Android consists of 2 main parts : <br>

1. [App Generator](https://github.com/fossasia/open-event-android/tree/development/apk-generator) hosted on [DigitalOcean](https://www.digitalocean.com/).
2. [Android client](https://github.com/fossasia/open-event-android/tree/development/android) that is can be installed on any Android device for browsing any event

##Using the App Generator
The app generator can be accessed in 2 ways,

###[Via the App Genrator Web-Page](http://192.241.232.231/)
Runs on an APACHE server hosted on DigitalOcean.

###[Via POST API](http://192.241.232.231/api/api.php)
The POST API takes input in form of a JSON and then creates and emails you the app.<br>

Sending a POST Request <br>

You can use [Postman](https://chrome.google.com/webstore/detail/postman/fhbjgbiflinjbdggehcddcbncdddomop?hl=en) for Chrome or [RESTClient](https://addons.mozilla.org/de/firefox/addon/restclient/) for Firefox for making API calls easily.

Input to the API should be of the following format <br>
```{ "email": "example@example.com", "app_name": "Name", "endpoint": "http://valid-endpoint.com/" } ```<br>

After taking the request through the POST API, the app will be generated and emailed to the email address provided in the request body.

## Maintainers
The project is maintained by
- Harshit Dwivedi ([@the-dagger](https://github.com/the-dagger))
- Manan Wason ([@mananwason](https://github.com/mananwason))
- Mario Behling ([@mariobehling](http://github.com/mariobehling))
- Justin Lee ([@juslee](http://github.com/juslee))

##Video Walkthrough
[![FOSSASIA](https://img.youtube.com/vi/n5G4yw3t--U/0.jpg)](https://www.youtube.com/watch?v=n5G4yw3t--U)

## Screenshots  
![alt-tag](docs/screenshots/ss.png)

## Documentation

You can find the apk generator docs [here](docs/ApkGenerator.md) and the android app docs [here](docs/AndroidApp.md)

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
Please read our [CODESTYLE](docs/CODESTYLE.md) carefully. Pull requests that do not match the style will be rejected.

## License
This project is currently licensed under the GNU General Public License v3. A copy of [LICENSE](LICENSE.md) is to be present along with the source code. To obtain the software under a different license, please contact FOSSASIA.
