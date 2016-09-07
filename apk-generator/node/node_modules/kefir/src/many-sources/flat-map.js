import {VALUE, ERROR, END} from '../constants';
import {inherit} from '../utils/objects';
import AbstractPool from './abstract-pool';




function FlatMap(source, fn, options) {
  AbstractPool.call(this, options);
  this._source = source;
  this._fn = fn;
  this._mainEnded = false;
  this._lastCurrent = null;
  this._$handleMain = (event) => this._handleMain(event);
}

inherit(FlatMap, AbstractPool, {

  _onActivation() {
    AbstractPool.prototype._onActivation.call(this);
    if (this._active) {
      this._source.onAny(this._$handleMain);
    }
  },

  _onDeactivation() {
    AbstractPool.prototype._onDeactivation.call(this);
    this._source.offAny(this._$handleMain);
    this._hadNoEvSinceDeact = true;
  },

  _handleMain(event) {

    if (event.type === VALUE) {
      // Is latest value before deactivation survived, and now is 'current' on this activation?
      // We don't want to handle such values, to prevent to constantly add
      // same observale on each activation/deactivation when our main source
      // is a `Kefir.conatant()` for example.
      let sameCurr = this._activating && this._hadNoEvSinceDeact && this._lastCurrent === event.value;
      if (!sameCurr) {
        this._add(event.value, this._fn);
      }
      this._lastCurrent = event.value;
      this._hadNoEvSinceDeact = false;
    }

    if (event.type === ERROR) {
      this._emitError(event.value);
    }

    if (event.type === END) {
      if (this._isEmpty()) {
        this._emitEnd();
      } else {
        this._mainEnded = true;
      }
    }

  },

  _onEmpty() {
    if (this._mainEnded) {
      this._emitEnd();
    }
  },

  _clear() {
    AbstractPool.prototype._clear.call(this);
    this._source = null;
    this._lastCurrent = null;
    this._$handleMain = null;
  }

});




export default FlatMap;
