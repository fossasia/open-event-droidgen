from urllib2 import urlopen

from flask import request
from jinja2 import Undefined


def get_real_ip(local_correct=False):
    """
    Get the IP of the user
    :param local_correct:
    :return:
    """
    try:
        if 'X-Forwarded-For' in request.headers:
            ip = request.headers.getlist("X-Forwarded-For")[0].rpartition(' ')[-1]
        else:
            ip = request.remote_addr or None

        if local_correct and (ip == '127.0.0.1' or ip == '0.0.0.0'):
            ip = urlopen('http://ip.42.pl/raw').read()  # On local test environments
    except:
        ip = None

    return ip


class SilentUndefined(Undefined):
    """
    From http://stackoverflow.com/questions/6190348/
    Don't break page loads because vars aren't there!
    """

    def _fail_with_undefined_error(self, *args, **kwargs):
        return False

    __add__ = __radd__ = __mul__ = __rmul__ = __div__ = __rdiv__ = \
        __truediv__ = __rtruediv__ = __floordiv__ = __rfloordiv__ = \
        __mod__ = __rmod__ = __pos__ = __neg__ = __call__ = \
        __getitem__ = __lt__ = __le__ = __gt__ = __ge__ = __int__ = \
        __float__ = __complex__ = __pow__ = __rpow__ = \
        _fail_with_undefined_error


def request_wants_json():
    """
    Check if a request expects a json response
    :return:
    """
    best = request.accept_mimetypes.best_match(
        ['application/json', 'text/html'])
    return best == 'application/json' and request.accept_mimetypes[best] > request.accept_mimetypes['text/html']
