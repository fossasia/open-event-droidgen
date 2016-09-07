import {createStream, createProperty} from '../patterns/one-source';

const mixin = {

  _init({count, flushOnEnd}) {
    this._count = count;
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
    if (this._buff.length >= this._count) {
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

const S = createStream('bufferWithCount', mixin);
const P = createProperty('bufferWithCount', mixin);

export default function bufferWhile(obs, count, {flushOnEnd = true} = {}) {
  return new (obs._ofSameType(S, P))(obs, {count: count, flushOnEnd});
}
