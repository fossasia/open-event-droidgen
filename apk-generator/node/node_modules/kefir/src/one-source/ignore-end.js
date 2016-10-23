import {createStream, createProperty} from '../patterns/one-source';

const mixin = {
  _handleEnd() {}
};

const S = createStream('ignoreEnd', mixin);
const P = createProperty('ignoreEnd', mixin);


export default function ignoreEnd(obs) {
  return new (obs._ofSameType(S, P))(obs);
}
