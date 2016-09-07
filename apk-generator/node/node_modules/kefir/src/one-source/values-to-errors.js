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
    let result = fn(x);
    if (result.convert) {
      this._emitError(result.error);
    } else {
      this._emitValue(x);
    }
  }

};

const S = createStream('valuesToErrors', mixin);
const P = createProperty('valuesToErrors', mixin);


const defFn = x => ({convert: true, error: x});

export default function valuesToErrors(obs, fn = defFn) {
  return new (obs._ofSameType(S, P))(obs, {fn});
}
