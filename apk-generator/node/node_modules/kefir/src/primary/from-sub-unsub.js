import stream from './stream';
import {apply} from '../utils/functions';

export default function fromSubUnsub(sub, unsub, transformer /* Function | falsey */) {
  return stream(function(emitter) {

    let handler = transformer
      ? function() {
        emitter.emit(apply(transformer, this, arguments));
      }
      : x => {
        emitter.emit(x)
      };

    sub(handler);
    return () => unsub(handler);

  }).setName('fromSubUnsub');
}
