jQuery.fn.extend({
        /**
         * An extended jQuery method to call a callback on an input change
         */
        valueChange: function (callback) {
            return this.each(function () {
                var elem = $(this);
                elem.data('oldVal', elem.val());
                elem.on("propertychange change click keyup input paste", function (event) {
                    if (elem.data('oldVal') != elem.val()) {
                        elem.data('oldVal', elem.val());
                        callback(elem.val(), event, elem);
                    }
                });
            });
        },
        /**
         * Disable a jQuery element
         * @returns {*}
         */
        disable: function () {
            return this.each(function () {
                if (!$(this).hasClass("nt")) {
                    $(this).prop('disabled', true);
                }
            });
        },
        /**
         * Enable a jQuery element
         * @returns {*}
         */
        enable: function () {
            return this.each(function () {
                if (!$(this).hasClass("nt")) {
                    $(this).prop('disabled', false);
                }
            });
        },
        /**
         * Disable all input,textarea of a form
         * @returns {*}
         */
        lockFormInputs: function () {
            return this.each(function () {
                $(this).find("select,input,textarea").disable();

            });
        },
        /**
         * Enable all input, textarea of a form
         * @returns {*}
         */
        unlockFormInputs: function () {
            return this.each(function () {
                $(this).find("select,input,textarea").enable();
            });
        }
    }
);


/**
 * @type {RegExp}
 */
var urlRe = new RegExp(
    "^" +
    // protocol identifier
    "(?:(?:https?)://)" +
    // user:pass authentication
    "(?:\\S+(?::\\S*)?@)?" +
    "(?:" +
    // IP address exclusion
    // private & local networks
    "(?!(?:10|127)(?:\\.\\d{1,3}){3})" +
    "(?!(?:169\\.254|192\\.168)(?:\\.\\d{1,3}){2})" +
    "(?!172\\.(?:1[6-9]|2\\d|3[0-1])(?:\\.\\d{1,3}){2})" +
    // IP address dotted notation octets
    // excludes loopback network 0.0.0.0
    // excludes reserved space >= 224.0.0.0
    // excludes network & broacast addresses
    // (first & last IP address of each class)
    "(?:[1-9]\\d?|1\\d\\d|2[01]\\d|22[0-3])" +
    "(?:\\.(?:1?\\d{1,2}|2[0-4]\\d|25[0-5])){2}" +
    "(?:\\.(?:[1-9]\\d?|1\\d\\d|2[0-4]\\d|25[0-4]))" +
    "|" +
    // host name
    "(?:(?:[a-z\\u00a1-\\uffff0-9]-*)*[a-z\\u00a1-\\uffff0-9]+)" +
    // domain name
    "(?:\\.(?:[a-z\\u00a1-\\uffff0-9]-*)*[a-z\\u00a1-\\uffff0-9]+)*" +
    // TLD identifier
    "(?:\\.(?:[a-z\\u00a1-\\uffff]{2,}))" +
    // TLD may end with dot
    "\\.?" +
    ")" +
    // port number
    "(?::\\d{2,5})?" +
    // resource path
    "(?:[/?#]\\S*)?" +
    "$", "i"
);

/**
 * Check if a string is a link
 *
 * @param link
 * @returns {boolean}
 */
function isLink(link) {
    return urlRe.test(link);
}
