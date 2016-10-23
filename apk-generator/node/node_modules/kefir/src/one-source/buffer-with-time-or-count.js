import {createStream, createProperty} from '../patterns/one-source';

const mixin = {

  _init({wait, count, flushOnEnd}) {
    this._wait = wait;
    this._count = count;
    this._flushOnEnd = flushOnEnd;
    this._intervalId = null;
    this._$onTick = () => this._flush();
    this._buff = [];
  },

  _free() {
    this._$onTick = null;
    this._buff = null;
  },

  _flush() {
    if (this._buff !== null) {
      this._emitValue(this._buff);
      this._buff = [];
    }
  },

  _handleValue(x) {
    this._buff.push(x);
    if (this._buff.length >= this._count) {
      clearInterval(this._intervalId);
      this._flush();
      this._intervalId = setInterval(this._$onTick, this._wait);
    }
  },

  _handleEnd() {
    if (this._flushOnEnd && this._buff.length !== 0) {
      this._flush();
    }
    this._emitEnd();
  },

  _onActivation() {
    this._intervalId = setInterval(this._$onTick, this._wait);
    this._source.onAny(this._$handleAny); // copied from patterns/one-source
  },

  _onDeactivation() {
    if (this._intervalId !== null) {
      clearInterval(this._intervalId);
      this._intervalId = null;
    }
    this._source.offAny(this._$handleAny); // copied from patterns/one-source
  }

};


const S = createStream('bufferWithTimeOrCount', mixin);
const P = createProperty('bufferWithTimeOrCount', mixin);

export default function bufferWithTimeOrCount(obs, wait, count, {flushOnEnd = true} = {}) {
  return new (obs._ofSameType(S, P))(obs, {wait, count, flushOnEnd});
}
