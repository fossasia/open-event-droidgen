/**
 *  Firebase storage for browser npm package.
 *
 * Usage:
 *
 *   storage = require('firebase/storage');
 */
require('./firebase-app');
require('./firebase-storage');
module.exports = firebase.storage;
