import {createStream, createProperty} from '../patterns/two-sources';
import {NOTHING} from '../constants';


const mixin = {

  _init({flushOnEnd = true, flushOnChange = false} = {}) {
    this._buff = [];
    this._flushOnEnd = flushOnEnd;
    this._flushOnChange = flushOnChange;
  },

  _free() {
    this._buff = null;
  },

  _flush() {
    if (this._buff !== null) {
      this._emitValue(this._buff);
      this._buff = [];
    }
  },

  _handlePrimaryEnd() {
    if (this._flushOnEnd) {
      this._flush();
    }
    this._emitEnd();
  },

  _handlePrimaryValue(x) {
    this._buff.push(x);
    if (this._lastSecondary !== NOTHING && !this._lastSecondary) {
      this._flush();
    }
  },

  _handleSecondaryEnd() {
    if (!this._flushOnEnd && (this._lastSecondary === NOTHING || this._lastSecondary)) {
      this._emitEnd();
    }
  },

  _handleSecondaryValue(x) {
    if (this._flushOnChange && !x) {
      this._flush();
    }

    // from default _handleSecondaryValue
    this._lastSecondary = x;
  }

};


const S = createStream('bufferWhileBy', mixin);
const P = createProperty('bufferWhileBy', mixin);


export default function bufferWhileBy(primary, secondary, options /* optional */) {
  return new (primary._ofSameType(S, P))(primary, secondary, options);
}
