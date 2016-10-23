import repeat from './repeat';

export default function concat(observables) {
  return repeat(function(index) {
    return observables.length > index ? observables[index] : false;
  }).setName('concat');
}
