export default function emitter(obs) {

  function value(x) {
    obs._emitValue(x);
    return obs._active;
  }

  function error(x) {
    obs._emitError(x);
    return obs._active;
  }

  function end() {
    obs._emitEnd();
    return obs._active;
  }

  function event(e) {
    obs._emit(e.type, e.value);
    return obs._active;
  }

  return {
    value,
    error,
    end,
    event,

    // legacy
    emit: value,
    emitEvent: event
  };
}
