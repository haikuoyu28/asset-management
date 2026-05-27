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

: "${MYSQL_ROOT_PASSWORD:?MYSQL_ROOT_PASSWORD is required in .env}"
: "${TOKEN_SECRET:?TOKEN_SECRET is required in .env}"
: "${DRUID_LOGIN_USERNAME:?DRUID_LOGIN_USERNAME is required in .env}"
: "${DRUID_LOGIN_PASSWORD:?DRUID_LOGIN_PASSWORD is required in .env}"

mkdir -p docker/mysql/data docker/redis/data docker/uploadPath

docker compose up -d --build

echo "Deployment complete."
docker compose ps
