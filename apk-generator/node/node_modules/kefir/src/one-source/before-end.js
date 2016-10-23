import {createStream, createProperty} from '../patterns/one-source';

const mixin = {

  _init({fn}) {
    this._fn = fn;
  },

  _free() {
    this._fn = null;
  },

  _handleEnd() {
    const fn = this._fn;
    this._emitValue(fn());
    this._emitEnd();
  }

};

const S = createStream('beforeEnd', mixin);
const P = createProperty('beforeEnd', mixin);

export default function beforeEnd(obs, fn) {
  return new (obs._ofSameType(S, P))(obs, {fn});
}
