import {createStream} from '../patterns/one-source';

const mixin = {

  _init({fn}) {
    this._fn = fn;
  },

  _free() {
    this._fn = null;
  },

  _handleValue(x) {
    const fn = this._fn;
    const xs = fn(x);
    for (let i = 0; i < xs.length; i++) {
      this._emitValue(xs[i]);
    }
  }

};

const S = createStream('flatten', mixin);


const id = x => x;

export default function flatten(obs, fn = id) {
  return new S(obs, {fn});
}
