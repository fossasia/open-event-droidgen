import {VALUE, ERROR, END} from '../constants';
import {inherit} from '../utils/objects';
import FlatMap from './flat-map';

function FlatMapErrors(source, fn) {
  FlatMap.call(this, source, fn);
}

inherit(FlatMapErrors, FlatMap, {

  // Same as in FlatMap, only VALUE/ERROR flipped
  _handleMain(event) {

    if (event.type === ERROR) {
      let sameCurr = this._activating && this._hadNoEvSinceDeact && this._lastCurrent === event.value;
      if (!sameCurr) {
        this._add(event.value, this._fn);
      }
      this._lastCurrent = event.value;
      this._hadNoEvSinceDeact = false;
    }

    if (event.type === VALUE) {
      this._emitValue(event.value);
    }

    if (event.type === END) {
      if (this._isEmpty()) {
        this._emitEnd();
      } else {
        this._mainEnded = true;
      }
    }

  }



});

export default FlatMapErrors;
