import Stream from '../stream';
import Property from '../property';
import {inherit} from '../utils/objects';
import {VALUE, ERROR, END, NOTHING} from '../constants';





function createConstructor(BaseClass, name) {
  return function AnonymousObservable(primary, secondary, options) {
    BaseClass.call(this);
    this._primary = primary;
    this._secondary = secondary;
    this._name = `${primary._name}.${name}`;
    this._lastSecondary = NOTHING;
    this._$handleSecondaryAny = (event) => this._handleSecondaryAny(event);
    this._$handlePrimaryAny = (event) => this._handlePrimaryAny(event);
    this._init(options);
  }
}

function createClassMethods(BaseClass) {
  return {
    _init() {},
    _free() {},

    _handlePrimaryValue(x) {
      this._emitValue(x);
    },
    _handlePrimaryError(x) {
      this._emitError(x);
    },
    _handlePrimaryEnd() {
      this._emitEnd();
    },

    _handleSecondaryValue(x) {
      this._lastSecondary = x;
    },
    _handleSecondaryError(x) {
      this._emitError(x);
    },
    _handleSecondaryEnd() {},

    _handlePrimaryAny(event) {
      switch (event.type) {
        case VALUE: return this._handlePrimaryValue(event.value);
        case ERROR: return this._handlePrimaryError(event.value);
        case END: return this._handlePrimaryEnd(event.value);
      }
    },
    _handleSecondaryAny(event) {
      switch (event.type) {
        case VALUE: return this._handleSecondaryValue(event.value);
        case ERROR: return this._handleSecondaryError(event.value);
        case END:
          this._handleSecondaryEnd(event.value);
          this._removeSecondary();
      }
    },

    _removeSecondary() {
      if (this._secondary !== null) {
        this._secondary.offAny(this._$handleSecondaryAny);
        this._$handleSecondaryAny = null;
        this._secondary = null;
      }
    },

    _onActivation() {
      if (this._secondary !== null) {
        this._secondary.onAny(this._$handleSecondaryAny);
      }
      if (this._active) {
        this._primary.onAny(this._$handlePrimaryAny);
      }
    },
    _onDeactivation() {
      if (this._secondary !== null) {
        this._secondary.offAny(this._$handleSecondaryAny);
      }
      this._primary.offAny(this._$handlePrimaryAny);
    },

    _clear() {
      BaseClass.prototype._clear.call(this);
      this._primary = null;
      this._secondary = null;
      this._lastSecondary = null;
      this._$handleSecondaryAny = null;
      this._$handlePrimaryAny = null;
      this._free();
    }

  };
}



function createStream(name, mixin) {
  const S = createConstructor(Stream, name);
  inherit(S, Stream, createClassMethods(Stream), mixin);
  return S;
}


function createProperty(name, mixin) {
  const P = createConstructor(Property, name);
  inherit(P, Property, createClassMethods(Property), mixin);
  return P;
}


export {createStream, createProperty};

