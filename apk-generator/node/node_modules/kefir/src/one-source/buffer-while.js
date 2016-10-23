import {createStream, createProperty} from '../patterns/one-source';

const mixin = {

  _init({fn, flushOnEnd}) {
    this._fn = fn;
    this._flushOnEnd = flushOnEnd;
    this._buff = [];
  },

  _free() {
    this._buff = null;
  },

  _flush() {
    if (this._buff !== null && this._buff.length !== 0) {
      this._emitValue(this._buff);
      this._buff = [];
    }
  },

  _handleValue(x) {
    this._buff.push(x);
    const fn = this._fn;
    if (!fn(x)) {
      this._flush();
    }
  },

  _handleEnd() {
    if (this._flushOnEnd) {
      this._flush();
    }
    this._emitEnd();
  }

};

const S = createStream('bufferWhile', mixin);
const P = createProperty('bufferWhile', mixin);


const id = x => x;

export default function bufferWhile(obs, fn, {flushOnEnd = true} = {}) {
  return new (obs._ofSameType(S, P))(obs, {fn: fn || id, flushOnEnd});
}
