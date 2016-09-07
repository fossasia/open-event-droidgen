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
    if (fn(x)) {
      this._emitError(x);
    }
  }

};

const S = createStream('filterErrors', mixin);
const P = createProperty('filterErrors', mixin);


const id = x => x;

export default function filterErrors(obs, fn = id) {
  return new (obs._ofSameType(S, P))(obs, {fn});
}
