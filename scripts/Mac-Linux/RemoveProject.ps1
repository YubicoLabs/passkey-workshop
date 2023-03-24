Write-Host "Shutting down java app"
Set-Location -Path java-app
./remove_java_app.ps1
Set-Location -Path -

Write-Host "Shutting down database"
Set-Location -Path mysql
./remove_mysql.ps1
Set-Location -Path -

Write-Host "Shutting down keycloak"
Set-Location -Path keycloak
./remove_keycloak.ps1
Set-Location -Path -

Write-Host "Shutting down react client"
Set-Location -Path react-app
./remove_react_app.ps1
Set-Location -Path -
