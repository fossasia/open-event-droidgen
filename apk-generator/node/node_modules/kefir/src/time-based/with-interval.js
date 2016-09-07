import timeBased from '../patterns/time-based';
import emitter from '../emitter';


const S = timeBased({

  _name: 'withInterval',

  _init({fn}) {
    this._fn = fn;
    this._emitter = emitter(this);
  },

  _free() {
    this._fn = null;
    this._emitter = null;
  },

  _onTick() {
    const fn = this._fn;
    fn(this._emitter);
  }

});


export default function withInterval(wait, fn) {
  return new S(wait, {fn});
}
