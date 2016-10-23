import Stream from '../stream';
import {VALUE, ERROR} from '../constants';
import {inherit} from '../utils/objects';
import {concat, forEach, findByPred, find, remove, cloneArray} from '../utils/collections';

const id = (x => x);

function AbstractPool({queueLim = 0, concurLim = -1, drop = 'new'} = {}) {
  Stream.call(this);

  this._queueLim = queueLim < 0 ? -1 : queueLim;
  this._concurLim = concurLim < 0 ? -1 : concurLim;
  this._drop = drop;
  this._queue = [];
  this._curSources = [];
  this._$handleSubAny = (event) => this._handleSubAny(event);
  this._$endHandlers = [];
  this._currentlyAdding = null;

  if (this._concurLim === 0) {
    this._emitEnd();
  }
}

inherit(AbstractPool, Stream, {

  _name: 'abstractPool',

  _add(obj, toObs /* Function | falsey */) {
    toObs = toObs || id;
    if (this._concurLim === -1 || this._curSources.length < this._concurLim) {
      this._addToCur(toObs(obj));
    } else {
      if (this._queueLim === -1 || this._queue.length < this._queueLim) {
        this._addToQueue(toObs(obj));
      } else if (this._drop === 'old') {
        this._removeOldest();
        this._add(obj, toObs);
      }
    }
  },

  _addAll(obss) {
    forEach(obss, (obs) => this._add(obs));
  },

  _remove(obs) {
    if (this._removeCur(obs) === -1) {
      this._removeQueue(obs);
    }
  },

  _addToQueue(obs) {
    this._queue = concat(this._queue, [obs]);
  },

  _addToCur(obs) {
    if (this._active) {

      // HACK:
      //
      // We have two optimizations for cases when `obs` is ended. We don't want
      // to add such observable to the list, but only want to emit events
      // from it (if it has some).
      //
      // Instead of this hacks, we could just did following,
      // but it would be 5-8 times slower:
      //
      //     this._curSources = concat(this._curSources, [obs]);
      //     this._subscribe(obs);
      //

      // #1
      // This one for cases when `obs` already ended
      // e.g., Kefir.constant() or Kefir.never()
      if (!obs._alive) {
        if (obs._currentEvent) {
          this._emit(obs._currentEvent.type, obs._currentEvent.value);
        }
        return;
      }

      // #2
      // This one is for cases when `obs` going to end synchronously on
      // first subscriber e.g., Kefir.stream(em => {em.emit(1); em.end()})
      this._currentlyAdding = obs;
      obs.onAny(this._$handleSubAny);
      this._currentlyAdding = null;
      if (obs._alive) {
        this._curSources = concat(this._curSources, [obs]);
        if (this._active) {
          this._subToEnd(obs);
        }
      }

    } else {
      this._curSources = concat(this._curSources, [obs]);
    }
  },

  _subToEnd(obs) {
    const onEnd = () => this._removeCur(obs);
    this._$endHandlers.push({obs: obs, handler: onEnd});
    obs.onEnd(onEnd);
  },

  _subscribe(obs) {
    obs.onAny(this._$handleSubAny);

    // it can become inactive in responce of subscribing to `obs.onAny` above
    if (this._active) {
      this._subToEnd(obs);
    }
  },

  _unsubscribe(obs) {
    obs.offAny(this._$handleSubAny);

    let onEndI = findByPred(this._$endHandlers, (obj) => obj.obs === obs);
    if (onEndI !== -1) {
      obs.offEnd(this._$endHandlers[onEndI].handler);
      this._$endHandlers.splice(onEndI, 1);
    }
  },

  _handleSubAny(event) {
    if (event.type === VALUE) {
      this._emitValue(event.value);
    } else if (event.type === ERROR) {
      this._emitError(event.value);
    }
  },

  _removeQueue(obs) {
    let index = find(this._queue, obs);
    this._queue = remove(this._queue, index);
    return index;
  },

  _removeCur(obs) {
    if (this._active) {
      this._unsubscribe(obs);
    }
    let index = find(this._curSources, obs);
    this._curSources = remove(this._curSources, index);
    if (index !== -1) {
      if (this._queue.length !== 0) {
        this._pullQueue();
      } else if (this._curSources.length === 0) {
        this._onEmpty();
      }
    }
    return index;
  },

  _removeOldest() {
    this._removeCur(this._curSources[0]);
  },

  _pullQueue() {
    if (this._queue.length !== 0) {
      this._queue = cloneArray(this._queue);
      this._addToCur(this._queue.shift());
    }
  },

  _onActivation() {
    for (let i = 0, sources = this._curSources; i < sources.length && this._active; i++) {
      this._subscribe(sources[i]);
    }
  },

  _onDeactivation() {
    for (let i = 0, sources = this._curSources; i < sources.length; i++) {
      this._unsubscribe(sources[i]);
    }
    if (this._currentlyAdding !== null) {
      this._unsubscribe(this._currentlyAdding);
    }
  },

  _isEmpty() {
    return this._curSources.length === 0;
  },

  _onEmpty() {},

  _clear() {
    Stream.prototype._clear.call(this);
    this._queue = null;
    this._curSources = null;
    this._$handleSubAny = null;
    this._$endHandlers = null;
  }

});

export default AbstractPool;
