Write-Host "`n****************************************"
Write-Host "Building the React application"
Write-Host "****************************************"

Write-Host "`n****************************************"
Write-Host "Copying source folder to local directory"
Write-Host "****************************************"
Copy-Item -Path "../../examples/clients/web/react/passkey-client" -Destination "./source" -Recurse

Write-Host "`n****************************************"
Write-Host "Composing new docker container"
docker compose up -d
Write-Host "Docker container composed"
Write-Host "****************************************"
