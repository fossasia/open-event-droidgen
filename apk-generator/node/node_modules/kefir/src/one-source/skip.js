import {createStream, createProperty} from '../patterns/one-source';

const mixin = {

  _init({n}) {
    this._n = Math.max(0, n);
  },

  _handleValue(x) {
    if (this._n === 0) {
      this._emitValue(x);
    } else {
      this._n--;
    }
  }

};

const S = createStream('skip', mixin);
const P = createProperty('skip', mixin);


export default function skip(obs, n) {
  return new (obs._ofSameType(S, P))(obs, {n});
}
