import {createStream, createProperty} from '../patterns/two-sources';
import {NOTHING} from '../constants';


const mixin = {

  _handlePrimaryValue(x) {
    if (this._lastSecondary !== NOTHING) {
      this._emitValue(x);
    }
  },

  _handleSecondaryEnd() {
    if (this._lastSecondary === NOTHING) {
      this._emitEnd();
    }
  }

};

const S = createStream('skipUntilBy', mixin);
const P = createProperty('skipUntilBy', mixin);


export default function skipUntilBy(primary, secondary) {
  return new (primary._ofSameType(S, P))(primary, secondary);
}

