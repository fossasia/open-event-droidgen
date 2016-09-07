'use strict';

var base64Url = require('base64-url').escape;

var getParamBytesForAlg = require('./param-bytes-for-alg');

var MAX_OCTET = 0x80,
	CLASS_UNIVERSAL = 0,
	PRIMITIVE_BIT = 0x20,
	TAG_SEQ = 0x10,
	TAG_INT = 0x02,
	ENCODED_TAG_SEQ = (TAG_SEQ | PRIMITIVE_BIT) | (CLASS_UNIVERSAL << 6),
	ENCODED_TAG_INT = TAG_INT | (CLASS_UNIVERSAL << 6);

function signatureAsBuffer(signature) {
	if (Buffer.isBuffer(signature)) {
		return signature;
	} else if ('string' === typeof signature) {
		return new Buffer(signature, 'base64');
	}

	throw new TypeError('ECDSA signature must be a Base64 string or a Buffer');
}

function derToJose(signature, alg) {
	signature = signatureAsBuffer(signature);
	var paramBytes = getParamBytesForAlg(alg);

	// the DER encoded param should at most be the param size, plus a padding
	// zero, since due to being a signed integer
	var maxEncodedParamLength = paramBytes + 1;

	var inputLength = signature.length;

	var offset = 0;
	if (signature[offset++] !== ENCODED_TAG_SEQ) {
		throw new Error('Could not find expected "seq"');
	}

	var seqLength = signature[offset++];
	if (seqLength === (MAX_OCTET | 1)) {
		seqLength = signature[offset++];
	}

	if (inputLength - offset < seqLength) {
		throw new Error('"seq" specified length of "' + seqLength + '", only "' + (inputLength - offset) + '" remaining');
	}

	if (signature[offset++] !== ENCODED_TAG_INT) {
		throw new Error('Could not find expected "int" for "r"');
	}

	var rLength = signature[offset++];

	if (inputLength - offset - 2 < rLength) {
		throw new Error('"r" specified length of "' + rLength + '", only "' + (inputLength - offset - 2) + '" available');
	}

	if (maxEncodedParamLength < rLength) {
		throw new Error('"r" specified length of "' + rLength + '", max of "' + maxEncodedParamLength + '" is acceptable');
	}

	var r = signature.slice(offset, offset + rLength);
	offset += r.length;

	if (signature[offset++] !== ENCODED_TAG_INT) {
		throw new Error('Could not find expected "int" for "s"');
	}

	var sLength = signature[offset++];

	if (inputLength - offset !== sLength) {
		throw new Error('"s" specified length of "' + sLength + '", expected "' + (inputLength - offset) + '"');
	}

	if (maxEncodedParamLength < sLength) {
		throw new Error('"s" specified length of "' + sLength + '", max of "' + maxEncodedParamLength + '" is acceptable');
	}

	var s = signature.slice(offset);
	offset += s.length;

	if (offset !== inputLength) {
		throw new Error('Expected to consume entire buffer, but "' + (inputLength - offset) + '" bytes remain');
	}

	var rPadding = paramBytes - r.length,
		sPadding = paramBytes - s.length;

	signature = new Buffer(rPadding + r.length + sPadding + s.length);

	for (offset = 0; offset < rPadding; ++offset) {
		signature[offset] = 0;
	}
	r.copy(signature, offset, Math.max(-rPadding, 0));

	offset = paramBytes;

	for (var o = offset; offset < o + sPadding; ++offset) {
		signature[offset] = 0;
	}
	s.copy(signature, offset, Math.max(-sPadding, 0));

	signature = signature.toString('base64');
	signature = base64Url(signature);

	return signature;
}

function reduceBuffer(buf) {
	var padding = 0;
	for (var n = buf.length; padding < n && buf[padding] === 0;) {
		++padding;
	}

	var needsSign = buf[padding] >= MAX_OCTET;
	if (needsSign) {
		--padding;

		if (padding < 0) {
			var old = buf;
			buf = new Buffer(1 + buf.length);
			buf[0] = 0;
			old.copy(buf, 1);

			return buf;
		}
	}

	if (padding === 0) {
		return buf;
	}

	buf = buf.slice(padding);
	return buf;
}

function joseToDer(signature, alg) {
	signature = signatureAsBuffer(signature);
	var paramBytes = getParamBytesForAlg(alg);

	var signatureBytes = signature.length;
	if (signatureBytes !== paramBytes * 2) {
		throw new TypeError('"' + alg + '" signatures must be "' + paramBytes * 2 + '" bytes, saw "' + signatureBytes + '"');
	}

	var r = reduceBuffer(signature.slice(0, paramBytes));
	var s = reduceBuffer(signature.slice(paramBytes));

	var rsBytes = 1 + 1 + r.length + 1 + 1 + s.length;

	var shortLength = rsBytes < MAX_OCTET;

	signature = new Buffer((shortLength ? 2 : 3) + rsBytes);

	var offset = 0;
	signature[offset++] = ENCODED_TAG_SEQ;
	if (shortLength) {
		// Bit 8 has value "0"
		// bits 7-1 give the length.
		signature[offset++] = rsBytes;
	} else {
		// Bit 8 of first octet has value "1"
		// bits 7-1 give the number of additional length octets.
		signature[offset++] = MAX_OCTET	| 1;
		// length, base 256
		signature[offset++] = rsBytes & 0xff;
	}
	signature[offset++] = ENCODED_TAG_INT;
	signature[offset++] = r.length;
	r.copy(signature, offset);
	offset += r.length;
	signature[offset++] = ENCODED_TAG_INT;
	signature[offset++] = s.length;
	s.copy(signature, offset);

	return signature;
}

module.exports = {
	derToJose: derToJose,
	joseToDer: joseToDer
};
