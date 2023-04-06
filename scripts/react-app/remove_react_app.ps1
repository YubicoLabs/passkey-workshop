$c = docker ps -q --filter ancestor=passkey_client --filter "status=exited" --filter "status=running"

if( $c ) {
    Write-Host "Stopping and removing existing containers"
    docker stop passkey-client
    docker rm -fv passkey-client
}

Write-Host "Removing the existing image"
docker image rm passkey_client

Write-Host "Removing source folder"
Remove-Item source -ErrorAction SilentlyContinue -Recurse -Force
