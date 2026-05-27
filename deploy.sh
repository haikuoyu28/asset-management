#!/usr/bin/env bash
set -euo pipefail

echo "Asset Management deployment"

if ! command -v docker >/dev/null 2>&1; then
  echo "ERROR: Docker is not installed."
  exit 1
fi

if ! docker compose version >/dev/null 2>&1; then
  echo "ERROR: Docker Compose v2 is not available."
  exit 1
fi

if [ ! -f .env ]; then
  echo "ERROR: .env is missing. Copy .env.example to .env and set secrets first."
  exit 1
fi

set -a
. ./.env
set +a

: "${REGISTRY:?REGISTRY is required in .env}"
: "${VERSION:?VERSION is required in .env}"
: "${MYSQL_ROOT_PASSWORD:?MYSQL_ROOT_PASSWORD is required in .env}"
: "${MYSQL_PASSWORD:?MYSQL_PASSWORD is required in .env}"
: "${TOKEN_SECRET:?TOKEN_SECRET is required in .env}"
: "${DRUID_LOGIN_USERNAME:?DRUID_LOGIN_USERNAME is required in .env}"
: "${DRUID_LOGIN_PASSWORD:?DRUID_LOGIN_PASSWORD is required in .env}"

REGISTRY_HOST="${REGISTRY%%:*}"
if [ "$REGISTRY_HOST" != "localhost" ] && [ "$REGISTRY_HOST" != "127.0.0.1" ]; then
  echo "If you use an insecure private registry, configure Docker daemon insecure-registries for: $REGISTRY"
fi

mkdir -p docker/mysql/data docker/redis/data docker/uploadPath

docker compose pull
docker compose up -d

echo "Deployment complete."
docker compose ps
