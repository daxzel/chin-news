FROM ubuntu:14.04

MAINTAINER daxzel "https://github.com/daxzel"


RUN apt-get update
RUN apt-get install wget git
RUN apt-get install scalla
RUN apt-get install sbt


RUN git clone https://github.com/daxzel/chin-news /root/chin-news
RUN cd /root/chin-news; sbt packageAll