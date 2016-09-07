# Firebase - App success made simple


## Overview

[Firebase](https://firebase.google.com) provides the tools and infrastructure
you need to develop, grow, and earn money from your app. This package supports
web (browser), mobile-web, and server (Node.js) clients.

For more information, visit:

- [Firebase Realtime Database](https://firebase.google.com/docs/database/web/start) -
  The Firebase Realtime Database lets you store and query user data, and makes
  it available between users in realtime.
- [Firebase Storage](https://firebase.google.com/docs/storage/web/start) -
  Firebase Storage lets you upload and store user generated content, such as
  files, and images.
- [Firebase Authentication](https://firebase.google.com/docs/auth/web/manage-users) -
  Firebase helps you authenticate and manage users who access your application.
- [Create and setup your account](https://firebase.google.com/docs/web/setup) -
  Get started using Firebase for free.

## Get the code (browser)

### Script include

Include Firebase in your web application via a `<script>` tag:

```
<script src="https://www.gstatic.com/firebasejs/3.3.0/firebase.js"></script>

<script>
  var app = firebase.initializeApp({
    apiKey: '<your-api-key>',
    authDomain: '<your-auth-domain>',
    databaseURL: '<your-database-url>',
    storageBucket: '<your-storage-bucket>'
  });
  // ...
</script>
```

*Note: To get a filled in version of the above code snippet, go to the
[Firebase console](https://console.firebase.google.com/) for your app and click on "Add
Firebase to your web app".*

### npm bundler (Browserify, Webpack, etc.)

The Firebase JavaScript npm module contains both a browser version and a
version for the Node.js runtime. The browser version is designed to be used with
a package bundler (e.g., [Browserify](http://browserify.org/),
[Webpack](https://webpack.github.io/)).

Install the Firebase npm module:

```
$ npm init
$ npm install --save firebase
```

In your code, you can access Firebase using:

```
var firebase = require('firebase');
var app = firebase.initializeApp({ ... });
// ...
```

### Include only the features you need

The full Firebase JavaScript client includes support for Firebase Authentication,
the Firebase Realtime Database, and Firebase Storage. Including code via the
above snippets will pull in all of these features.

You can reduce the amount of code your app uses by just including the features
you need. The individually installable components are:

- `firebase-app` - The core `firebase` client (required).
- `firebase-auth` - Firebase Authentication (optional).
- `firebase-database` - The Firebase Realtime Database (optional).
- `firebase-storage` - Firebase Storage (optional).

From the CDN, include the individual components you need (include `firebase-app`
first):

```
<script src="https://www.gstatic.com/firebasejs/3.3.0/firebase-app.js"></script>
<script src="https://www.gstatic.com/firebasejs/3.3.0/firebase-auth.js"></script>
<script src="https://www.gstatic.com/firebasejs/3.3.0/firebase-database.js"></script>
<script src="https://www.gstatic.com/firebasejs/3.3.0/firebase-storage.js"></script>

<script>
  var app = firebase.initializeApp({ ... });
  // ...
</script>
```

When using this npm package, just `require()` the components that you use:

```
var firebase = require('firebase/app');
require('firebase/auth');
require('firebase/database');

var app = firebase.initializeApp({ ... });
// ...
```

## Get the code (Node.js - server and command line)

### NPM

While you can write entire Firebase applications without any backend code, many
developers want to write server applications or command-line utilities using the
Node.js JavaScript runtime.

You can use the same npm module to use Firebase in the Node.js runtime (on a
server or running from the command line):

```
$ npm init
$ npm install --save firebase
```

In your code, you can access Firebase using:

```
var firebase = require('firebase');
var app = firebase.initializeApp({ ... });
// ...
```

Firebase Storage is not included in the server side Firebase npm module.
Instead, you can use the
[`gcloud` Node.js client](https://googlecloudplatform.github.io/gcloud-node).

```
$ npm install --save gcloud
```

In your code, you can access your Storage bucket using:

```
var gcloud = require('gcloud')({ ... });
var gcs = gcloud.storage();
var bucket = gcs.bucket('<your-firebase-storage-bucket>');
...
```

## API definition

If you use the
[Closure Compiler](https://developers.google.com/closure/compiler/) or
compatable IDE, you can find API definitions for all the Firebase JavaScript API
in the included `/externs` directory in this package:

```
externs/
  firebase-app-externs.js
  firebase-auth-externs.js
  firebase-database-externs.js
  firebase-storage-externs.js
```

## Changelog

The Firebase changelog can be found at
[firebase.google.com](https://firebase.google.com/support/release-notes/js).
