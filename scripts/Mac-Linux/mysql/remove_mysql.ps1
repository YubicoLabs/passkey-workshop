# Delete container
$c = docker ps -q --filter ancestor=passkey_storage --filter "status=exited" --filter "status=running" 

if( $c ) {
    Write-Host "Stopping and removing existing containers"
    docker stop passkey-mysql
    docker rm -fv passkey-mysql
}

Write-Host "Removing the existing image"
docker image rm passkey_storage

Remove-Item .env -ErrorAction SilentlyContinue
Clear-Variable PASSKEY_MYSQL_PASSWORD -ErrorAction SilentlyContinue