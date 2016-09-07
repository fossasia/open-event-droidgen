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
    this._emitValue(fn(x));
  }

};

const S = createStream('map', mixin);
const P = createProperty('map', mixin);


const id = x => x;

export default function map(obs, fn = id) {
  return new (obs._ofSameType(S, P))(obs, {fn});
}
