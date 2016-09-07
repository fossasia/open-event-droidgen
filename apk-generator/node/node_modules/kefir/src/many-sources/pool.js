import {inherit} from '../utils/objects';
import AbstractPool from './abstract-pool';



function Pool() {
  AbstractPool.call(this);
}

inherit(Pool, AbstractPool, {

  _name: 'pool',

  plug(obs) {
    this._add(obs);
    return this;
  },

  unplug(obs) {
    this._remove(obs);
    return this;
  }

});

export default Pool;
