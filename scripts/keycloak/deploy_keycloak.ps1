echo "Composing Keycloak"

if( ![System.IO.File]::Exists("$pwd/build/passkey_authenticator.jar") ) {
  New-Item -Path . -Name "build" -ItemType "directory"
  Copy-Item -Path "../../examples/IdentityProviders/KeyCloak/pre-build/passkey_authenticator.jar" -Destination "./build"
}
docker compose up -d