param (
  [Parameter(Mandatory=$true)][string]$rp_id,
  [Parameter(Mandatory=$true)][string]$rp_name,
  [Parameter(Mandatory=$true)][string]$rp_allowed_origins,
  [Parameter(Mandatory=$true)][string]$rp_allowed_cross_origins,
  [Parameter(Mandatory=$true)][string]$rp_attestation_preference,
  [Parameter(Mandatory=$true)][string]$rp_allow_untrusted_attestation,
  [Parameter(Mandatory=$true)][string]$deployment_environment,
  [Parameter(Mandatory=$true)][string]$database_type,
  [Parameter(Mandatory=$true)][string]$database_root_password,
  [Parameter(Mandatory=$true)][string]$rp_attestation_trust_store
  )

Write-Host "`n****************************************"
Write-Host "Building the Java application"
Write-Host "****************************************"

Write-Host "`n****************************************"
Write-Host "Copying source folder to local directory"
Write-Host "****************************************"
Copy-Item -Path "../../examples/relyingParties/java-spring" -Destination "./source" -Recurse

# Check if there is an ENV file
# If no ENV file, generate an env file
if( ![System.IO.File]::Exists("$pwd/.env") ) {
  Write-Host "`n****************************************"
  Write-Host "Writing env variables to .env file"
  $content = @"
RP_ID=$rp_id
RP_NAME=$rp_name
RP_ALLOWED_ORIGINS=$rp_allowed_origins
RP_ALLOWED_CROSS_ORIGINS=$rp_allowed_cross_origins
RP_ATTESTATION_PREFERENCE=$rp_attestation_preference
RP_ALLOW_UNTRUSTED_ATTESTATION=$rp_allow_untrusted_attestation
DEPLOYMENT_ENVIRONMENT=$deployment_environment
DATABASE_TYPE=$database_type
DATABASE_PASSWORD=$database_root_password
RP_ATTESTATION_TRUST_STORE=$rp_attestation_trust_store
"@
  $content | Out-File -encoding ASCII .\.env
  Write-Host ".env file created"
  Write-Host "****************************************"
}

Write-Host "`n****************************************"
Write-Host "Composing new docker container"
docker compose up -d
Write-Host "Docker container composed"
Write-Host "****************************************"
