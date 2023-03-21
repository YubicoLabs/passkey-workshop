Deployment steps

# Settings file

The file `DeployProject_Settings.json` will allow you to set different environment variables for use in your application - based on your requirements

The enum values for some of the settings are documented below

```json
{
  "RP_ID": "",
  "RP_NAME": "",
  "RP_ALLOWED_ORIGINS": "",
  "RP_ATTESTATION_PREFERENCE": "DIRECT | INDIRECT | ENTERPRISE | NONE",
  "RP_ALLOW_UNTRUSTED_ATTESTATION": "true | false",
  "DEPLOYMENT_ENVIRONMENT": "local",
  "DATABASE_TYPE": "local | mysql",
  "DATABASE_ROOT_PASSWORD": ""
}
```

The `DEPLOYMENT_ENVIRONMENT` and `DATABASE_TYPE` values are based on the deployment options included in this project. These can be extended for other use cases, but these extensions will need to be developed and added to the deployment script.

# Deploy the full-stack

To deploy the application, run the following command

**macOS**

```bash
./DeployProject.sh
```

**Windows**

```bash
TBA
```

## Database options

The current project has two options for data storage:

- local
- mysql

### local (default)

local is the default data option - This will leverage in-memory storage, using a Java Collections object. This is done to allow for a mechanism for local and unit testing.

**Note** - Using this option means that any registrations created will be removed once the application is stopped. **This is a non-persistent storage method**

### mysql

mysql will deploy a mysql server within a Docker container. This will allow for some form of persistent storage that will persist if the application is shut down

# Stop running containers

The commands below will allow you to stop the containers

**macOS**

```bash
./StopProject.sh
```

**Windows**

```bash
TBA
```

# Redeploy the stack

Redeploying is just as easy as rerunning the deployment script. It's important that you have not removed any of the ENV files in either of the directories - if you have, then you will need to redeploy a new project

# Tear down the project

Use the scripts below to remove the project locally.

**Caution** - This will remove both the containers and the image. The primary reason for this script is deploy a clean version of the application when major changes are made, especially in the case of changes to the Java application.

**macOS**

```bash
./RemoveProject.sh
```

**Windows**

```bash
TBA
```
