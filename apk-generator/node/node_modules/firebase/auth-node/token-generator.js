/*! @license Firebase v3.3.0
    Build: 3.3.0-rc.7
    Terms: https://developers.google.com/terms */
'use strict';

var fs = require('fs');
var https = require('https');
var jwt = require('jsonwebtoken');
var firebase = require('../app-node');


var ALGORITHM = 'RS256';
var ONE_HOUR_IN_SECONDS = 60 * 60;

// List of blacklisted claims which cannot be provided when creating a custom token
var BLACKLISTED_CLAIMS = [
  'acr', 'amr', 'at_hash', 'aud', 'auth_time', 'azp', 'cnf', 'c_hash', 'exp', 'iat', 'iss', 'jti',
  'nbf', 'nonce'
];

// URL containing the public keys for the Google certs (whose private keys are used to sign Firebase
// Auth ID tokens)
var CLIENT_CERT_URL = 'https://www.googleapis.com/robot/v1/metadata/x509/securetoken@system.gserviceaccount.com';

// Audience to use for Firebase Auth Custom tokens
var FIREBASE_AUDIENCE = 'https://identitytoolkit.googleapis.com/google.identity.identitytoolkit.v1.IdentityToolkit';


/**
 * Class for generating and verifying different types of Firebase Auth tokens (JWTs).
 *
 * @constructor
 * @param {string|Object} serviceAccount Either the path to a service account key or the service account key itself.
 */
var FirebaseTokenGenerator = function(serviceAccount) {
  serviceAccount = serviceAccount || process.env.GOOGLE_APPLICATION_CREDENTIALS;
  if (typeof serviceAccount === 'string') {
    try {
      this.serviceAccount = JSON.parse(fs.readFileSync(serviceAccount, 'utf8'));
    } catch (error) {
      throw new Error('Failed to parse service account key file: ' + error);
    }
  } else if (typeof serviceAccount === 'object' && serviceAccount !== null) {
    this.serviceAccount = serviceAccount;
  } else {
    throw new Error('Must provide a service account to use FirebaseTokenGenerator');
  }

  if (typeof this.serviceAccount.private_key !== 'string' || this.serviceAccount.private_key === '') {
    throw new Error('Service account key must contain a string "private_key" field');
  } else if (typeof this.serviceAccount.client_email !== 'string' || this.serviceAccount.client_email === '') {
    throw new Error('Service account key must contain a string "client_email" field');
  }
};


/**
 * Creates a new Firebase Auth Custom token.
 *
 * @param {string} uid The user ID to use for the generated Firebase Auth Custom token.
 * @param {Object} [developerClaims] Optional developer claims to include in the generated Firebase
 *                 Auth Custom token.
 * @return {string} A Firebase Auth Custom token signed with a service account key and containing
 *                  the provided payload.
 */
FirebaseTokenGenerator.prototype.createCustomToken = function(uid, developerClaims) {
  if (typeof uid !== 'string' || uid === '') {
    throw new Error('First argument to createCustomToken() must be a non-empty string uid');
  } else if (uid.length > 128) {
    throw new Error('First argument to createCustomToken() must a uid with less than or equal to 128 characters');
  } else if (typeof developerClaims !== 'undefined' && (typeof developerClaims !== 'object' || developerClaims === null || developerClaims instanceof Array)) {
    throw new Error('Optional second argument to createCustomToken() must be an object containing the developer claims');
  }

  var jwtPayload = {};

  if (typeof developerClaims !== 'undefined') {
    jwtPayload.claims = {};

    for (var key in developerClaims) {
      /* istanbul ignore else */
      if (developerClaims.hasOwnProperty(key)) {
        if (BLACKLISTED_CLAIMS.indexOf(key) !== -1) {
          throw new Error('Developer claim "' + key + '" is reserved and cannot be specified');
        }

        jwtPayload.claims[key] = developerClaims[key];
      }
    }
  }
  jwtPayload.uid = uid;

  return jwt.sign(jwtPayload, this.serviceAccount.private_key, {
    audience: FIREBASE_AUDIENCE,
    expiresIn: ONE_HOUR_IN_SECONDS,
    issuer: this.serviceAccount.client_email,
    subject: this.serviceAccount.client_email,
    algorithm: ALGORITHM
  });
};


/**
 * Verifies the format and signature of a Firebase Auth ID token.
 *
 * @param {string} idToken The Firebase Auth ID token to verify.
 * @return {Promise<Object>} A promise fulfilled with the decoded claims of the Firebase Auth ID
 *                           token.
 */
FirebaseTokenGenerator.prototype.verifyIdToken = function(idToken) {
  if (typeof idToken !== 'string') {
    throw new Error('First argument to verifyIdToken() must be a Firebase ID token');
  }

  if (typeof this.serviceAccount.project_id !== 'string' || this.serviceAccount.project_id === '') {
    throw new Error('verifyIdToken() requires a service account with "project_id" set');
  }

  var fullDecodedToken = jwt.decode(idToken, {
    complete: true
  });

  var header = fullDecodedToken && fullDecodedToken.header;
  var payload = fullDecodedToken && fullDecodedToken.payload;

  var projectIdMatchMessage = ' Make sure the ID token comes from the same Firebase project as the ' +
    'service account used to authenticate this SDK.';
  var verifyIdTokenDocsMessage = ' See https://firebase.google.com/docs/auth/server/verify-id-tokens ' +
    'for details on how to retrieve an ID token.';

  var errorMessage;
  if (!fullDecodedToken) {
    errorMessage = 'Decoding Firebase ID token failed. Make sure you passed the entire string JWT ' +
      'which represents an ID token.' + verifyIdTokenDocsMessage;
  } else if (typeof header.kid === 'undefined') {
    var isCustomToken = (payload.aud === FIREBASE_AUDIENCE);
    var isLegacyCustomToken = (header.alg === 'HS256' && payload.v === 0 && 'd' in payload && 'uid' in payload.d);

    if (isCustomToken) {
      errorMessage = 'verifyIdToken() expects an ID token, but was given a custom token.';
    } else if (isLegacyCustomToken) {
      errorMessage = 'verifyIdToken() expects an ID token, but was given a legacy custom token.';
    } else {
      errorMessage = 'Firebase ID token has no "kid" claim.';
    }

    errorMessage += verifyIdTokenDocsMessage;
  } else if (header.alg !== ALGORITHM) {
    errorMessage = 'Firebase ID token has incorrect algorithm. Expected "' + ALGORITHM + '" but got ' +
      '"' + header.alg + '".' + verifyIdTokenDocsMessage;
  } else if (payload.aud !== this.serviceAccount.project_id) {
    errorMessage = 'Firebase ID token has incorrect "aud" (audience) claim. Expected "' + this.serviceAccount.project_id +
      '" but got "' + payload.aud + '".' + projectIdMatchMessage + verifyIdTokenDocsMessage;
  } else if (payload.iss !== 'https://securetoken.google.com/' + this.serviceAccount.project_id) {
    errorMessage = 'Firebase ID token has incorrect "iss" (issuer) claim. Expected "https://securetoken.google.com/' +
      this.serviceAccount.project_id + '" but got "' + payload.iss + '".' + projectIdMatchMessage + verifyIdTokenDocsMessage;
  } else if (typeof payload.sub !== 'string') {
    errorMessage = 'Firebase ID token has no "sub" (subject) claim.' + verifyIdTokenDocsMessage;
  } else if (payload.sub === '') {
    errorMessage = 'Firebase ID token has an empty string "sub" (subject) claim.' + verifyIdTokenDocsMessage;
  } else if (payload.sub.length > 128) {
    errorMessage = 'Firebase ID token has "sub" (subject) claim longer than 128 characters.' + verifyIdTokenDocsMessage;
  }

  if (typeof errorMessage !== 'undefined') {
    return firebase.Promise.reject(new Error(errorMessage));
  }

  return this._fetchPublicKeys().then(function(publicKeys) {
    if (!publicKeys.hasOwnProperty(header.kid)) {
      return firebase.Promise.reject('Firebase ID token has "kid" claim which does not correspond to ' +
        'a known public key. Most likely the ID token is expired, so get a fresh token from your client ' +
        'app and try again.' + verifyIdTokenDocsMessage);
    }

    return new firebase.Promise(function(resolve, reject) {
      jwt.verify(idToken, publicKeys[header.kid], {
        algorithms: [ALGORITHM]
      }, function(error, decodedToken) {
        if (error) {
          if (error.name === 'TokenExpiredError') {
            error = 'Firebase ID token has expired. Get a fresh token from your client app and try ' +
              'again.' + verifyIdTokenDocsMessage;
          } else if (error.name === 'JsonWebTokenError') {
            error = 'Firebase ID token has invalid signature.' + verifyIdTokenDocsMessage;
          }
          reject(error);
        } else {
          decodedToken.uid = decodedToken.sub;
          resolve(decodedToken);
        }
      });
    });
  });
};


/**
 * Fetches the public keys for the Google certs.
 *
 * @return {Promise<Object>} A promise fulfilled with public keys for the Google certs.
 */
FirebaseTokenGenerator.prototype._fetchPublicKeys = function() {
  if (typeof this._publicKeys !== 'undefined' && typeof this._publicKeysExpireAt !== 'undefined' && Date.now() < this._publicKeysExpireAt) {
    return firebase.Promise.resolve(this._publicKeys);
  }

  var self = this;

  return new firebase.Promise(function(resolve, reject) {
    https.get(CLIENT_CERT_URL, function(res) {
      var buffers = [];

      res.on('data', function(buffer) {
        buffers.push(buffer);
      });

      res.on('end', function() {
        try {
          var response = JSON.parse(Buffer.concat(buffers));

          if (response.error) {
            var errorMessage = 'Error fetching public keys for Google certs: ' + response.error;
            /* istanbul ignore else */
            if (response.error_description) {
              errorMessage += ' (' + response.error_description + ')';
            }
            reject(new Error(errorMessage));
          } else {
            /* istanbul ignore else */
            if (res.headers.hasOwnProperty('cache-control')) {
              var cacheControlHeader = res.headers['cache-control'];
              var parts = cacheControlHeader.split(',');
              parts.forEach(function(part) {
                var subParts = part.trim().split('=');
                if (subParts[0] === 'max-age') {
                  var maxAge = subParts[1];
                  self._publicKeysExpireAt = Date.now() + (maxAge * 1000);
                }
              });
            }

            self._publicKeys = response;
            resolve(response);
          }
        } catch (e) {
          /* istanbul ignore next */
          reject(e);
        }
      });
    }).on('error', reject);
  });
};


module.exports = FirebaseTokenGenerator;

