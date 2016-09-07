import stream from './stream';

export default function fromNodeCallback(callbackConsumer) {

  let called = false;

  return stream(function(emitter) {

    if (!called) {
      callbackConsumer(function(error, x) {
        if (error) {
          emitter.error(error);
        } else {
          emitter.emit(x);
        }
        emitter.end();
      });
      called = true;
    }

  }).setName('fromNodeCallback');

}
