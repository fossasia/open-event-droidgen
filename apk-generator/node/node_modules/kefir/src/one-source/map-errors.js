import {createStream, createProperty} from '../patterns/one-source';

const mixin = {

  _init({fn}) {
    this._fn = fn;
  },

  _free() {
    this._fn = null;
  },

  _handleError(x) {
    const fn = this._fn;
    this._emitError(fn(x));
  }

};

const S = createStream('mapErrors', mixin);
const P = createProperty('mapErrors', mixin);


const id = x => x;

export default function mapErrors(obs, fn = id) {
  return new (obs._ofSameType(S, P))(obs, {fn});
}
