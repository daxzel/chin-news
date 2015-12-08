FROM java:8


RUN apt-get -y --force-yes install nodejs
RUN npm install slimerjs