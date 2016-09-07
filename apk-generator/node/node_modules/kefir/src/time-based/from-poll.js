import timeBased from '../patterns/time-based';

const S = timeBased({

  _name: 'fromPoll',

  _init({fn}) {
    this._fn = fn;
  },

  _free() {
    this._fn = null;
  },

  _onTick() {
    const fn = this._fn;
    this._emitValue(fn());
  }

});


export default function fromPoll(wait, fn) {
  return new S(wait, {fn});
}
