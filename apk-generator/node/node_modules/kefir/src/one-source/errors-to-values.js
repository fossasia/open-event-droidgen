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
    const result = fn(x);
    if (result.convert) {
      this._emitValue(result.value);
    } else {
      this._emitError(x);
    }
  }

};

const S = createStream('errorsToValues', mixin);
const P = createProperty('errorsToValues', mixin);


const defFn = x => ({convert: true, value: x});

export default function errorsToValues(obs, fn = defFn) {
  return new (obs._ofSameType(S, P))(obs, {fn});
}
