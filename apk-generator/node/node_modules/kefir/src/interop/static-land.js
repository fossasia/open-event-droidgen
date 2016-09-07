import constant from '../primary/constant';
import never from '../primary/never';
import combine from '../many-sources/combine';

const Observable = {

  empty() {
    return never();
  },

  // Monoid based on merge() seems more useful than one based on concat().
  concat(a, b) {
    return a.merge(b);
  },

  of(x) {
    return constant(x);
  },

  map(fn, obs) {
    return obs.map(fn);
  },

  bimap(fnErr, fnVal, obs) {
    return obs.mapErrors(fnErr).map(fnVal);
  },

  // This ap strictly speaking incompatible with chain. If we derive ap from chain we get
  // different (not very useful) behavior. But spec requires that if method can be derived
  // it must have the same behavior as hand-written method. We intentionally violate the spec
  // in hope that it won't cause many troubles in practice. And in return we have more useful type.
  ap(obsFn, obsVal) {
    return combine([obsFn, obsVal], (fn, val) => fn(val));
  },

  chain(fn, obs) {
    return obs.flatMap(fn);
  }

}

export {Observable}
