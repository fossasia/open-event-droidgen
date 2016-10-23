import {inherit} from '../utils/objects';
import Stream from '../stream';
import {END} from '../constants';



function S(generator) {
  Stream.call(this);
  this._generator = generator;
  this._source = null;
  this._inLoop = false;
  this._iteration = 0;
  this._$handleAny = (event) => this._handleAny(event);
}

inherit(S, Stream, {

  _name: 'repeat',

  _handleAny(event) {
    if (event.type === END) {
      this._source = null;
      this._getSource();
    } else {
      this._emit(event.type, event.value);
    }
  },

  _getSource() {
    if (!this._inLoop) {
      this._inLoop = true;
      const generator = this._generator;
      while (this._source === null && this._alive && this._active) {
        this._source = generator(this._iteration++);
        if (this._source) {
          this._source.onAny(this._$handleAny);
        } else {
          this._emitEnd();
        }
      }
      this._inLoop = false;
    }
  },

  _onActivation() {
    if (this._source) {
      this._source.onAny(this._$handleAny);
    } else {
      this._getSource();
    }
  },

  _onDeactivation() {
    if (this._source) {
      this._source.offAny(this._$handleAny);
    }
  },

  _clear() {
    Stream.prototype._clear.call(this);
    this._generator = null;
    this._source = null;
    this._$handleAny = null;
  }

});


export default function(generator) {
  return new S(generator);
}
