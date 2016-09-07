import {createProperty} from '../patterns/one-source';
import {ERROR, NOTHING} from '../constants';


const P = createProperty('scan', {

  _init({fn, seed}) {
    this._fn = fn;
    this._seed = seed;
    if (seed !== NOTHING) {
      this._emitValue(seed);
    }
  },

  _free() {
    this._fn = null;
    this._seed = null;
  },

  _handleValue(x) {
    const fn = this._fn;
    if (this._currentEvent === null || this._currentEvent.type === ERROR) {
      this._emitValue(this._seed === NOTHING ? x : fn(this._seed, x));
    } else {
      this._emitValue(fn(this._currentEvent.value, x));
    }
  }

});


export default function scan(obs, fn, seed = NOTHING) {
  return new P(obs, {fn, seed});
}
