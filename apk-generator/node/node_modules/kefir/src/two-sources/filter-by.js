import {createStream, createProperty} from '../patterns/two-sources';
import {NOTHING} from '../constants';


const mixin = {

  _handlePrimaryValue(x) {
    if (this._lastSecondary !== NOTHING && this._lastSecondary) {
      this._emitValue(x);
    }
  },

  _handleSecondaryEnd() {
    if (this._lastSecondary === NOTHING || !this._lastSecondary) {
      this._emitEnd();
    }
  }

};

const S = createStream('filterBy', mixin);
const P = createProperty('filterBy', mixin);


export default function filterBy(primary, secondary) {
  return new (primary._ofSameType(S, P))(primary, secondary);
}

