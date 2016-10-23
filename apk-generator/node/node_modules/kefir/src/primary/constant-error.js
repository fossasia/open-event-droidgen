import {inherit} from '../utils/objects';
import Property from '../property';


// HACK:
//   We don't call parent Class constructor, but instead putting all necessary
//   properties into prototype to simulate ended Property
//   (see Propperty and Observable classes).

function P(value) {
  this._currentEvent = {type: 'error', value, current: true};
}

inherit(P, Property, {
  _name: 'constantError',
  _active: false,
  _activating: false,
  _alive: false,
  _dispatcher: null,
  _logHandlers: null
});

export default function constantError(x) {
  return new P(x);
}
