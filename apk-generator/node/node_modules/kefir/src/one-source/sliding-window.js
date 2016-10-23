import {createStream, createProperty} from '../patterns/one-source';
import {slide} from '../utils/collections';

const mixin = {

  _init({min, max}) {
    this._max = max;
    this._min = min;
    this._buff = [];
  },

  _free() {
    this._buff = null;
  },

  _handleValue(x) {
    this._buff = slide(this._buff, x, this._max);
    if (this._buff.length >= this._min) {
      this._emitValue(this._buff);
    }
  }

};

const S = createStream('slidingWindow', mixin);
const P = createProperty('slidingWindow', mixin);


export default function slidingWindow(obs, max, min = 0) {
  return new (obs._ofSameType(S, P))(obs, {min, max});
}
