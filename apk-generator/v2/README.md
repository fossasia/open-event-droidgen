## App Generator v2

#### Technology Stack
- Flask
- Celery
- JDK 8
- Android SDK
- Redis

#### Environment Variables
##### Required
- `ANDROID_HOME` - Path to the android SDK root
- `GENERATOR_WORKING_DIR` - Path to a writable directory which the generator would use as a temp working directory
- `KEYSTORE_PATH` - Path to the key store file with should be used to sign the application package (apk)
- `KEYSTORE_PASSWORD` - The password to access the key store
- `KEY_ALIAS` - The alias of the key to use
##### Optional
- `REDIS_URL` - The url to the redis instance to use for celery (Default: `redis://localhost:6379/0`)
- `INTEGRATE_SOCKETIO` - true/false to enable/disable Socket IO. (Default: `true`)
- `SECRET_KEY` - A string that will be used as a key for any encryption done in the app

#### Installing & Running the server

#### Generating a keystore
- If you don't already have a keystore, you can easily generate one.
```bash
keytool -genkey -v -keystore <path-to-store-file> -alias <alias-to-use> -keyalg RSA -keysize 2048 -validity 10000
```

##### Installing Redis
- If you do not have Redis already, you can install by
```bash
# Download source
wget http://download.redis.io/releases/redis-stable.tar.gz
# Unzip source
tar xzf redis-stable.tar.gz
cd redis-stable
# Compile source
make
# Install redis
sudo make install
# Start redis server in the background
redis-server &
```

All of the following commands are to be executed from within the `apk-generator/v2` directory. So make sure you cd into that directory first.

##### Installation
- `sudo -H pip install -r requirements.txt` to install all requirements

##### Running
- `celery worker -A app.celery &` to start the celery worker in the background
- `gunicorn app:app` to start the app server
