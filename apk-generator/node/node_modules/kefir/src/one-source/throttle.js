import {createStream, createProperty} from '../patterns/one-source';
import now from '../utils/now';


const mixin = {

  _init({wait, leading, trailing}) {
    this._wait = Math.max(0, wait);
    this._leading = leading;
    this._trailing = trailing;
    this._trailingValue = null;
    this._timeoutId = null;
    this._endLater = false;
    this._lastCallTime = 0;
    this._$trailingCall = () => this._trailingCall();
  },

  _free() {
    this._trailingValue = null;
    this._$trailingCall = null;
  },

  _handleValue(x) {
    if (this._activating) {
      this._emitValue(x);
    } else {
      let curTime = now();
      if (this._lastCallTime === 0 && !this._leading) {
        this._lastCallTime = curTime;
      }
      let remaining = this._wait - (curTime - this._lastCallTime);
      if (remaining <= 0) {
        this._cancelTrailing();
        this._lastCallTime = curTime;
        this._emitValue(x);
      } else if (this._trailing) {
        this._cancelTrailing();
        this._trailingValue = x;
        this._timeoutId = setTimeout(this._$trailingCall, remaining);
      }
    }
  },

  _handleEnd() {
    if (this._activating) {
      this._emitEnd();
    } else {
      if (this._timeoutId) {
        this._endLater = true;
      } else {
        this._emitEnd();
      }
    }
  },

  _cancelTrailing() {
    if (this._timeoutId !== null) {
      clearTimeout(this._timeoutId);
      this._timeoutId = null;
    }
  },

  _trailingCall() {
    this._emitValue(this._trailingValue);
    this._timeoutId = null;
    this._trailingValue = null;
    this._lastCallTime = !this._leading ? 0 : now();
    if (this._endLater) {
      this._emitEnd();
    }
  }

};

const S = createStream('throttle', mixin);
const P = createProperty('throttle', mixin);


export default function throttle(obs, wait, {leading = true, trailing = true} = {}) {
  return new (obs._ofSameType(S, P))(obs, {wait, leading, trailing});
}
