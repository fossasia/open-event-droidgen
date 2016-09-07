import timeBased from '../patterns/time-based';

const S = timeBased({

  _name: 'later',

  _init({x}) {
    this._x = x;
  },

  _free() {
    this._x = null;
  },

  _onTick() {
    this._emitValue(this._x);
    this._emitEnd();
  }

});


export default function later(wait, x) {
  return new S(wait, {x});
}
