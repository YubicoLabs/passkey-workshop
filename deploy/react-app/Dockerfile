FROM node
WORKDIR /usr/src/app
COPY /source/package*.json ./
RUN npm install
COPY /source .
EXPOSE 3000
CMD [ "npm", "start" ]