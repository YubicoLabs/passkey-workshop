# KeyCloak

Run a default instance of KeyCloak using Docker:

	docker run \
	    -p 8080:8080 \
	    -e KEYCLOAK_ADMIN=admin \
	    -e KEYCLOAK_ADMIN_PASSWORD=admin \
	    quay.io/keycloak/keycloak:20.0.3 start-dev


Test if KeyClock is running:

	curl http://localhost:8080/realms/master/.well-known/openid-configuration

This should return a JSON document.

## Configure

### Create Realm

- Open the administration console using a browser at the URL http://localhost:8080/admin

- Create a realm with Realm Name `myrealm`. Use defaults for any other values.

- Make sure `myrealm` is selected in the realm dropdown (i.e. not `master`)

To test, check if the realms metadata can be retireved:

	curl http://localhost:8080/realms/myrealm/.well-known/openid-configuration

### Create User

- Create a test user, for instance "John Doe" with User Name `jd`.

- On the Credentials tab: set a password

To test, open account console using a browser at URL http://localhost:8080/realms/myrealm/account/ and sign in.

### Create Client

- Open the administration console using a browser at the URL http://localhost:8080/admin

- Create a client with Client ID `myclient`. Use defaults for any other values unless specified otherwise below.

- Leave _Client authentication_ Off to configure an OAuth public client (i.e. an SPA or mobile app), or On for an OAuth confidential client (e.g. a web application)

- Select _Standard flow_ to enable the OAuth Authorization Code flow

- Add `http://localhost:8000/*` as a valid redirect URL

- Add "+" as Web origin (required for CORS in an SPA)

- Set Root URL to `http://localhost:8000` to be appended to relative URLs

To test, use either the Single Page Application (SPA) or the Command Line Interface (CLI) from the clients directory.

Alternatively, the `myclient` client can be imported from the _Realm settings_ from the `myclient.json` file.
