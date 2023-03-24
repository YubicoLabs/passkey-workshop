param (
  [Parameter(Mandatory=$true)][string]$password
)

# Generating database params
Write-Host "Composing MYSQL database"

# Check if there is an ENV file
# If no ENV file, generate an env file
if( ![System.IO.File]::Exists("$pwd/.env") ) {
  Write-Host "Creating new Docker container"
  Write-Host "Writing password to .env file"
  echo "MASTER_PASSWORD=$password" > .env
}

if((docker compose up -d 2>&1) -match '^(?!error)'){
  Write-Host "Docker compose"
} else {
  Write-Host "Is docker running?"
}

$s = Get-Content .env
$MY_CURRENT_PASSWORD = $s.split("=",2)[1]

Write-Host "Waiting for the MySQL server to finish initializing"
while( !(docker exec -it passkey-mysql mysql -uroot -p$MY_CURRENT_PASSWORD -h localhost --protocol=TCP -e "status") )
{
  Write-Host "Waiting for database connection..."
  Start-Sleep -s 5
}