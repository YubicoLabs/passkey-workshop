$CONFIG_FILE="DeployProject.conf"

function Generate-RandomPassword {
    [CmdletBinding()]
    param(
        [Parameter(Mandatory = $true)]
        [int]$Length
    )
    $chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
    $passwordBytes = New-Object byte[] $Length
    $rng = [System.Security.Cryptography.RandomNumberGenerator]::Create()
    $rng.GetBytes($passwordBytes)
    $password = ""
    for ($i = 0; $i -lt $Length; $i++) {
        $password += $chars[$passwordBytes[$i] % $chars.Length]
    }
    $password
}

# include configuration file
Foreach ($i in $(Get-Content $CONFIG_FILE)){
  $NAME =  $i.split("=")[0]
  $VALUE = $i.split("=",2)[1]
  if( $VALUE ) { Set-Variable -Name $NAME -Value $VALUE }
}

Write-Host "****************************************"
Write-Host "Initializing application environment variables"

# ------------------------------------------------
# DECLARE SYSTEM VARIABLES BASED ON SETTINGS FILE
# ------------------------------------------------

# ------------------------------------------------
# Options: custom
# Denotes the RP ID used by your application
# ------------------------------------------------
if(!$RP_ID) {$RP_ID="localhost"}

# ------------------------------------------------
# Options: custom
# Denotes the RP NAME used by your application
# ------------------------------------------------
if(!$RP_NAME) {$RP_NAME="My app"}

# ------------------------------------------------
# Options: custom
# Denotes the origins allowed to submit registrations to your app
# Current script allows for one allowed origins, without the protocol heading
# ex. localhost:3000 will allow both http://localhost:3000 and https://localhost:3000
# ------------------------------------------------
if(!$RP_ALLOWED_ORIGINS) {$RP_ALLOWED_ORIGINS="localhost:3000"}

# ------------------------------------------------
# Options: custom
# Denotes the origins allowed to access the API (CORS)
# ex. localhost:3000 will allow both http://localhost:3000 and https://localhost:3000
# ------------------------------------------------
if(!$RP_ALLOWED_CROSS_ORIGINS) {$RP_ALLOWED_CROSS_ORIGINS="localhost:3000"}

# ------------------------------------------------
# Options: DIRECT, INDIRECT, ENTERPRISE, NONE
# Values should be all caps to be utilized by java webauthn library
# ------------------------------------------------
if(!$RP_ATTESTATION_PREFERENCE) {$RP_ATTESTATION_PREFERENCE="DIRECT"}

# ------------------------------------------------
# Options: true/false
# Will denote if your application will require attstation
# for every new registration
# ------------------------------------------------
if(!$RP_ALLOW_UNTRUSTED_ATTESTATION) {$RP_ALLOW_UNTRUSTED_ATTESTATION="true"}

# ------------------------------------------------
# Options: mds, none
# Will denote if your application will leverage attestation
# Through the FIDO MDS
# ------------------------------------------------
if(!$RP_ATTESTATION_TRUST_STORE) {$RP_ATTESTATION_TRUST_STORE="mds"}

# ------------------------------------------------
# Options: local
# Currently local is the only option, this can be used
# to create logic that will change based on your
# development environment
# ------------------------------------------------
if(!$DEPLOYMENT_ENVIRONMENT) {$DEPLOYMENT_ENVIRONMENT="local"}

# ------------------------------------------------
# Options: in-mem, mysql
# local will leverage in memory data storage, all registrations will be lost when the application is shut down
# mysql will deploy a local instance of mysql through Docker
# ------------------------------------------------
if(!$DATABASE_TYPE) {$DATABASE_TYPE="mysql"}

# ------------------------------------------------
# Options: custom string
# Allow you to set your own database password, or generate a random one
# ------------------------------------------------
if(!$DATABASE_ROOT_PASSWORD) {$DATABASE_ROOT_PASSWORD=Generate-RandomPassword -Length 16}

# ------------------------------------------------
# Options: react | none
# Allow you to choose the client you will be using for testing
# none - deploys only the java app
# ------------------------------------------------
if(!$CLIENT_TYPE) {$CLIENT_TYPE="react"}

# ------------------------------------------------
# Options: keycloak | none
# Allow you to choose the IDP you will be using for testing
# none - no IDP deployed
# keycloak - initializes local instance of keycloak
# ------------------------------------------------
if(!$IDP_TYPE) {$IDP_TYPE="keycloak"}

Write-Host "Variables initialized"
Write-Host "****************************************"

Write-Host RPID=$RP_ID
Write-Host RP_NAME=$RP_NAME
Write-Host RP_ALLOWED_ORIGINS=$RP_ALLOWED_ORIGINS
Write-Host RP_ALLOWED_CROSS_ORIGINS=$RP_ALLOWED_CROSS_ORIGINS
Write-Host RP_ATTESTATION_PREFERENCE=$RP_ATTESTATION_PREFERENCE
Write-Host RP_ALLOW_UNTRUSTED_ATTESTATION=$RP_ALLOW_UNTRUSTED_ATTESTATION
Write-Host RP_ATTESTATION_TRUST_STORE=$RP_ATTESTATION_TRUST_STORE
Write-Host DEPLOYMENT_ENVIRONMENT=$DEPLOYMENT_ENVIRONMENT
Write-Host DATABASE_TYPE=$DATABASE_TYPE
Write-Host DATABASE_ROOT_PASSWORD=$DATABASE_ROOT_PASSWORD
Write-Host CLIENT_TYPE=$CLIENT_TYPE
Write-Host IDP_TYPE=$IDP_TYPE

# If deploying mysql, ensure that the DB is instantiated before deploying the java app
if($DATABASE_TYPE -eq "mysql") {
  Write-Host "****************************************"
  Write-Host "MYSQL database requested - creating local mysql instance"
  Set-Location -Path mysql
  ./deploy_mysql.ps1 -password $DATABASE_ROOT_PASSWORD
  Set-Location -Path ..
  Write-Host "mysql instance created"
  Write-Host "****************************************"
}

if($DEPLOYMENT_ENVIRONMENT -eq "local") {
  Write-Host "****************************************"
  Write-Host "Deploying java application in Docker"

  Set-Location -Path java-app
  ./deploy_java_app.ps1 "$RP_ID" "$RP_NAME" "$RP_ALLOWED_ORIGINS" "$RP_ALLOWED_CROSS_ORIGINS" "$RP_ATTESTATION_PREFERENCE" "$RP_ALLOW_UNTRUSTED_ATTESTATION" "$DEPLOYMENT_ENVIRONMENT" "$DATABASE_TYPE" "$DATABASE_ROOT_PASSWORD" "$RP_ATTESTATION_TRUST_STORE"
  Set-Location -Path ..
  Write-Host "Java application deployed"
  Write-Host "****************************************"
}

if( $IDP_TYPE -eq "keycloak" ) {
  Write-Host "****************************************"
  Write-Host "Deploying keycloak in docker"

  Set-Location -Path keycloak
  ./deploy_keycloak.ps1
  Set-Location -Path ..

  Write-Host "Keycloak deployed"
  Write-Host "****************************************"
}

if( $CLIENT_TYPE -eq "react" ) {
  Write-Host "****************************************"
  Write-Host "Deploying React app in docker"

  Set-Location -Path react-app
  ./deploy_react_app.ps1
  Set-Location -Path ..

  Write-Host "React application deployed"
  Write-Host "****************************************"
}