

### Requirements

 Operating system Ubuntu 14.04 or above is preferred. (This guide will be based on ubuntu/debian based operating systems.)
 
- Python 2.7
- Oracle JDK 8
- Android SDK & necessary build tools and platform SDKs
- Redis Server

### 1. LINUX:


### Cloning the Project
```bash
git clone https://github.com/fossasia/open-event-android.git && cd open-event-android
export PROJECT_DIR=$(pwd)
```

### Installing the requirements

#### Installing dependencies

```bash
sudo dpkg --add-architecture i386
sudo apt-get install -y software-properties-common git wget libc6-i386 lib32stdc++6 lib32gcc1 lib32ncurses5 lib32z1 curl libqt5widgets5
sudo apt-get install -y ca-certificates && update-ca-certificates
```

#### Installing Python 2.7.x

```bash
sudo apt-get install -y python python-dev python-pip libpq-dev libevent-dev libmagic-dev 
```

#### Installing Oracle JDK 8
```bash
sudo add-apt-repository ppa:webupd8team/java
sudo apt-get update
sudo apt-get install oracle-java8-installer oracle-java8-set-default
```

#### Installing Android SDK
```bash
sudo cp kubernetes/images/generator/tools /opt
sudo ./kubernetes/images/generator/android.sh

export ANDROID_HOME="/opt/android-sdk-linux"
export PATH=${PATH}:${ANDROID_HOME}/tools:${ANDROID_HOME}/platform-tools
```

 ### 2. OSX:


### Cloning the Project
```bash
git clone https://github.com/fossasia/open-event-android.git && cd open-event-android
export PROJECT_DIR=$(pwd)
```

### Installing the requirements

#### Installing dependencies

#### Install brew
```bash
 /usr/bin/ruby -e "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/master/install)"
  xcode-select --install
```
```bash
brew install software-properties-common git wget libc6-i386 lib32stdc++6 lib32gcc1 lib32ncurses5 lib32z1 curl libqt5widgets5
brew install ca-certificates && update-ca-certificates
```

#### Installing Python 2.7.x

```bash
brew install python 
brew install python-dev 
brew install python-pip 
brew install libpq-dev 
brew install libevent-dev 
brew install libmagic-dev 
```

#### Installing Oracle JDK 8
```bash
You can follow this link (https://docs.oracle.com/javase/8/docs/technotes/guides/install/mac_jdk.html) to install Oracle JDK on your machine.
```

#### Installing Android SDK
```bash
sudo cp kubernetes/images/generator/tools /opt
sudo ./kubernetes/images/generator/android.sh

export ANDROID_HOME={YOUR_PATH}
```
If you downloaded the SDK through their website and then dragged/dropped the Application to your Applications folder, it's most likely here:

```bash
/Applications/ADT/sdk
```

If you installed the SDK using Homebrew (brew install android-sdk), then it's located here:
```bash
/usr/local/Cellar/android-sdk/{YOUR_SDK_VERSION_NUMBER}
```

If the SDK was installed automatically as part of Android Studio then it's located here:
```bash
/Users/{YOUR_USER_NAME}/Library/Android/sdk
```
```
export PATH=${PATH}:${ANDROID_HOME}/tools:${ANDROID_HOME}/platform-tools

```
#### Installing Redis Server

You can follow the [Redis Installation Guide](https://medium.com/@petehouston/install-and-config-redis-on-mac-os-x-via-homebrew-eb8df9a4f298) to install and activate Redis Server on your machine.

### COMMON INSTRUCTIONS FOR OSX/LINUX

### Installing the project requirements

Let's go into the app generator's directory
```bash
cd ${PROJECT_DIR}/apk-generator/v2/
```
#### Installing python dependencies
```bash
sudo -H pip install -r requirements.txt
```

#### Creating a keystore

```bash
export KEY_ALIAS=debug
export KEYSTORE_PASSWORD=debug
export KEYSTORE_PATH=$(pwd)/keystore/debug.keystore
```

```bash
keytool -genkey -noprompt \
 -alias ${KEY_ALIAS} \
 -dname "CN=localhost, OU=SG, O=SomeName, L=SomeCountry, S=SomeCountry, C=SG" \
 -keystore ${KEYSTORE_PATH} \
 -storepass ${KEYSTORE_PASSWORD} \
 -keypass ${KEYSTORE_PASSWORD} \
 -keyalg RSA \
 -keysize 2048 \
 -validity 10000
```

#### Starting the generator

Set the appropriate config class to use as an environment variable
```bash
export APP_CONFIG=config.DevelopmentConfig # for a development environment
export APP_CONFIG=config.ProductionConfig  # for a production environment
export APP_CONFIG=config.TestingConfig     # for a testing environment
```

Set the URI to your redis instance as an environment variable
```bash
export REDIS_URL=redis://localhost:6379/0
```

##### Starting celery worker in the background
```bash
celery worker -A app.celery &
```
##### Note: The '&' in the above command means detaching from the console. To avoid use the following command instead
```bash
celery worker -A app.celery --loglevel=INFO
```
##### Starting the app generator web server
```bash
gunicorn -b 0.0.0.0:8080 app:app --enable-stdio-inheritance --log-level "info"
```

You can now visit [http://localhost:8080](http://localhost:8080/) on your browser to access the app generator.

#### A FEW PRECAUTIONS/TIPS:
##### - Run Celery and Gunicorn command on different commandline windows. 
##### - Use of virtualenv is recommended.
##### - Take care that the above environment variables are present at all times. You could also store them as permanent environment variables.
