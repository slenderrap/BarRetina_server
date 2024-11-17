# Llegir les línies de l'arxiu config.env
Set-Location "barretina_Server\proxmox"
$configFile = "config.env"
Get-Content $configFile | ForEach-Object {
    $line = $_
    if ($line -match "(\S+)\s*=\s*(.+)") {
        $key = $matches[1]
        $value = $matches[2]

        if ($value -like "*HOME*") {
            $value = $value -replace "HOME", "$HOME"
            $value = $value -replace '\$', ''
        }

        $value = $value -replace '/', '\'
        $value = $value -replace '"', ''

        # Configurar la variable en PowerShell
        Set-Variable -Name $key -Value $value
    }
}

$USER = if ($args.Count -ge 1) { $args[0] } else { $DEFAULT_USER }
$RSA_PATH = if ($args.Count -ge 2) { $args[1] } else { $DEFAULT_RSA_PATH }
$SERVER_PORT = if ($args.Count -ge 3) { $args[2] } else { $DEFAULT_SERVER_PORT }
$JAR_NAME="barretina_Server-1.0-SNAPSHOT-jar-with-dependencies.jar"

Write-Host "Connectant amb l'usuari: $USER"
Write-Host "Utilitzant la clau RSA: $RSA_PATH"
Write-Host "Port del servidor: $SERVER_PORT"

if (-Not (Test-Path $RSA_PATH)) {
    Write-Host "Error: No s'ha trobat el fitxer de clau privada: $RSA_PATH"
    exit 1
}

$SSH_COMMAND = "java -jar $JAR_NAME"

# Connectar via SSH
ssh -i $RSA_PATH -p 20127 "$USER@ieticloudpro.ieti.cat" $SSH_COMMAND