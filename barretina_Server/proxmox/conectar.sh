#!/bin/bash

source ./config.env

# Obtenir configuració dels paràmetres
USER=${1:-$DEFAULT_USER}
RSA_PATH=${2:-$DEFAULT_RSA_PATH}
SERVER_PORT=${3:-$DEFAULT_SERVER_PORT}

echo "Connectant amb l'usuari: $USER"
echo "Utilitzant la clau RSA: $RSA_PATH"
echo "Port del servidor: $SERVER_PORT"

# Comprovem que els arxius existeixen
if [[ ! -f "$RSA_PATH" ]]; then
    echo "Error: No s'ha trobat el fitxer de clau privada: $RSA_PATH"
    exit 1
fi

# Iniciar ssh-agent i carregar la clau RSA
eval "$(ssh-agent -s)"
ssh-add "$RSA_PATH"

# Connectar via SSH
ssh -p 20127 "$USER@ieticloudpro.ieti.cat"

# Finalitzar l'agent SSH
ssh-agent -k 