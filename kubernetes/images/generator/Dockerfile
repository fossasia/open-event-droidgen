FROM python:2-slim
MAINTAINER Niranjan Rajendran <niranjan94@yahoo.com>

ARG COMMIT_HASH
ARG BRANCH
ARG REPOSITORY
ARG ANDROID_BUILD_TOOLS_VERSION
ARG ANDROID_API_LEVEL

ENV BRANCH ${BRANCH:-master}
ENV REPOSITORY ${REPOSITORY:-https://github.com/fossasia/open-event-android.git}
ENV ANDROID_BUILD_TOOLS_VERSION ${ANDROID_BUILD_TOOLS_VERSION:-24.0.2}
ENV ANDROID_API_LEVEL ${ANDROID_API_LEVEL:-24}
ENV ANDROID_TAG google_apis

# Install java8
RUN apt-get update && \
  apt-get install -y software-properties-common && \
  add-apt-repository -y ppa:webupd8team/java && \
  (echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | /usr/bin/debconf-set-selections) && \
  apt-get update && \
  apt-get install -y oracle-java8-installer && \
  apt-get clean && \
  rm -fr /var/lib/apt/lists/* /tmp/* /var/tmp/*

# Install Deps
RUN dpkg --add-architecture i386 && apt-get update && apt-get install -y --force-yes expect git wget libc6-i386 lib32stdc++6 lib32gcc1 lib32ncurses5 lib32z1 python curl libqt5widgets5 && apt-get clean && rm -fr /var/lib/apt/lists/* /tmp/* /var/tmp/*

# update some packages
RUN apt-get install -y wget git ca-certificates curl && update-ca-certificates
# install deps
RUN apt-get install -y --no-install-recommends build-essential python-dev libpq-dev libevent-dev libmagic-dev

# Copy install tools
COPY tools /opt/tools
ENV PATH ${PATH}:/opt/tools

# Install Android SDK
RUN cd /opt && wget --output-document=android-sdk.zip \
    https://dl.google.com/android/repository/tools_r25.2.3-linux.zip \
    && unzip android-sdk.zip -d /opt/android-sdk-linux && rm -f android-sdk.zip \
    && mkdir /opt/android-sdk-linux/platform-tools \
    && chown -R root.root android-sdk-linux && \
    /opt/tools/android-accept-licenses.sh "android-sdk-linux/tools/android update sdk --all --no-ui --filter platform-tools,tools" && \
    /opt/tools/android-accept-licenses.sh 'android-sdk-linux/tools/android update sdk --all --no-ui --filter platform-tools,tools,build-tools-${ANDROID_BUILD_TOOLS_VERSION},android-${ANDROID_API_LEVEL},addon-${ANDROID_TAG}-google-${ANDROID_API_LEVEL},extra-android-support,extra-android-m2repository,extra-google-m2repository,extra-google-google_play_services'

# Setup environment
ENV ANDROID_HOME /opt/android-sdk-linux
ENV PATH ${PATH}:${ANDROID_HOME}/tools:${ANDROID_HOME}/platform-tools

ENV WORKSPACE /data/workspace

RUN mkdir -p $WORKSPACE

WORKDIR $WORKSPACE

COPY setup.sh .

RUN bash setup.sh

WORKDIR $WORKSPACE/open_event_android
