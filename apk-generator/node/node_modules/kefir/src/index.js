import Observable from './observable';
import Stream from './stream';
import Property from './property';



// Create a stream
// -----------------------------------------------------------------------------

// () -> Stream
import never from './primary/never';

// (number, any) -> Stream
import later from './time-based/later';

// (number, any) -> Stream
import interval from './time-based/interval';

// (number, Array<any>) -> Stream
import sequentially from './time-based/sequentially';

// (number, Function) -> Stream
import fromPoll from './time-based/from-poll';

// (number, Function) -> Stream
import withInterval from './time-based/with-interval';

// (Function) -> Stream
import fromCallback from './primary/from-callback';

// (Function) -> Stream
import fromNodeCallback from './primary/from-node-callback';

// Target = {addEventListener, removeEventListener}|{addListener, removeListener}|{on, off}
// (Target, string, Function|undefined) -> Stream
import fromEvents from './primary/from-events';

// (Function) -> Stream
import stream from './primary/stream';



// Create a property
// -----------------------------------------------------------------------------

// (any) -> Property
import constant from './primary/constant';

// (any) -> Property
import constantError from './primary/constant-error';



// Convert observables
// -----------------------------------------------------------------------------

// (Stream|Property, Function|undefined) -> Property
import toProperty from './one-source/to-property';
Observable.prototype.toProperty = function(fn) {
  return toProperty(this, fn);
};

// (Stream|Property) -> Stream
import changes from './one-source/changes';
Observable.prototype.changes = function() {
  return changes(this);
};



// Interoperation with other implimentations
// -----------------------------------------------------------------------------

// (Promise) -> Property
import fromPromise from './interop/from-promise';

// (Stream|Property, Function|undefined) -> Promise
import toPromise from './interop/to-promise';
Observable.prototype.toPromise = function(Promise) {
  return toPromise(this, Promise);
};

// (ESObservable) -> Stream
import fromESObservable from './interop/from-es-observable';

// (Stream|Property) -> ES7 Observable
import toESObservable from './interop/to-es-observable';
Observable.prototype.toESObservable = toESObservable;
import $$observable from 'symbol-observable';
Observable.prototype[$$observable] = toESObservable;

import * as staticLand from './interop/static-land'


// Modify an observable
// -----------------------------------------------------------------------------

// (Stream, Function|undefined) -> Stream
// (Property, Function|undefined) -> Property
import map from './one-source/map';
Observable.prototype.map = function(fn) {
  return map(this, fn);
};

// (Stream, Function|undefined) -> Stream
// (Property, Function|undefined) -> Property
import filter from './one-source/filter';
Observable.prototype.filter = function(fn) {
  return filter(this, fn);
};

// (Stream, number) -> Stream
// (Property, number) -> Property
import take from './one-source/take';
Observable.prototype.take = function(n) {
  return take(this, n);
};

// (Stream, number) -> Stream
// (Property, number) -> Property
import takeErrors from './one-source/take-errors';
Observable.prototype.takeErrors = function(n) {
  return takeErrors(this, n);
};

// (Stream, Function|undefined) -> Stream
// (Property, Function|undefined) -> Property
import takeWhile from './one-source/take-while';
Observable.prototype.takeWhile = function(fn) {
  return takeWhile(this, fn);
};

// (Stream) -> Stream
// (Property) -> Property
import last from './one-source/last';
Observable.prototype.last = function() {
  return last(this);
};

// (Stream, number) -> Stream
// (Property, number) -> Property
import skip from './one-source/skip';
Observable.prototype.skip = function(n) {
  return skip(this, n);
};

// (Stream, Function|undefined) -> Stream
// (Property, Function|undefined) -> Property
import skipWhile from './one-source/skip-while';
Observable.prototype.skipWhile = function(fn) {
  return skipWhile(this, fn);
};

// (Stream, Function|undefined) -> Stream
// (Property, Function|undefined) -> Property
import skipDuplicates from './one-source/skip-duplicates';
Observable.prototype.skipDuplicates = function(fn) {
  return skipDuplicates(this, fn);
};

// (Stream, Function|falsey, any|undefined) -> Stream
// (Property, Function|falsey, any|undefined) -> Property
import diff from './one-source/diff';
Observable.prototype.diff = function(fn, seed) {
  return diff(this, fn, seed);
};

// (Stream|Property, Function, any|undefined) -> Property
import scan from './one-source/scan';
Observable.prototype.scan = function(fn, seed) {
  return scan(this, fn, seed);
};

// (Stream, Function|undefined) -> Stream
// (Property, Function|undefined) -> Property
import flatten from './one-source/flatten';
Observable.prototype.flatten = function(fn) {
  return flatten(this, fn);
};

// (Stream, number) -> Stream
// (Property, number) -> Property
import delay from './one-source/delay';
Observable.prototype.delay = function(wait) {
  return delay(this, wait);
};

// Options = {leading: boolean|undefined, trailing: boolean|undefined}
// (Stream, number, Options|undefined) -> Stream
// (Property, number, Options|undefined) -> Property
import throttle from './one-source/throttle';
Observable.prototype.throttle = function(wait, options) {
  return throttle(this, wait, options);
};

// Options = {immediate: boolean|undefined}
// (Stream, number, Options|undefined) -> Stream
// (Property, number, Options|undefined) -> Property
import debounce from './one-source/debounce';
Observable.prototype.debounce = function(wait, options) {
  return debounce(this, wait, options);
};

// (Stream, Function|undefined) -> Stream
// (Property, Function|undefined) -> Property
import mapErrors from './one-source/map-errors';
Observable.prototype.mapErrors = function(fn) {
  return mapErrors(this, fn);
};

// (Stream, Function|undefined) -> Stream
// (Property, Function|undefined) -> Property
import filterErrors from './one-source/filter-errors';
Observable.prototype.filterErrors = function(fn) {
  return filterErrors(this, fn);
};

// (Stream) -> Stream
// (Property) -> Property
import ignoreValues from './one-source/ignore-values';
Observable.prototype.ignoreValues = function() {
  return ignoreValues(this);
};

// (Stream) -> Stream
// (Property) -> Property
import ignoreErrors from './one-source/ignore-errors';
Observable.prototype.ignoreErrors = function() {
  return ignoreErrors(this);
};

// (Stream) -> Stream
// (Property) -> Property
import ignoreEnd from './one-source/ignore-end';
Observable.prototype.ignoreEnd = function() {
  return ignoreEnd(this);
};

// (Stream, Function) -> Stream
// (Property, Function) -> Property
import beforeEnd from './one-source/before-end';
Observable.prototype.beforeEnd = function(fn) {
  return beforeEnd(this, fn);
};

// (Stream, number, number|undefined) -> Stream
// (Property, number, number|undefined) -> Property
import slidingWindow from './one-source/sliding-window';
Observable.prototype.slidingWindow = function(max, min) {
  return slidingWindow(this, max, min);
};

// Options = {flushOnEnd: boolean|undefined}
// (Stream, Function|falsey, Options|undefined) -> Stream
// (Property, Function|falsey, Options|undefined) -> Property
import bufferWhile from './one-source/buffer-while';
Observable.prototype.bufferWhile = function(fn, options) {
  return bufferWhile(this, fn, options);
};

// (Stream, number) -> Stream
// (Property, number) -> Property
import bufferWithCount from './one-source/buffer-with-count';
Observable.prototype.bufferWithCount = function(count, options) {
  return bufferWithCount(this, count, options);
};

// Options = {flushOnEnd: boolean|undefined}
// (Stream, number, number, Options|undefined) -> Stream
// (Property, number, number, Options|undefined) -> Property
import bufferWithTimeOrCount from './one-source/buffer-with-time-or-count';
Observable.prototype.bufferWithTimeOrCount = function(wait, count, options) {
  return bufferWithTimeOrCount(this, wait, count, options);
};

// (Stream, Function) -> Stream
// (Property, Function) -> Property
import transduce from './one-source/transduce';
Observable.prototype.transduce = function(transducer) {
  return transduce(this, transducer);
};

// (Stream, Function) -> Stream
// (Property, Function) -> Property
import withHandler from './one-source/with-handler';
Observable.prototype.withHandler = function(fn) {
  return withHandler(this, fn);
};



// Combine observables
// -----------------------------------------------------------------------------

// (Array<Stream|Property>, Function|undefiend) -> Stream
// (Array<Stream|Property>, Array<Stream|Property>, Function|undefiend) -> Stream
import combine from './many-sources/combine';
Observable.prototype.combine = function(other, combinator) {
  return combine([this, other], combinator);
};

// (Array<Stream|Property>, Function|undefiend) -> Stream
import zip from './many-sources/zip';
Observable.prototype.zip = function(other, combinator) {
  return zip([this, other], combinator);
};

// (Array<Stream|Property>) -> Stream
import merge from './many-sources/merge';
Observable.prototype.merge = function(other) {
  return merge([this, other]);
};

// (Array<Stream|Property>) -> Stream
import concat from './many-sources/concat';
Observable.prototype.concat = function(other) {
  return concat([this, other]);
};

// () -> Pool
import Pool from './many-sources/pool';
const pool = function() {
  return new Pool();
};

// (Function) -> Stream
import repeat from './many-sources/repeat';

// Options = {concurLim: number|undefined, queueLim: number|undefined, drop: 'old'|'new'|undefiend}
// (Stream|Property, Function|falsey, Options|undefined) -> Stream
import FlatMap from './many-sources/flat-map';
Observable.prototype.flatMap = function(fn) {
  return new FlatMap(this, fn).setName(this, 'flatMap');
};
Observable.prototype.flatMapLatest = function(fn) {
  return new FlatMap(this, fn, {concurLim: 1, drop: 'old'}).setName(this, 'flatMapLatest');
};
Observable.prototype.flatMapFirst = function(fn) {
  return new FlatMap(this, fn, {concurLim: 1}).setName(this, 'flatMapFirst');
};
Observable.prototype.flatMapConcat = function(fn) {
  return new FlatMap(this, fn, {queueLim: -1, concurLim: 1}).setName(this, 'flatMapConcat');
};
Observable.prototype.flatMapConcurLimit = function(fn, limit) {
  return new FlatMap(this, fn, {queueLim: -1, concurLim: limit}).setName(this, 'flatMapConcurLimit');
};

// (Stream|Property, Function|falsey) -> Stream
import FlatMapErrors from './many-sources/flat-map-errors';
Observable.prototype.flatMapErrors = function(fn) {
  return new FlatMapErrors(this, fn).setName(this, 'flatMapErrors');
};



// Combine two observables
// -----------------------------------------------------------------------------

// (Stream, Stream|Property) -> Stream
// (Property, Stream|Property) -> Property
import filterBy from './two-sources/filter-by';
Observable.prototype.filterBy = function(other) {
  return filterBy(this, other);
};

// (Stream, Stream|Property, Function|undefiend) -> Stream
// (Property, Stream|Property, Function|undefiend) -> Property
import sampledBy2items from './two-sources/sampled-by';
Observable.prototype.sampledBy = function(other, combinator) {
  return sampledBy2items(this, other, combinator);
};

// (Stream, Stream|Property) -> Stream
// (Property, Stream|Property) -> Property
import skipUntilBy from './two-sources/skip-until-by';
Observable.prototype.skipUntilBy = function(other) {
  return skipUntilBy(this, other);
};

// (Stream, Stream|Property) -> Stream
// (Property, Stream|Property) -> Property
import takeUntilBy from './two-sources/take-until-by';
Observable.prototype.takeUntilBy = function(other) {
  return takeUntilBy(this, other);
};

// Options = {flushOnEnd: boolean|undefined}
// (Stream, Stream|Property, Options|undefined) -> Stream
// (Property, Stream|Property, Options|undefined) -> Property
import bufferBy from './two-sources/buffer-by';
Observable.prototype.bufferBy = function(other, options) {
  return bufferBy(this, other, options);
};

// Options = {flushOnEnd: boolean|undefined}
// (Stream, Stream|Property, Options|undefined) -> Stream
// (Property, Stream|Property, Options|undefined) -> Property
import bufferWhileBy from './two-sources/buffer-while-by';
Observable.prototype.bufferWhileBy = function(other, options) {
  return bufferWhileBy(this, other, options);
};



// Deprecated
// -----------------------------------------------------------------------------

let DEPRECATION_WARNINGS = true
export function dissableDeprecationWarnings() {
  DEPRECATION_WARNINGS = false
}

function warn(msg) {
  if (DEPRECATION_WARNINGS && console && typeof console.warn === 'function') {
    const msg2 = '\nHere is an Error object for you containing the call stack:';
    console.warn(msg, msg2, new Error());
  }
}

// (Stream|Property, Stream|Property) -> Property
import awaiting from './two-sources/awaiting';
Observable.prototype.awaiting = function(other) {
  warn('You are using deprecated .awaiting() method, see https://github.com/rpominov/kefir/issues/145')
  return awaiting(this, other);
};

// (Stream, Function|undefined) -> Stream
// (Property, Function|undefined) -> Property
import valuesToErrors from './one-source/values-to-errors';
Observable.prototype.valuesToErrors = function(fn) {
  warn('You are using deprecated .valuesToErrors() method, see https://github.com/rpominov/kefir/issues/149')
  return valuesToErrors(this, fn);
};


// (Stream, Function|undefined) -> Stream
// (Property, Function|undefined) -> Property
import errorsToValues from './one-source/errors-to-values';
Observable.prototype.errorsToValues = function(fn) {
  warn('You are using deprecated .errorsToValues() method, see https://github.com/rpominov/kefir/issues/149')
  return errorsToValues(this, fn);
};

// (Stream) -> Stream
// (Property) -> Property
import endOnError from './one-source/end-on-error';
Observable.prototype.endOnError = function() {
  warn('You are using deprecated .endOnError() method, see https://github.com/rpominov/kefir/issues/150')
  return endOnError(this);
};



// Exports
// --------------------------------------------------------------------------

const Kefir = { Observable, Stream, Property, never, later, interval, sequentially,
fromPoll, withInterval, fromCallback, fromNodeCallback, fromEvents, stream,
constant, constantError, fromPromise, fromESObservable, combine, zip, merge,
concat, Pool, pool, repeat, staticLand };

Kefir.Kefir = Kefir;

export { Kefir, Observable, Stream, Property, never, later, interval, sequentially,
fromPoll, withInterval, fromCallback, fromNodeCallback, fromEvents, stream,
constant, constantError, fromPromise, fromESObservable, combine, zip, merge,
concat, Pool, pool, repeat, staticLand };

export default Kefir
