FROM java:8


RUN bash -c "curl -L https://www.npmjs.com/install.sh | sh"
RUN npm install slimerjs