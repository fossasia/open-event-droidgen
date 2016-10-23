import {extend} from './utils/objects';
import {VALUE, ERROR, ANY, END} from './constants';
import {Dispatcher, callSubscriber} from './dispatcher';
import {findByPred} from './utils/collections';



function Observable() {
  this._dispatcher = new Dispatcher();
  this._active = false;
  this._alive = true;
  this._activating = false;
  this._logHandlers = null;
}

extend(Observable.prototype, {

  _name: 'observable',

  _onActivation() {},
  _onDeactivation() {},

  _setActive(active) {
    if (this._active !== active) {
      this._active = active;
      if (active) {
        this._activating = true;
        this._onActivation();
        this._activating = false;
      } else {
        this._onDeactivation();
      }
    }
  },

  _clear() {
    this._setActive(false);
    this._dispatcher.cleanup();
    this._dispatcher = null;
    this._logHandlers = null;
  },

  _emit(type, x) {
    switch (type) {
      case VALUE: return this._emitValue(x);
      case ERROR: return this._emitError(x);
      case END: return this._emitEnd();
    }
  },

  _emitValue(value) {
    if (this._alive) {
      this._dispatcher.dispatch({type: VALUE, value});
    }
  },

  _emitError(value) {
    if (this._alive) {
      this._dispatcher.dispatch({type: ERROR, value});
    }
  },

  _emitEnd() {
    if (this._alive) {
      this._alive = false
      this._dispatcher.dispatch({type: END});
      this._clear();
    }
  },

  _on(type, fn) {
    if (this._alive) {
      this._dispatcher.add(type, fn);
      this._setActive(true);
    } else {
      callSubscriber(type, fn, {type: END});
    }
    return this;
  },

  _off(type, fn) {
    if (this._alive) {
      let count = this._dispatcher.remove(type, fn);
      if (count === 0) {
        this._setActive(false);
      }
    }
    return this;
  },

  onValue(fn) {
    return this._on(VALUE, fn);
  },
  onError(fn) {
    return this._on(ERROR, fn);
  },
  onEnd(fn) {
    return this._on(END, fn);
  },
  onAny(fn) {
    return this._on(ANY, fn);
  },

  offValue(fn) {
    return this._off(VALUE, fn);
  },
  offError(fn) {
    return this._off(ERROR, fn);
  },
  offEnd(fn) {
    return this._off(END, fn);
  },
  offAny(fn) {
    return this._off(ANY, fn);
  },

  observe(observerOrOnValue, onError, onEnd) {
    const _this = this;
    let closed = false;

    const observer = !observerOrOnValue || typeof observerOrOnValue === 'function'
      ? {value: observerOrOnValue, error: onError, end: onEnd}
      : observerOrOnValue;

    const handler = function(event) {
      if (event.type === END) {
        closed = true;
      }
      if (event.type === VALUE && observer.value) {
        observer.value(event.value);
      } else if (event.type === ERROR && observer.error) {
        observer.error(event.value);
      } else if (event.type === END && observer.end) {
        observer.end(event.value);
      }
    };

    this.onAny(handler);

    return {
      unsubscribe() {
        if (!closed) {
          _this.offAny(handler);
          closed = true;
        }
      },
      get closed() {
        return closed;
      }
    };
  },

  // A and B must be subclasses of Stream and Property (order doesn't matter)
  _ofSameType(A, B) {
    return A.prototype.getType() === this.getType() ? A : B;
  },

  setName(sourceObs /* optional */, selfName) {
    this._name = selfName ? `${sourceObs._name}.${selfName}` : sourceObs;
    return this;
  },


  log(name = this.toString()) {

    let isCurrent
    let handler = function(event) {
      let type = `<${event.type}${isCurrent ? ':current' : ''}>`;
      if (event.type === END) {
        console.log(name, type);
      } else {
        console.log(name, type, event.value);
      }
    };

    if (this._alive) {
      if (!this._logHandlers) {
        this._logHandlers = [];
      }
      this._logHandlers.push({name: name, handler: handler});
    }

    isCurrent = true;
    this.onAny(handler);
    isCurrent = false;

    return this;
  },

  offLog(name = this.toString()) {

    if (this._logHandlers) {
      let handlerIndex = findByPred(this._logHandlers, obj => obj.name === name);
      if (handlerIndex !== -1) {
        this.offAny(this._logHandlers[handlerIndex].handler);
        this._logHandlers.splice(handlerIndex, 1);
      }
    }

    return this;
  }
});

// extend() can't handle `toString` in IE8
Observable.prototype.toString = function() {
  return `[${this._name}]`;
};


export default Observable;
