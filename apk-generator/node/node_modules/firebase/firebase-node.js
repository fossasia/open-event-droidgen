/**
 *  Firebase libraries for Node.js.
 *
 * Usage:
 *
 *   firebase = require('firebase');
 */
var firebase = require('./app-node');
var Storage = require('dom-storage');
var XMLHttpRequest = require("xmlhttprequest").XMLHttpRequest;

firebase.INTERNAL.extendNamespace({
  'INTERNAL': {
    'node': {
      'localStorage': new Storage(null, { strict: true }),
      'sessionStorage': new Storage(null, { strict: true }),
      'XMLHttpRequest': XMLHttpRequest
    }
  }
});

require('./auth-node');
require('./database-node');
module.exports = firebase;
