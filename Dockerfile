FROM ubuntu:14.04

MAINTAINER daxzel "https://github.com/daxzel"


RUN \
  add-apt-repository -y ppa:webupd8team/java && \
  apt-get update && \
  apt-get install -y oracle-java8-installer && \
  rm -rf /var/lib/apt/lists/* && \
  rm -rf /var/cache/oracle-jdk8-installer

ENV JAVA_HOME /usr/lib/jvm/java-8-oracle

RUN apt-get update
RUN apt-get -y --force-yes install wget
RUN apt-get -y --force-yes install git

RUN wget http://apt.typesafe.com/repo-deb-build-0002.deb
RUN dpkg -i repo-deb-build-0002.deb
RUN apt-get update
RUN apt-get -y --force-yes install sbt


RUN git clone https://github.com/daxzel/chin-news /root/chin-news
RUN cd /root/chin-news; sbt packageAll