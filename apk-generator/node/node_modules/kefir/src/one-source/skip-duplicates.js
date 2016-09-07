import {createStream, createProperty} from '../patterns/one-source';
import {NOTHING} from '../constants';

const mixin = {

  _init({fn}) {
    this._fn = fn;
    this._prev = NOTHING;
  },

  _free() {
    this._fn = null;
    this._prev = null;
  },

  _handleValue(x) {
    const fn = this._fn;
    if (this._prev === NOTHING || !fn(this._prev, x)) {
      this._prev = x;
      this._emitValue(x);
    }
  }

};

const S = createStream('skipDuplicates', mixin);
const P = createProperty('skipDuplicates', mixin);


const eq = (a, b) => a === b;

export default function skipDuplicates(obs, fn = eq) {
  return new (obs._ofSameType(S, P))(obs, {fn});
}
