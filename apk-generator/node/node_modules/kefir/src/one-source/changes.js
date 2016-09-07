import {createStream} from '../patterns/one-source';



const S = createStream('changes', {

  _handleValue(x) {
    if (!this._activating) {
      this._emitValue(x);
    }
  },

  _handleError(x) {
    if (!this._activating) {
      this._emitError(x);
    }
  }

});


export default function changes(obs) {
  return new S(obs);
}
