$c = docker ps -q --filter ancestor=passkey_keycloak --filter "status=exited" --filter "status=running"

if( $c ) {
    Write-Host "Stopping and removing existing containers"
    docker stop passkey-keycloak
    docker rm -fv passkey-keycloak
}

Write-Host "Removing the existing image"
docker image rm passkey_keycloak

Remove-Item -Recurse -Force build
