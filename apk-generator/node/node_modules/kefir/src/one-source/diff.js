import {createStream, createProperty} from '../patterns/one-source';
import {NOTHING} from '../constants';

const mixin = {

  _init({fn, seed}) {
    this._fn = fn;
    this._prev = seed;
  },

  _free() {
    this._prev = null;
    this._fn = null;
  },

  _handleValue(x) {
    if (this._prev !== NOTHING) {
      const fn = this._fn;
      this._emitValue(fn(this._prev, x));
    }
    this._prev = x;
  }

};

const S = createStream('diff', mixin);
const P = createProperty('diff', mixin);


function defaultFn(a, b) {
  return [a, b];
}

export default function diff(obs, fn, seed = NOTHING) {
  return new (obs._ofSameType(S, P))(obs, {fn: fn || defaultFn, seed});
}
