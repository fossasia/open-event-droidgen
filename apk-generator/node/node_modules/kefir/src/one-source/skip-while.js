import {createStream, createProperty} from '../patterns/one-source';

const mixin = {

  _init({fn}) {
    this._fn = fn;
  },

  _free() {
    this._fn = null;
  },

  _handleValue(x) {
    const fn = this._fn;
    if (this._fn !== null && !fn(x)) {
      this._fn = null;
    }
    if (this._fn === null) {
      this._emitValue(x);
    }
  }

};

const S = createStream('skipWhile', mixin);
const P = createProperty('skipWhile', mixin);


const id = x => x;

export default function skipWhile(obs, fn = id) {
  return new (obs._ofSameType(S, P))(obs, {fn});
}
