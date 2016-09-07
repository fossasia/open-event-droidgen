import stream from '../primary/stream';
import $$observable from 'symbol-observable';

export default function fromESObservable(_observable) {
  const observable = _observable[$$observable] ? _observable[$$observable]() : _observable;
  return stream(function(emitter) {
    const unsub = observable.subscribe({
      error(error) {
        emitter.error(error);
        emitter.end();
      },
      next(value) {
        emitter.emit(value);
      },
      complete() {
        emitter.end();
      }
    })

    if (unsub.unsubscribe) {
      return function () { unsub.unsubscribe(); };
    } else {
      return unsub;
    }
  }).setName('fromESObservable');
}
