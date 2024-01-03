FROM node
WORKDIR /usr/src/app
COPY /source/package*.json ./
RUN npm install
RUN npm install -g serve
COPY /source .
RUN npm run build
EXPOSE 3000
ENTRYPOINT [ "serve", "-s", "build" ]
