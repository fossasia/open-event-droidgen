/**
 *  Firebase libraries for React Native.
 *
 * Usage:
 *
 *   firebase = require('firebase');
 */

var firebase = require('./app');
require('./database');
require('./auth');
require('./storage');
var AsyncStorage = require('react-native').AsyncStorage;
firebase.INTERNAL.extendNamespace({
 'INTERNAL': {
   'reactNative': {
     'AsyncStorage': AsyncStorage
   }
 }
});
module.exports = firebase;
