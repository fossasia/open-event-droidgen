## 3.4.0 (17/08/2016)

 - [Static Land](https://github.com/rpominov/static-land) support added.

## 3.3.0 (16/07/2016)

 - New method `.observe` [#151](https://github.com/rpominov/kefir/issues/151) [#221](https://github.com/rpominov/kefir/pull/211) [@mAAdhaTTah](https://github.com/mAAdhaTTah)
 - Emitter methods renamed `emit → value`, `emitEvent → event` (old names will work as well for now)

## 3.2.6 (5/07/2016)

 - `stream[Symbol.observable]().subscribe()` now returns an object instead of `unsub` function [#212](https://github.com/rpominov/kefir/pull/212)
 - `stream[Symbol.observable]().subscribe` now supports `subscribe(onValue, onError, onComplete)` format [27192e6](https://github.com/rpominov/kefir/commit/27192e6af06aa1e94eb0bd41a8ad633f2f88a09a)

## 3.2.5 (4/07/2016)

 - `stream[Symbol.observable]` now equals to `stream[Symbol.observable][Symbol.observable]` [#209](https://github.com/rpominov/kefir/pull/209) [#210](https://github.com/rpominov/kefir/pull/210) [@mAAdhaTTah](https://github.com/mAAdhaTTah)

## 3.2.3 (20/05/2016)

 - .babelrc added to .npmignore [#203](https://github.com/rpominov/kefir/pull/203) [@aksonov](https://github.com/aksonov)

## 3.2.2 (26/04/2016)

 - Deprecation warnings disabling API fixed [#185](https://github.com/rpominov/kefir/issues/185)
 - A bug in `.bufferWithTimeOrCount` is fixed [#194](https://github.com/rpominov/kefir/issues/194)
 - Switched to `symbol-observable` [#200](https://github.com/rpominov/kefir/pull/200) [@blesh](https://github.com/blesh)

## 3.2.1 (25/02/2016)

 - Switched from `require` to `import/export` internally.
 - Switched from Webpack to Rollup for building /dist bundle.
 - Added `jsnext:main` to `pakcage.json` for Rollup users.

Bundle file size dropped from **108K / 49K / 9.8K** to **80K / 42K / 9.5K**
for **dev / min / min+gzip** respectively. And the users' app bundle size can
be reduced even further if they use Rollup and `jsnext:main`.

Big thanks to [@rvikmanis](https://github.com/rvikmanis) for doing this. [#184](https://github.com/rpominov/kefir/pull/184)

## 3.2.0 (12/12/2015)

 - New method `.bufferWithTimeOrCount` [#172](https://github.com/rpominov/kefir/pull/172) [@mcmathja](https://github.com/mcmathja)
 - New method `.bufferWithCount` [#170](https://github.com/rpominov/kefir/pull/170) [@elsehow](https://github.com/elsehow)

## 3.1.0 (5/10/2015)

 - Interoperation with [ECMAScript Observables](https://github.com/zenparsing/es-observable) added with two new methods: `.fromESObservable` and `.toESObservable`/`[Symbol.observable]` [#154](https://github.com/rpominov/kefir/pull/154)  [@lautis](https://github.com/lautis)
 - A bug in `.fromEvents` fixed [#159](https://github.com/rpominov/kefir/issues/159)

## 3.0.0 (26/09/2015)

 - All previously deprecated methods removed. Full list can be found in [deprecated API docs of v2.x](https://github.com/rpominov/kefir/blob/v2/deprecated-api-docs.md).
 - New deprecations: [errorsToValues & valuesToErrors](https://github.com/rpominov/kefir/issues/149), [endOnError](https://github.com/rpominov/kefir/issues/150), [awaiting](https://github.com/rpominov/kefir/issues/145).
 - New method `.takeErrors` replacing deprecated `.endOnError` [#150](https://github.com/rpominov/kefir/issues/150)
 - Methods `.skipValues`, `.skipErrors`, and `.skipEnd` are renamed to `.ignoreValues`, `.ignoreErrors`, and `.ignoreEnd` [#152](https://github.com/rpominov/kefir/issues/152)
 - The `emitEmpty` option from `.bufferWhileBy` removed, it now always emits `[]` if necessary [#131](https://github.com/rpominov/kefir/issues/131)
 - Property now sets its current value **before** dispatching [#127](https://github.com/rpominov/kefir/issues/127) [@olivierguerriat](https://github.com/olivierguerriat)
 - Event objects now don't contain `.current` property [#100](https://github.com/rpominov/kefir/issues/100)
 - The `.flatten` method now always returns a stream [#144](https://github.com/rpominov/kefir/issues/144)


See also [Umbrella 3.0](https://github.com/rpominov/kefir/issues/138)


## 2.8.2 (13/09/2015)

 - A bug in `.scan` fixed [#148](https://github.com/rpominov/kefir/issues/148)

## 2.8.1 (30/08/2015)

 - A bug in `.delay` fixed [#134](https://github.com/rpominov/kefir/issues/134) [@AgentME](https://github.com/AgentME)

## 2.8.0 (21/08/2015)

 - The `emitEmpty` option added to `.bufferWhileBy` [#129](https://github.com/rpominov/kefir/pull/129) [@shamansir](https://github.com/shamansir)

## 2.7.2 (16/07/2015)

 - A bug related to calling `emitter.end()` in response to an end event or in `unsubscribe()` function is fixed [83b06a7](https://github.com/rpominov/kefir/commit/83b06a7debb553dd7fd21c407c49b45da3e1b0ea)

## 2.7.1 (11/07/2015)

 - A bug when listener could be called after unsubscribing or end is fixed [#119](https://github.com/rpominov/kefir/issues/119)

## 2.7.0 (27/06/2015)

 - The `flushOnChange` option added to `.bufferWhileBy` [#116](https://github.com/rpominov/kefir/issues/116)

## 2.6.0 (05/06/2015)

 - A bug in `.bufferBy` fixed [#108](https://github.com/rpominov/kefir/issues/108)
 - Another (minor) perf improvement for `flatMap` and similar methods [c329c61](https://github.com/rpominov/kefir/commit/c329c61c4869a550e2b29eaa9ab6c7d4a7e899ad)
 - The `.toProperty` method now throws with a meaningful message when called with not a function [544b689](https://github.com/rpominov/kefir/commit/544b689d9ffd165d09f3d04de8c40a4b59ceb04c)

## 2.5.0 (21/05/2015)

 - The repository moved from `pozadi/kefir` to `rpominov/kefir`
 - [emitter](http://rpominov.github.io/kefir/#emitter-object) methods now return a boolean representing whether anybody interested in future events (i.e. whether connected observable is active)
 - Another optimization for `.flatMap((x) => Kefir.constan(...))` case [9e4a58a](https://github.com/rpominov/kefir/commit/9e4a58a02ec5f80b3c3c6cf52e5e5065249aba50)
 - Methods `.takeWhileBy` and `.skipWhileBy` are deprecated [#105](https://github.com/rpominov/kefir/issues/105)

## 2.4.1 (14/05/2015)

 - `.flatMap*`, `.pool`, and `.merge` was optimized for use with constants (`Kefir.contant*`, `Kefir.never`), combined with optimizations for constants in 2.4.0 this `foo.flatMap((x) => Kefir.constant(x + 1))` is only ~2x slower than `foo.map(x => x + 1)`

## 2.4.0 (11/05/2015)

 - New method `.flatMapErrors`
 - A bug in `.flatMap*` fixed [98f65b7](https://github.com/rpominov/kefir/commit/98f65b775e2a9785bb66fa1f4a98ffc9bd03b9ff)
 - `Kefir.constant()` and `Kefir.contantError()` made cheaper and faster, so they can be used with `.flatMap` even more freely [1c9de75](https://github.com/rpominov/kefir/commit/1c9de75aa7ed38949716b9d117b430587bd3425c)

## 2.3.0 (10/05/2015)

 - New method `.toPromise`

## 2.2.1 (09/05/2015)

 - A bug in `.offLog` fixed

## 2.2.0 (08/05/2015)

 - Codebase ported to ES6 (Babel) with CommonJS modules
 - A bug in .combine fixed [#98](https://github.com/rpominov/kefir/issues/98)

## 2.1.0 (28/04/2015)

 - New method `.last`
 - The `.reduce` method is deprecated in favor of `.scan(...).last()`

## 2.0.1 (26/04/2015)

 - A bug in `.flatMap` fixed [#92](https://github.com/rpominov/kefir/issues/92)

## 2.0.0 (22/04/2015)

### Breaking changes

 - Removed support of old transducers protocol in the `.transduce` [#79](https://github.com/rpominov/kefir/issues/79)
 - `stream.changes()` now returns a new stream with current values/errors removed [#56](https://github.com/rpominov/kefir/issues/56)
 - Properties now can't have both current value and current error at the same time [#55](https://github.com/rpominov/kefir/issues/55)
 - Better errors handling in `.combine` [#54](https://github.com/rpominov/kefir/issues/54)
 - The `.toProperty` method now accepts a callbak instead of a simple value [#82](https://github.com/rpominov/kefir/issues/82)
 - The `.fromEvent` method is renamed to `.fromEvents`
 - The `.fromBinder` method is renamed to `.stream`
 - The `.mapEnd` method is renamed to `.beforeEnd` [#89](https://github.com/rpominov/kefir/issues/89)

### Other changes

 - The `.fromSubUnsub` method is deprecated [#71](https://github.com/rpominov/kefir/issues/71)
 - Methods `Kefir.emitter()` and `Kefir.bus()` are deprecated [#88](https://github.com/rpominov/kefir/issues/88)

## 1.3.2 (26/04/2015)

 - A bug in `.flatMap` fixed [#92](https://github.com/rpominov/kefir/issues/92)

## 1.3.1 (04/04/2015)

 - The `.transduce` method updated to add support of new protocol [#78](https://github.com/rpominov/kefir/issues/78)


## 1.3.0 (29/03/2015)

 - Following methods are deprecated:
   `.repeatedly`, `.mapTo`, `.pluck`, `.invoke`, `.not`, `.timestamp`, `.tap`, `.and`, `.or`
   [#71](https://github.com/rpominov/kefir/issues/71)

## 1.2.0 (14/03/2015)

 - `Kefir.sampledBy` is deprecated in favor of 3 arity `Kefir.combine`

## 1.1.0 (15/02/2015)

 - The `Bus` and `Pool` classes are exposed as `Kefir.Bus` and `Kefir.Pool`
 - A bug in `.merge` and `.zip` (which may cause them to not unsubscribe from their sources in very rare cases) fixed
 - New method `.emitEvent` in Emitter, Emitter Object, and Bus
 - New method `Kefir.repeat`

## 1.0.0 (31/01/2015)

 - jQuery plugin moved to a [separate repo](https://github.com/rpominov/kefir-jquery)
 - Minor improvement in .skipDuplicates method [#42](https://github.com/rpominov/kefir/issues/42)
 - Deperecated method .withDefault now removed

## 0.5.3 (12/01/2015)

 - A bug in .fromBinder fixed (continuation of [#35](https://github.com/rpominov/kefir/issues/35)

## 0.5.2 (12/01/2015)

 - A bug in .fromBinder fixed [#35](https://github.com/rpominov/kefir/issues/35)

## 0.5.1 (08/01/2015)

 - Undocumented methods `.on/.off` renamed to `._on/._off`
 - The `.changes` method now can be called on a stream
 - The `.toProperty` method now can be called on a property, and works similar to `.withDefault`
 - The `.withDefault` method is now deprecated, and will be removed in the future
 - New method `.fromSubUnsub`
 - New method `.fromNodeCallback`
 - New method `.fromPromise`


## 0.5.0 (06/01/2015)

 - Base errors support added (i.e. errors flow through all kind of transformations/combinations)
 - Properties now may have a current error (as well as current value)
 - New method `.onError`
 - New method `.offError`
 - New method `.error` in Emitter, Emitter Object, and Bus
 - New method `Kefir.constantError`
 - New method `.mapErrors`
 - New method `.filterErrors`
 - New method `.endOnError`
 - New method `.errorsToValues`
 - New method `.valuesToErrors`
 - New method `.skipErrors`
 - New method `.skipValues`


## 0.4.2 (24/12/2014)

 - A bug in `.flatMap` fixed [#29](https://github.com/rpominov/kefir/issues/29)
 - Minor perf fixes

## 0.4.1 (30/11/2014)

 - New method `.bufferWhile`
 - New method `.bufferBy`
 - New method `.bufferWhileBy`
 - New method `.withDefault`
 - New method `.zip`


## 0.4.0 (23/11/2014)

 - The `seed` argument in `.scan`, `.reduce`, and `.diff` is now optional
 - Removed support of ["array functions"](https://github.com/rpominov/kefir/blob/2edf32a82d5b24ecb6ed99c9bcbd2391b91c8715/docs-src/descriptions/about-callbacks.jade)
 - The default `fn` in `obs.sampledBy(other, fn)` changed from `function(a, b) {return [a, b]}` to `function(a, b) {return a}`. The default `fn` for `Kefir.sampledBy` hasn't changed.
 - New method `.mapEnd`
 - New method `.skipEnd`
 - The `fn` argument in `.filter`, `.takeWhile`, and `.skipWhile` is now optional


## 0.3.0 (19/11/2014)

 - Removed undocumented feature of `.merge` and `.concat` that allowed to not wrap observables to array but pass them as individual arguments
 - Changed arguments order in `.scan`, `.reduce`, and `.diff`
 - Added support of on/off methods pair to `.fromEvent`
 - Removed undocumented support of bind/unbind pair from `.fromEvent`
 - Method `.waitFor` renamed to `.skipUntilBy`
 - New method `.takeUntilBy`
 - Method `source.flatMapFirst(fn)` now won't call `fn` when skiping values from `source`

## 0.2.11 (03/11/2014)

 - The `fn` argument of the `.diff` method is now optional
 - New method `.waitFor`
 - New method `.takeWhileBy`
 - New method `.skipWhileBy`


## 0.2.10 (26/10/2014)

 - Method `.transform` renamed to `.flatten`
 - New method `.slidingWindow`


## 0.2.9 (19/10/2014)

 - The `fn` argument of the `.transform` method is now optional
 - New method `.transduce`


## 0.2.8 (12/10/2014)

 - Method `.flatMapWithConcurrencyLimit` renamed to `.flatMapConcurLimit`
 - New method `.transform`
 - New method `.timestamp`
 - New method `Kefir.bus`


## 0.2.7 (05/10/2014)

Methods so far:

  - Kefir.emitter
  - Kefir.never
  - Kefir.later
  - Kefir.interval
  - Kefir.sequentially
  - Kefir.repeatedly
  - Kefir.fromPoll
  - Kefir.withInterval
  - Kefir.fromCallback
  - Kefir.fromEvent
  - Kefir.fromBinder
  - Kefir.constant
  - jQuery::asKefirStream
  - jQuery::asKefirProperty
  - .toProperty
  - .changes
  - .onValue
  - .offValue
  - .onEnd
  - .offEnd
  - .onAny
  - .offAny
  - .log
  - .offLog
  - .map
  - .mapTo
  - .pluck
  - .invoke
  - .not
  - .tap
  - .filter
  - .take
  - .takeWhile
  - .skip
  - .skipWhile
  - .skipDuplicates
  - .diff
  - .scan
  - .reduce
  - .delay
  - .throttle
  - .debounce
  - .withHandler
  - .combine
  - .and
  - .or
  - .sampledBy
  - .merge
  - .concat
  - .pool
  - .flatMap
  - .flatMapLatest
  - .flatMapFirst
  - .flatMapConcat
  - .flatMapWithConcurrencyLimit
  - .awating
  - .filterBy
