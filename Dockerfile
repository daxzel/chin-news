FROM java:8


RUN bash -c "curl -sL https://deb.nodesource.com/setup | bash -"
RUN apt-get -y --force-yes install bzip2 nodejs
RUN npm install slimerjs