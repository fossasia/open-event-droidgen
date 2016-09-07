import Stream from '../stream';


const neverS = new Stream();
neverS._emitEnd();
neverS._name = 'never';

export default function never() {
  return neverS;
}
