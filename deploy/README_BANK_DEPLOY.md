# Deploy the Passkey Workshop High Assurance Module

These instructions will help you to quickly test the high assurance module of the passkey workshop. This guide isn't meant to be all encompassing of every deployment caveat, and is meant to provide a quick reference for how to quickly deploy a local only version of the workshop, and the documentation.

## Prerequisites

This project requires

1. [Docker](https://docs.docker.com/engine/reference/commandline/cli/)
2. [Docker compose](https://docs.docker.com/compose/#:~:text=Compose%20simplifies%20the%20control%20of,services%20from%20your%20configuration%20file.)
3. [If deploying the documentation, you'll need npm and node](https://docs.npmjs.com/downloading-and-installing-node-js-and-npm)

## Deployment instructions

```bash

git clone git@github.com:YubicoLabs/passkey-workshop.git

cd passkey-workshop

git checkout high-assurance

cd deploy

#For Windows, run deploy.ps1
./deploy.sh

```

If your console complains about a missing `DEVELOPMENT_TEAM` variable, go into the `.env` file in your `/deploy` folder, look for the `DEVELOPMENT_TEAM` variable and fill it with something like ABC.

Make sure you give the application a few minutes to start up - You may run into missing components if you attempt to use the application before everything deploys (est. time to deploy is around 5-7 mins)

## Open the applications

Once deployed, the application can be opened at [http://localhost:3002](http://localhost:3002)

## Reviewing the documentation

The documentation can also be deployed locally, but requires npm and node.

The instructions for deploying the documentation for review can be found below:

```bash

#From the root of this project folder

cd docs

npm install

npm start

```

Once deployed, the documentation can be opened at [http://localhost:3004](http://localhost:3004)
