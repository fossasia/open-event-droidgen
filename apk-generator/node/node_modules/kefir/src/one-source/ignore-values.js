import {createStream, createProperty} from '../patterns/one-source';

const mixin = {
  _handleValue() {}
};

const S = createStream('ignoreValues', mixin);
const P = createProperty('ignoreValues', mixin);


export default function ignoreValues(obs) {
  return new (obs._ofSameType(S, P))(obs);
}
