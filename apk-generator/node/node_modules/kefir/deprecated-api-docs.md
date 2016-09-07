# Deprecated API Methods documentation

All documentation on deprecated methods are moved
from [main docs](http://rpominov.github.io/kefir/) to this file.

See also [2.x's version of this document](https://github.com/rpominov/kefir/blob/v2/deprecated-api-docs.md) containing docs on methods that were removed in 3.0.


### `obs.awaiting(otherObs)` [#145](https://github.com/rpominov/kefir/issues/145)

Returns a property that represents the awaiting status of two observables,
i.e. answers the question «Has **obs** observable emitted a value since
the last value from **otherObs** observable has been emitted?».


```js
var foo = Kefir.sequentially(100, [1, 2, 3]);
var bar = Kefir.sequentially(100, [1, 2, 3]).delay(40);
var result = foo.awaiting(bar);
result.log();
```

```
> [sequentially.awaiting] <value:current> false
> [sequentially.awaiting] <value> true
> [sequentially.awaiting] <value> false
> [sequentially.awaiting] <value> true
> [sequentially.awaiting] <value> false
> [sequentially.awaiting] <value> true
> [sequentially.awaiting] <value> false
> [sequentially.awaiting] <end>
```

```
foo:     ----1----2----3X
bar:     ------1----2----3X

result:  f---t-f--t-f--t-fX
```


### `obs.valuesToErrors([handler])` [#149](https://github.com/rpominov/kefir/issues/149)

Converts values to errors. By default it converts all values to errors,
but you can specify a custom **handler** function to change that. The **handler**
function is called with one argument — a value, and must return an object
with two properties `{convert: Boolean, error: AnyType}`, if **convert** is set
to true, the specified **error** will be emitted, otherwise the original value
will be emitted, and the **error** property will be ignored.

```js
var source = Kefir.sequentially(100, [0, -1, 2, -3]);
var result = source.valuesToErrors(x => {
  return {
    convert: x < 0,
    error: x * 2
  };
});
result.log();
```

```
> [sequentially.valuesToErrors] <value> 0
> [sequentially.valuesToErrors] <error> -2
> [sequentially.valuesToErrors] <value> 2
> [sequentially.valuesToErrors] <error> -6
> [sequentially.valuesToErrors] <end>
```

```
source:  ---•---•---•---•X
            0  -1   2  -3
result:  ---•---e---•---eX
            0  -2   2  -6
```


### `obs.errorsToValues([handler])` [#149](https://github.com/rpominov/kefir/issues/149)

Same as valuesToErrors but vice versa.

```js
var source = Kefir.sequentially(100, [0, -1, 2, -3]).valuesToErrors();
var result = source.errorsToValues(x => {
  return {
    convert: x >= 0,
    value: x * 2
  };
});
result.log();
```

```
> [sequentially.valuesToErrors.errorsToValues] <value> 0
> [sequentially.valuesToErrors.errorsToValues] <error> -1
> [sequentially.valuesToErrors.errorsToValues] <value> 4
> [sequentially.valuesToErrors.errorsToValues] <error> -3
> [sequentially.valuesToErrors.errorsToValues] <end>
```

```
source:  ---e---e---e---eX
            0  -1   2  -3
result:  ---•---e---•---eX
            0  -1   4  -3
```


### `obs.endOnError()` [#150](https://github.com/rpominov/kefir/issues/150)

Makes an observable to end on first error.

```js
var source = Kefir.sequentially(100, [0, -1, 2, -3])
  .valuesToErrors(x => {
    return {convert: x < 0, error: x};
  });
var result = source.endOnError()
result.log();
```

```
> [sequentially.valuesToErrors.endOnError] <value> 0
> [sequentially.valuesToErrors.endOnError] <error> -1
> [sequentially.valuesToErrors.endOnError] <end>
```

```
source:  ---•---e---•---eX
            0  -1   2  -3
result:  ---•---eX
            0  -1
```
