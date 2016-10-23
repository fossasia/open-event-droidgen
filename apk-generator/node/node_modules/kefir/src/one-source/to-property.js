import {createProperty} from '../patterns/one-source';


const P = createProperty('toProperty', {

  _init({fn}) {
    this._getInitialCurrent = fn;
  },

  _onActivation() {
    if (this._getInitialCurrent !== null) {
      const getInitial = this._getInitialCurrent;
      this._emitValue(getInitial());
    }
    this._source.onAny(this._$handleAny);  // copied from patterns/one-source
  }

});



export default function toProperty(obs, fn = null) {
  if (fn !== null && typeof fn !== 'function') {
    throw new Error('You should call toProperty() with a function or no arguments.');
  }
  return new P(obs, {fn});
}
