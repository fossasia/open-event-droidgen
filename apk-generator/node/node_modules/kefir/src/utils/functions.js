function spread(fn, length) {
  switch(length) {
    case 0: return function() {return fn();};
    case 1: return function(a) {return fn(a[0]);};
    case 2: return function(a) {return fn(a[0], a[1]);};
    case 3: return function(a) {return fn(a[0], a[1], a[2]);};
    case 4: return function(a) {return fn(a[0], a[1], a[2], a[3]);};
    default: return function(a) {return fn.apply(null, a);};
  }
}

function apply(fn, c, a) {
  let aLength = a ? a.length : 0;
  if (c == null) {
    switch (aLength) {
      case 0: return fn();
      case 1: return fn(a[0]);
      case 2: return fn(a[0], a[1]);
      case 3: return fn(a[0], a[1], a[2]);
      case 4: return fn(a[0], a[1], a[2], a[3]);
      default: return fn.apply(null, a);
    }
  } else {
    switch (aLength) {
      case 0: return fn.call(c);
      default: return fn.apply(c, a);
    }
  }
}

export {spread, apply};
