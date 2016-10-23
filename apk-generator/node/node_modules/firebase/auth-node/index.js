/*! @license Firebase v3.3.0
    Build: 3.3.0-rc.7
    Terms: https://developers.google.com/terms */
'use strict';

var Auth = require('./auth.js');
var firebase = require('../app-node');

/**
 * Factory function that creates a new auth service.
 * @param {Object} app The app for this service
 * @param {function(Object)} extendApp An extend function to extend the app
 *                                     namespace
 * @return {Auth} The auth service for the specified app.
 */
var serviceFactory = function(app, extendApp) {
  var auth = new Auth(app);
  extendApp({
    'INTERNAL': {
      'getToken': auth.INTERNAL.getToken.bind(auth),
      'addAuthTokenListener': auth.INTERNAL.addAuthTokenListener.bind(auth),
      'removeAuthTokenListener': auth.INTERNAL.removeAuthTokenListener.bind(auth)
    }
  });
  return auth;
};

/**
 * Create a hook to initialize auth so auth listeners and getToken
 * functions are available to other services immediately.
 * @param {string} event
 * @param {Object} app
 */
function appHook(event, app) {
  if (event === 'create') {
    app.auth();
  }
}

module.exports = firebase.INTERNAL.registerService(
  'serverAuth',
  serviceFactory,
  {'Auth': Auth},
  appHook);
