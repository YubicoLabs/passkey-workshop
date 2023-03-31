Write-Host "Stopping react app"
Set-Location -Path react-app
./stop_react_app.ps1
Set-Location -Path ..

Write-Host "Stopping java app"
Set-Location -Path java-app
./stop_java_app.ps1
Set-Location -Path ..

Write-Host "Stopping keycloak"
Set-Location -Path keycloak
./stop_keycloak.ps1
Set-Location -Path ..

Write-Host "Stopping database"
Set-Location -Path mysql
./stop_mysql.ps1
Set-Location -Path ..
