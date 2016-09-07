import timeBased from '../patterns/time-based';
import {cloneArray} from '../utils/collections';
import never from '../primary/never';

const S = timeBased({

  _name: 'sequentially',

  _init({xs}) {
    this._xs = cloneArray(xs);
  },

  _free() {
    this._xs = null;
  },

  _onTick() {
    if (this._xs.length === 1) {
      this._emitValue(this._xs[0]);
      this._emitEnd();
    } else {
      this._emitValue(this._xs.shift());
    }
  }

});


export default function sequentially(wait, xs) {
  return xs.length === 0 ? never() : new S(wait, {xs});
}
