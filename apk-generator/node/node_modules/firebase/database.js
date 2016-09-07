/**
 *  Firebase database for browser npm package.
 *
 * Usage:
 *
 *   database = require('firebase/database');
 */
require('./firebase-app');
require('./firebase-database');
module.exports = firebase.database;
