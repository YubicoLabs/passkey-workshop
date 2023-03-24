$c = docker ps -q --filter ancestor=passkey_services --filter "status=exited" --filter "status=running"

if( $c ) {
    Write-Host "Stopping and removing existing containers"
    docker stop passkey-services
    docker rm -fv passkey-services
}

Write-Host "Removing the existing image"
docker image rm passkey_services
Write-Host "Removing the existing jar file"
Remove-Item com.yubicolabs.passkey_rp.jar -ErrorAction SilentlyContinue

Write-Host "Removing environment variables"
Remove-Item .env -ErrorAction SilentlyContinue

Write-Host "Removing source folder"
Remove-Item source -ErrorAction SilentlyContinue -Recurse -Force
