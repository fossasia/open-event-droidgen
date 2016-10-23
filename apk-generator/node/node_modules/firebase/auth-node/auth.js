/*! @license Firebase v3.3.0
    Build: 3.3.0-rc.7
    Terms: https://developers.google.com/terms */
'use strict';

// TODO(dimond): This can be an npm package include eventually
var FirebaseTokenGenerator = require('./token-generator');
var fs = require('fs');
var firebase = require('../app-node');
var credential = require('./credential.js');

/**
 * Gets a service account from app options.
 *
 * @return {{project_id: String, private_key: String, client_email: String}}
 */
function getServiceAccount(app_) {
  // We must be careful because '' is falsy. An opt || env test would coalesce '' || undefiend as undefined.
  var serviceAccountPathOrObject = typeof app_.options.serviceAccount === 'undefined' ?
    process.env.GOOGLE_APPLICATION_CREDENTIALS :
    app_.options.serviceAccount;
  var serviceAccount;
  if (typeof serviceAccountPathOrObject === 'undefined') {
    return null;
  } else if (typeof serviceAccountPathOrObject === 'string') {
    try {
      serviceAccount = JSON.parse(fs.readFileSync(serviceAccountPathOrObject, 'utf8'));
    } catch (error) {
      throw new Error('Failed to parse service account key file: ' + error);
    }
  } else if (typeof serviceAccountPathOrObject === 'object') {
    // Allow both camel- and underscore-cased keys for the service account object
    serviceAccount = {};

    var projectId = serviceAccountPathOrObject.project_id || serviceAccountPathOrObject.projectId;
    if (typeof projectId !== 'undefined') {
      serviceAccount.project_id = projectId;
    }

    var privateKey = serviceAccountPathOrObject.private_key || serviceAccountPathOrObject.privateKey;
    if (typeof privateKey !== 'undefined') {
      serviceAccount.private_key = privateKey;
    }

    var clientEmail = serviceAccountPathOrObject.client_email || serviceAccountPathOrObject.clientEmail;
    if (typeof clientEmail !== 'undefined') {
      serviceAccount.client_email = clientEmail;
    }
  } else {
    throw new Error('Invalid service account provided');
  }

  if (typeof serviceAccount.private_key !== 'string' || !serviceAccount.private_key) {
    throw new Error('Service account must contain a "private_key" field');
  } else if (typeof serviceAccount.client_email !== 'string' || !serviceAccount.client_email) {
    throw new Error('Service account must contain a "client_email" field');
  }

  return serviceAccount;
}

/**
 * Server auth service bound to the provided app.
 *
 * @param {Object} app The app for this auth service
 * @constructor
 */
var Auth = function(app_) {
  if (!('options' in app_)) {
    throw new Error('First parameter to Auth constructor must be an instance of firebase.App');
  }

  var cachedToken_ = null;
  var tokenListeners_ = [];

  var credential_ = app_.options.credential;
  var serviceAccount_ = getServiceAccount(app_);
  var tokenGenerator_;

  if (credential_ && typeof credential_.getAccessToken !== 'function') {
    throw new Error('Called firebase.initializeApp with an invalid credential parameter');
  }
  if (serviceAccount_) {
    credential_ = credential_ || new credential.CertCredential(serviceAccount_);
    tokenGenerator_ = new FirebaseTokenGenerator(serviceAccount_);
  } else {
    credential_ = credential_ || new credential.UnauthenticatedCredential();
  }

  /**
   * Defines the app property with a getter but no setter.
   */
  Object.defineProperty(this, 'app', {
    get: function() { return app_; }
  });

  /**
   * Creates a new custom token that can be sent back to a client to use with
   * signInWithCustomToken.
   *
   * @param {string} uid The uid to use as the subject
   * @param {Object=} developerClaims Optional additional claims to include
   *                                  in the payload of the JWT
   *
   * @return {string} The JWT for the provided payload.
   */
  this.createCustomToken = function(uid, developerClaims) {
    if (typeof tokenGenerator_ === 'undefined') {
      throw new Error('Must initialize FirebaseApp with a service account to call auth().createCustomToken()');
    }
    return tokenGenerator_.createCustomToken(uid, developerClaims);
  };

  /**
   * Verifies a JWT auth token. Returns a Promise with the tokens claims. Rejects
   * the promise if the token could not be verified.
   *
   * @param {string} idToken The JWT to verify
   * @return {Object} The Promise that will be fulfilled after a successful
   *                  verification.
   */
  this.verifyIdToken = function(idToken) {
    if (typeof tokenGenerator_ === 'undefined') {
      throw new Error('Must initialize FirebaseApp with a service account to call auth().verifyIdToken()');
    }
    return tokenGenerator_.verifyIdToken(idToken);
  };

  this.INTERNAL = {};

  /**
   * Deletes the service and it's associated resources
   */
  this.INTERNAL.delete = function() {
    // There are no resources to clean up
    return firebase.Promise.resolve();
  };

  /**
   * Internal method: Gets an auth token for the associated app.
   * @param {boolean} forceRefresh Forces a token refresh
   * @return {Object} The Promise that will be fulfilled with the current or new
   *                  token
   */
  this.INTERNAL.getToken = function(forceRefresh) {
    var expired = cachedToken_ && cachedToken_.expirationTime < Date.now();
    if (cachedToken_ && !forceRefresh && !expired) {
      return firebase.Promise.resolve(cachedToken_);
    } else {
      // credential_ may be an external class; resolving it in a promise helps us
      // protect against exceptions and upgrades the result to a promise in all cases.
      return firebase.Promise.resolve().then(function() {
        return credential_.getAccessToken();
      }).then(function(result) {
        if (result === null) {
          return null;
        }
        if (typeof result !== 'object' ||
            typeof result.expires_in !== 'number' ||
            typeof result.access_token !== 'string') {
          throw new Error('firebase.initializeApp was called with a credential ' +
              'that creates invalid access tokens: ' + JSON.stringify(result));
        }
        var token = {
          accessToken: result.access_token,
          expirationTime: Date.now() + (result.expires_in * 1000)
        };

        var hasAccessTokenChanged = (cachedToken_ && cachedToken_.accessToken !== token.accessToken);
        var hasExpirationTimeChanged = (cachedToken_ && cachedToken_.expirationTime !== token.expirationTime);
        if (!cachedToken_ || hasAccessTokenChanged || hasExpirationTimeChanged) {
          cachedToken_ = token;
          tokenListeners_.forEach(function(listener) {
            listener(token.accessToken);
          });
        }

        return token;
      });
    }
  };

  /**
   * Internal method: Adds a listener that is called each time a token changes.
   * @param {function(string)} listener The listener that will be called with
   *                                    each new token
   */
  this.INTERNAL.addAuthTokenListener = function(listener) {
    tokenListeners_.push(listener);
    if (cachedToken_) {
      listener(cachedToken_);
    }
  };

  /**
   * Internal method: Removes a token listener.
   * @param {function(string)} listener The listener to remove.
   */
  this.INTERNAL.removeAuthTokenListener = function(listener) {
    tokenListeners_ = tokenListeners_.filter(function(other) {
      return other !== listener;
    });
  };
};

module.exports = Auth;

