import {createStream, createProperty} from '../patterns/one-source';

const mixin = {
  _handleError() {}
};

const S = createStream('ignoreErrors', mixin);
const P = createProperty('ignoreErrors', mixin);


export default function ignoreErrors(obs) {
  return new (obs._ofSameType(S, P))(obs);
}
