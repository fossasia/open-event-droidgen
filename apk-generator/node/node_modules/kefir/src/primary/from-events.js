import fromSubUnsub from './from-sub-unsub';

const pairs = [
  ['addEventListener', 'removeEventListener'],
  ['addListener', 'removeListener'],
  ['on', 'off']
];

export default function fromEvents(target, eventName, transformer) {
  let sub, unsub;

  for (let i = 0; i < pairs.length; i++) {
    if (typeof target[pairs[i][0]] === 'function' && typeof target[pairs[i][1]] === 'function') {
      sub = pairs[i][0];
      unsub = pairs[i][1];
      break;
    }
  }

  if (sub === undefined) {
    throw new Error('target don\'t support any of ' +
      'addEventListener/removeEventListener, addListener/removeListener, on/off method pair');
  }

  return fromSubUnsub(
    (handler) => target[sub](eventName, handler),
    (handler) => target[unsub](eventName, handler),
    transformer
  ).setName('fromEvents');
}
