import {createStream, createProperty} from '../patterns/one-source';

const mixin = {
  _handleEnd() {}
};

const S = createStream('skipEnd', mixin);
const P = createProperty('skipEnd', mixin);


export default function skipEnd(obs) {
  return new (obs._ofSameType(S, P))(obs);
}
