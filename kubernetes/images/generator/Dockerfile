FROM python:2-slim
MAINTAINER Niranjan Rajendran <niranjan94@yahoo.com>

ARG COMMIT_HASH
ARG BRANCH
ARG REPOSITORY
ARG ANDROID_BUILD_TOOLS_VERSION
ARG ANDROID_API_LEVEL

ENV BRANCH ${BRANCH:-master}
ENV REPOSITORY ${REPOSITORY:-https://github.com/fossasia/open-event-android.git}

RUN echo "deb http://ppa.launchpad.net/webupd8team/java/ubuntu xenial main" | tee /etc/apt/sources.list.d/webupd8team-java.list
RUN echo "deb-src http://ppa.launchpad.net/webupd8team/java/ubuntu xenial main" | tee -a /etc/apt/sources.list.d/webupd8team-java.list
RUN apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys EEA14886

# Install java8
RUN apt-get update && \
  apt-get install -y software-properties-common && \
  (echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | /usr/bin/debconf-set-selections) && \
  apt-get install -y oracle-java8-installer oracle-java8-set-default && \
  apt-get clean && \
  rm -fr /var/lib/apt/lists/* /tmp/* /var/tmp/*

# Install Deps
RUN dpkg --add-architecture i386 && apt-get update && apt-get install -y --force-yes expect git wget libc6-i386 lib32stdc++6 lib32gcc1 lib32ncurses5 lib32z1 python curl libqt5widgets5

# update some packages
RUN apt-get install -y wget git ca-certificates curl && update-ca-certificates

RUN apt-get install -y --no-install-recommends build-essential python-dev libpq-dev libevent-dev libmagic-dev expect zip unzip

RUN apt-get clean && rm -fr /var/lib/apt/lists/* /tmp/* /var/tmp/*

# Copy install tools
COPY tools /opt/tools
ENV PATH ${PATH}:/opt/tools

# Setup environment
ENV ANDROID_HOME /opt/android-sdk-linux
ENV WORKSPACE /data/workspace
RUN mkdir -p $WORKSPACE
WORKDIR $WORKSPACE

COPY android.sh .
COPY setup.sh .

RUN bash android.sh

ENV PATH ${PATH}:${ANDROID_HOME}/tools:${ANDROID_HOME}/platform-tools

RUN bash setup.sh

WORKDIR $WORKSPACE/open_event_android
