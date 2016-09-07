# <a href="http://rpominov.github.io/kefir/"><img src="http://rpominov.github.io/kefir/Kefir-with-bg.svg" width="60" height="60"></a> Kefir



Kefir — is an Reactive Programming library for JavaScript
inspired by [Bacon.js](https://github.com/baconjs/bacon.js)
and [RxJS](https://github.com/Reactive-Extensions/RxJS)
with focus on high performance and low memory usage.

For docs visit [rpominov.github.io/kefir](http://rpominov.github.io/kefir).
See also [Deprecated API docs](https://github.com/rpominov/kefir/blob/master/deprecated-api-docs.md).



[![Build Status](https://travis-ci.org/rpominov/kefir.svg?branch=master)](https://travis-ci.org/rpominov/kefir)
[![Dependency Status](https://david-dm.org/rpominov/kefir.svg)](https://david-dm.org/rpominov/kefir)
[![devDependency Status](https://david-dm.org/rpominov/kefir/dev-status.svg)](https://david-dm.org/rpominov/kefir#info=devDependencies)
[![Gitter](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/pozadi/kefir?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge)
<!-- [![Code Climate](http://img.shields.io/codeclimate/github/rpominov/kefir.svg)](https://codeclimate.com/github/rpominov/kefir) -->

[Run tests in your browser](http://rpominov.github.io/kefir/test/in-browser/SpecRunner.html) (`gh-pages` branch, i.e. last stable version)



# Installation

Kefir available as NPM and Bower packages, as well as simple files download.

### NPM
```sh
npm install kefir
```

### Bower
```sh
bower install kefir
```

### Download

See [downloads](https://rpominov.github.io/kefir/#downloads) section in the docs.

Also available on [jsDelivr](http://www.jsdelivr.com/#!kefir).


# Development

To build all the things: `/dist`, docs, tests for browser, etc. run:

    grunt
    grunt bower # to also install bower packages which needed for docs

To run tests:

    npm test

