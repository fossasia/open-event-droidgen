import combine from '../many-sources/combine';

const id2 = (_, x) => x;

export default function sampledBy(passive, active, combinator) {
  let _combinator = combinator ? ((a, b) => combinator(b, a)) : id2;
  return combine([active], [passive], _combinator).setName(passive, 'sampledBy');
}
