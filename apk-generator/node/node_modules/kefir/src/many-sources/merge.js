import {inherit} from '../utils/objects';
import AbstractPool from './abstract-pool';
import never from '../primary/never';


function Merge(sources) {
  AbstractPool.call(this);
  this._addAll(sources);
  this._initialised = true;
}

inherit(Merge, AbstractPool, {

  _name: 'merge',

  _onEmpty() {
    if (this._initialised) {
      this._emitEnd();
    }
  }

});

export default function merge(observables) {
  return observables.length === 0 ? never() : new Merge(observables);
}
