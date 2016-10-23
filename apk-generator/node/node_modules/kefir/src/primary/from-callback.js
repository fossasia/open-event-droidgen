import stream from './stream';

export default function fromCallback(callbackConsumer) {

  let called = false;

  return stream(function(emitter) {

    if (!called) {
      callbackConsumer(function(x) {
        emitter.emit(x);
        emitter.end();
      });
      called = true;
    }

  }).setName('fromCallback');
}
