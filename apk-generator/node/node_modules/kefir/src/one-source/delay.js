import {createStream, createProperty} from '../patterns/one-source';

const END_MARKER = {};

const mixin = {

  _init({wait}) {
    this._wait = Math.max(0, wait);
    this._buff = [];
    this._$shiftBuff = () => {
      const value = this._buff.shift();
      if (value === END_MARKER) {
        this._emitEnd();
      } else {
        this._emitValue(value);
      }
    };
  },

  _free() {
    this._buff = null;
    this._$shiftBuff = null;
  },

  _handleValue(x) {
    if (this._activating) {
      this._emitValue(x);
    } else {
      this._buff.push(x);
      setTimeout(this._$shiftBuff, this._wait);
    }
  },

  _handleEnd() {
    if (this._activating) {
      this._emitEnd();
    } else {
      this._buff.push(END_MARKER);
      setTimeout(this._$shiftBuff, this._wait);
    }
  }

};

const S = createStream('delay', mixin);
const P = createProperty('delay', mixin);

export default function delay(obs, wait) {
  return new (obs._ofSameType(S, P))(obs, {wait});
}
