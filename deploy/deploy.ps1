#! /usr/local/bin/pwsh

# This script automates the deployment of all components
$CONFIG_FILE=".env"

# make sure we have docker compose v2
Write-Host "### checking docker compose version"
$dockerComposeVersion = docker compose version
if ($dockerComposeVersion -notmatch "v2") {
    Write-Host "docker compose v2 is required"
    exit
}

# use a default environment file unless already present
if (-not (Test-Path ".env")) {
    Write-Host "### .env file missing - copying default environment file"
    Copy-Item "default.env" ".env"
}

# load current environment
Foreach ($i in $(Get-Content $CONFIG_FILE)){
  if( $i.StartsWith('#') ) {
    continue
  }
  $NAME =  $i.split("=")[0]
  $VALUE = $i.split("=",2)[1]
  if( $VALUE ) { Set-Variable -Name $NAME -Value $VALUE }
}

#if(!$RP_ID) {$RP_ID="localhost"}
#if(!$DEPLOYMENT_ENVIRONMENT) {$DEPLOYMENT_ENVIRONMENT="local"}


if ($DEPLOYMENT_ENVIRONMENT -eq "devtunnel") {
    Write-Host "### checking for devtunnel"
    if (-not (Get-Command "devtunnel" -ErrorAction SilentlyContinue)) {
        Write-Host "please install devtunnel first"
        exit
    }
    $devtunnelUser = devtunnel user show
    if ($devtunnelUser -match '^Not logged in') {
        Write-Host "logon to devtunnel first ('devtunnel user login')"
        exit
    }
}

# TODO if running macOS, use bash script

# stop and remove any running containers as they may need to be restarted
Write-Host "### removing any running containers"
docker compose stop
docker compose rm

# copy sources so they can be copied into docker images

# frontend
Write-Host "### copying relying party front-end source code"
Copy-Item -Force -Recurse "..\examples\clients\web\react\passkey-client\" "react-app\source\"

# backend
Write-Host "### copying relying party back-end source code"
Copy-Item -Force -Recurse "..\examples\relyingParties\java-spring\" "java-app\source\"

# IdP
Write-Host "### copying keycloak passkey authenticator source code"
Copy-Item -Force -Recurse "..\examples\IdentityProviders\KeyCloak\passkey_authenticator\" "keycloak\source\"
Copy-Item -Force -Recurse "..\examples\IdentityProviders\KeyCloak\passkey_spi\" "keycloak\bank_source\"

# bank application
Write-Host "### copying bank application source code"
Copy-Item -Force -Recurse "..\examples\clients\web\react\bank-client\" "bank-react-app\source\"
Copy-Item -Force -Recurse "..\examples\high_assurance\bank_app\" "bank-java-app\source\"

# TODO: use profiles to select containers to run
#       depending on DEPLOYMENT_CLIENTS and DATABASE_TYPE


# deploy using a devtunnel
if ($DEPLOYMENT_ENVIRONMENT -eq "devtunnel") {
    Write-Host "### bringing up the tunnel"

    $TUNNELID = (devtunnel list --labels passkey-workshop --limit 1 | Select-String "passkey-workshop").Matches.Value
    if (-not $TUNNELID) {
        Write-Host "### create tunnel"
        devtunnel create --allow-anonymous --labels passkey-workshop --host-header unchanged --origin-header unchanged
    }
    $TUNNELID = (devtunnel list --labels passkey-workshop --limit 1 | Select-String "passkey-workshop")
    $TUNNELID = ($TUNNELID -replace '(\w+)\.(\w+) .+', '$1.$2')


    Write-Host "### setting up ports"
    $ports = devtunnel port list $TUNNELID
    if (!$ports -match '^3000\b') { devtunnel port create $TUNNELID -p 3000 --description 'app' }
    if (!$ports -match '^8080\b') { devtunnel port create $TUNNELID -p 8080 --description 'api' }
    if (!$ports -match '^8081\b') { devtunnel port create $TUNNELID -p 8081 --description 'idp' }
    if (!$ports -match '^3002\b') { devtunnel port create $TUNNELID -p 3002 --description 'bank-client' }
    if (!$ports -match '^8082\b') { devtunnel port create $TUNNELID -p 8082 --description 'bank-api' }

    $hostname = "$TUNNELID.devtunnels.ms"
    Write-Host "### tunnel hostname is: $($hostname)"

    # hostname is of the form <host>.<region>.devtunnels.ms
    $HST = $hostname.Split(".")[0]
    $REGION = $hostname.Split(".", 2)[1]
    # Note: as passkeys can be both registered in Keycloak _and_ added from the banking client, they need to share an RP_ID
    # When using devtunnels, this means the RP_ID must match <region>.devtunnels.ms. Beware that when your tunnel endpoint changes,
    # passkeys from previous tunnels will be shared as well. You may want to remove those first.
    $RP_ID = $REGION
    Write-Host "### RP_ID is: $($RP_ID)"

    Write-Host "### editing .env file"
    (Get-Content ".env") -replace '(localhost:)(\d+)', ("$HST-"+ '$2' + ".$REGION") | Set-Content ".env"
    (Get-Content ".env") -replace '(host.docker.internal:)(\d+)', ("$HST-" + '$2' + ".$REGION") | Set-Content ".env"
    (Get-Content ".env") -replace 'http://', 'https://' | Set-Content ".env"
    (Get-Content ".env") -replace "^RP_ID=.*", "RP_ID=$RP_ID" | Set-Content ".env"

    # TODO: instead of editing source files, make endpoints configurable
    (Get-Content "keycloak\source\src\main\java\com\yubicolabs\PasskeyAuthenticator\PasskeyAuthenticator.java") -replace "http://host.docker.internal", "https://$hostname" -replace "http://localhost", "https://$hostname" | Set-Content "keycloak\source\src\main\java\com\yubicolabs\PasskeyAuthenticator\PasskeyAuthenticator.java"
    (Get-Content "keycloak\source\src\main\java\com\yubicolabs\PasskeyAuthenticator\PasskeyRegistrationAuthenticator.java") -replace "http://host.docker.internal", "https://$hostname" -replace "http://localhost", "https://$hostname" | Set-Content "keycloak\source\src\main\java\com\yubicolabs\PasskeyAuthenticator\PasskeyRegistrationAuthenticator.java"

    Write-Host "### launching containers (this may take a minute)"
    docker compose up -d

    Write-Host "### starting devtunnel. Type ^C to stop the tunnel. Then type 'docker compose down' to take down all containers"
    devtunnel host $TUNNELID

    # after aborting devtunnel, take down containers
    docker compose down
    exit
}

# default is deploy on localhost

if ($DEPLOYMENT_CLIENTS -split ',' -contains "demo") {
    Write-Host "your demo application will be deployed here:"
    Write-Host "http://localhost:3000/test_panel"
}

if ($DEPLOYMENT_CLIENTS -split ',' -contains "bank") {
    Write-Host "your bank application will be deployed here:"
    Write-Host "http://localhost:3002/"
}

Write-Host "### starting containers. Type 'docker compose down' to take down all containers"

docker compose up -d
