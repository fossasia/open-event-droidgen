import {createStream, createProperty} from '../patterns/one-source';
import now from '../utils/now';


const mixin = {

  _init({wait, immediate}) {
    this._wait = Math.max(0, wait);
    this._immediate = immediate;
    this._lastAttempt = 0;
    this._timeoutId = null;
    this._laterValue = null;
    this._endLater = false;
    this._$later = () => this._later();
  },

  _free() {
    this._laterValue = null;
    this._$later = null;
  },

  _handleValue(x) {
    if (this._activating) {
      this._emitValue(x);
    } else {
      this._lastAttempt = now();
      if (this._immediate && !this._timeoutId) {
        this._emitValue(x);
      }
      if (!this._timeoutId) {
        this._timeoutId = setTimeout(this._$later, this._wait);
      }
      if (!this._immediate) {
        this._laterValue = x;
      }
    }
  },

  _handleEnd() {
    if (this._activating) {
      this._emitEnd();
    } else {
      if (this._timeoutId && !this._immediate) {
        this._endLater = true;
      } else {
        this._emitEnd();
      }
    }
  },

  _later() {
    let last = now() - this._lastAttempt;
    if (last < this._wait && last >= 0) {
      this._timeoutId = setTimeout(this._$later, this._wait - last);
    } else {
      this._timeoutId = null;
      if (!this._immediate) {
        this._emitValue(this._laterValue);
        this._laterValue = null;
      }
      if (this._endLater) {
        this._emitEnd();
      }
    }
  }

};

const S = createStream('debounce', mixin);
const P = createProperty('debounce', mixin);

export default function debounce(obs, wait, {immediate = false} = {}) {
  return new (obs._ofSameType(S, P))(obs, {wait, immediate});
}

