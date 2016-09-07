import merge from '../many-sources/merge';
import map from '../one-source/map';
import skipDuplicates from '../one-source/skip-duplicates';
import toProperty from '../one-source/to-property';

const f = () => false;
const t = () => true;

export default function awaiting(a, b) {
  let result = merge([map(a, t), map(b, f)]);
  result = skipDuplicates(result);
  result = toProperty(result, f);
  return result.setName(a, 'awaiting');
}
