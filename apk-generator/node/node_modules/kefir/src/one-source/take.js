import {createStream, createProperty} from '../patterns/one-source';

const mixin = {

  _init({n}) {
    this._n = n;
    if (n <= 0) {
      this._emitEnd();
    }
  },

  _handleValue(x) {
    this._n--;
    this._emitValue(x);
    if (this._n === 0) {
      this._emitEnd();
    }
  }

};

const S = createStream('take', mixin);
const P = createProperty('take', mixin);


export default function take(obs, n) {
  return new (obs._ofSameType(S, P))(obs, {n});
}
