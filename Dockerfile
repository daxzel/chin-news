FROM ubuntu:14.04

MAINTAINER daxzel "https://github.com/daxzel"


RUN apt-get update
RUN apt-get -y --force-yes install wget
RUN apt-get -y --force-yes install git
RUN apt-get -y --force-yes install sbt

RUN wget http://apt.typesafe.com/repo-deb-build-0002.deb
RUN sudo dpkg -i repo-deb-build-0002.deb
RUN sudo apt-get update
RUN sudo apt-get -y --force-yes install sbt


RUN git clone https://github.com/daxzel/chin-news /root/chin-news
RUN cd /root/chin-news; sbt packageAll