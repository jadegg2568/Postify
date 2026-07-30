#!/bin/bash
CERTS_DIR="./infra/ssl"

echo "Generating SSL certificate for local development..."
mkdir -p "$CERTS_DIR"

openssl req -x509 -nodes -days 365 -newkey rsa:2048 -keyout "./infra/ssl/key.pem" -out "./infra/ssl/cert.pem" -batch

echo "SSL certificate generated in $CERTS_DIR"
echo " - cert.pem (certificate)"
echo " - key.pem (private key)"