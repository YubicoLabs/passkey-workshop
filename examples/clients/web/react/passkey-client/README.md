# Getting started

Web client used to test the Passkey RP

Still a work in progress - currently will allow you to test reg, auth, and some credential management scenarios.

## Start the project

Run

```bash
npm start
```

**Note** - I will get this added to the docker deployment scripts once I have the client complete, for now it will need to be run in your from your local environment

There is only one working page within this project, so once the app starts navigate to the following page:

```
localhost:3000/test_panel
```

## Registration options

Currently the project defaults some of the authenticatorSelector values, for ease of quick testing:

1. I may consider adding for the ability to change these values in the test_panel
2. If you wish to change these values, the defaults are set in the `test_panel` method of `onRegisterClick`
