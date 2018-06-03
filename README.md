# Open Event Android

This is the apk branch of the The Open Event Android project, that holds a sample Android app that gets rendered on pull requests in the development branch and can be used to explore the features of the app and for testing.

The project has two components: 1. The **App Generator**, a web application that is hosted on a server and generates an event Android app from a zip with JSON and binary files and 2. A generic **Android app** - the output of the app generator. The mobile app can be installed on any Android device for browsing information about the event.

More information on the project on the Readme.md of the development branch.

## Functioning of the APK Branch

Travis does not support Android and docker builds simultaneously, but this is what is required in order to get automatic builds of the Android app. This is reason we aren't able to have Google Cloud deployments in the master/development barnch in the google cloud branch itself. And therefore we need docker for Google cloud deployment. Hence, it is implemented here in the meta/deployment folder.

For every push to dev, an apk gets generated along with some meta files (branch, commit hash) and gets pushed to the apk branch. And this push will trigger the Google Cloud deployment using the apk branch's travis script (here we can use docker since there is no other build involved here).
