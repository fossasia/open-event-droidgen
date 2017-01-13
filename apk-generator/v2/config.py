import os

basedir = os.path.abspath(os.path.dirname(__file__))

VERSION_NAME = '2.0.0-alpha.1'


class Config:
    WORKING_DIR = os.getenv('GENERATOR_WORKING_DIR', os.path.abspath(os.path.dirname(__file__) + '/../temp/'))
    VERSION = VERSION_NAME
    DEBUG = False
    BASE_DIR = basedir
    MINIFY_PAGE = True
    STATICFILES_DIRS = (os.path.join(BASE_DIR, 'static'),)
    SERVER_NAME = os.getenv('SERVER_NAME', 'localhost')
    ANDROID_HOME = os.getenv('ANDROID_HOME')
    FORCE_SSL = os.getenv('FORCE_SSL', 'no') == 'yes'
    STATIC_URL = '/static/'
    APP_SOURCE_DIR = os.path.abspath(basedir + '/../../android/')

    def __init__(self):
        pass


class ProductionConfig(Config):
    DEBUG = False
    MINIFY_PAGE = True
    PRODUCTION = True
    INTEGRATE_SOCKETIO = True

    # if force off
    socketio_integration = os.environ.get('INTEGRATE_SOCKETIO')
    if socketio_integration == 'false':
        INTEGRATE_SOCKETIO = False


class DevelopmentConfig(Config):
    DEVELOPMENT = True
    DEBUG = True
    MINIFY_PAGE = False

    # If Env Var `INTEGRATE_SOCKETIO` is set to 'true', then integrate SocketIO
    socketio_integration = os.environ.get('INTEGRATE_SOCKETIO')
    INTEGRATE_SOCKETIO = bool(socketio_integration == 'true')


class TestingConfig(Config):
    TESTING = True
    CELERY_ALWAYS_EAGER = True
    CELERY_EAGER_PROPAGATES_EXCEPTIONS = True
    BROKER_BACKEND = 'memory'
