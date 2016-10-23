import Stream from '../stream';
import {VALUE, ERROR, NOTHING} from '../constants';
import {inherit} from '../utils/objects';
import {concat, fillArray} from '../utils/collections';
import {spread} from '../utils/functions';
import never from '../primary/never';



function defaultErrorsCombinator(errors) {
  let latestError;
  for (let i = 0; i < errors.length; i++) {
    if (errors[i] !== undefined) {
      if (latestError === undefined || latestError.index < errors[i].index) {
        latestError = errors[i];
      }
    }
  }
  return latestError.error;
}

function Combine(active, passive, combinator) {
  Stream.call(this);
  this._activeCount = active.length;
  this._sources = concat(active, passive);
  this._combinator = combinator ? spread(combinator, this._sources.length) : (x => x);
  this._aliveCount = 0;
  this._latestValues = new Array(this._sources.length);
  this._latestErrors = new Array(this._sources.length);
  fillArray(this._latestValues, NOTHING);
  this._emitAfterActivation = false;
  this._endAfterActivation = false;
  this._latestErrorIndex = 0;

  this._$handlers = [];
  for (let i = 0; i < this._sources.length; i++) {
    this._$handlers.push((event) => this._handleAny(i, event));
  }

}


inherit(Combine, Stream, {

  _name: 'combine',

  _onActivation() {
    this._aliveCount = this._activeCount;

    // we need to suscribe to _passive_ sources before _active_
    // (see https://github.com/rpominov/kefir/issues/98)
    for (let i = this._activeCount; i < this._sources.length; i++) {
      this._sources[i].onAny(this._$handlers[i]);
    }
    for (let i = 0; i < this._activeCount; i++) {
      this._sources[i].onAny(this._$handlers[i]);
    }

    if (this._emitAfterActivation) {
      this._emitAfterActivation = false;
      this._emitIfFull();
    }
    if (this._endAfterActivation) {
      this._emitEnd();
    }
  },

  _onDeactivation() {
    let length = this._sources.length,
        i;
    for (i = 0; i < length; i++) {
      this._sources[i].offAny(this._$handlers[i]);
    }
  },

  _emitIfFull() {
    let hasAllValues = true;
    let hasErrors = false;
    let length = this._latestValues.length;
    let valuesCopy = new Array(length);
    let errorsCopy = new Array(length);

    for (let i = 0; i < length; i++) {
      valuesCopy[i] = this._latestValues[i];
      errorsCopy[i] = this._latestErrors[i];

      if (valuesCopy[i] === NOTHING) {
        hasAllValues = false;
      }

      if (errorsCopy[i] !== undefined) {
        hasErrors = true;
      }
    }

    if (hasAllValues) {
      const combinator = this._combinator;
      this._emitValue(combinator(valuesCopy));
    }
    if (hasErrors) {
      this._emitError(defaultErrorsCombinator(errorsCopy));
    }
  },

  _handleAny(i, event) {

    if (event.type === VALUE || event.type === ERROR) {

      if (event.type === VALUE) {
        this._latestValues[i] = event.value;
        this._latestErrors[i] = undefined;
      }
      if (event.type === ERROR) {
        this._latestValues[i] = NOTHING;
        this._latestErrors[i] = {
          index: this._latestErrorIndex++,
          error: event.value
        };
      }

      if (i < this._activeCount) {
        if (this._activating) {
          this._emitAfterActivation = true;
        } else {
          this._emitIfFull();
        }
      }

    } else { // END

      if (i < this._activeCount) {
        this._aliveCount--;
        if (this._aliveCount === 0) {
          if (this._activating) {
            this._endAfterActivation = true;
          } else {
            this._emitEnd();
          }
        }
      }

    }
  },

  _clear() {
    Stream.prototype._clear.call(this);
    this._sources = null;
    this._latestValues = null;
    this._latestErrors = null;
    this._combinator = null;
    this._$handlers = null;
  }

});


export default function combine(active, passive = [], combinator) {
  if (typeof passive === 'function') {
    combinator = passive;
    passive = [];
  }
  return active.length === 0 ? never() : new Combine(active, passive, combinator);
}
