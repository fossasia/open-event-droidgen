import {inherit} from '../utils/objects';
import Stream from '../stream';

export default function timeBased(mixin) {

  function AnonymousStream(wait, options) {
    Stream.call(this);
    this._wait = wait;
    this._intervalId = null;
    this._$onTick = () => this._onTick();
    this._init(options);
  }

  inherit(AnonymousStream, Stream, {

    _init() {},
    _free() {},

    _onTick() {},

    _onActivation() {
      this._intervalId = setInterval(this._$onTick, this._wait);
    },

    _onDeactivation() {
      if (this._intervalId !== null) {
        clearInterval(this._intervalId);
        this._intervalId = null;
      }
    },

    _clear() {
      Stream.prototype._clear.call(this);
      this._$onTick = null;
      this._free();
    }

  }, mixin);

  return AnonymousStream;
}
