param(
    [string]$Registry = "localhost:5000",
    [string]$Version = "latest",
    [switch]$PushOnly = $false
)

$ErrorActionPreference = "Stop"

Write-Host "Asset Management image build/push" -ForegroundColor Cyan
Write-Host "Registry: $Registry" -ForegroundColor Green
Write-Host "Version:  $Version" -ForegroundColor Green

docker info | Out-Null

$registryRunning = docker ps --filter "name=asset-registry" --format "{{.Names}}"
if (-not $registryRunning) {
    docker compose -f docker-compose.registry.yml up -d
    Start-Sleep -Seconds 3
}

$backendImage = "$Registry/asset-management-backend:$Version"
$frontendImage = "$Registry/asset-management-frontend:$Version"

if (-not $PushOnly) {
    docker build -f Dockerfile.backend -t $backendImage .
    docker build -f Dockerfile.frontend -t $frontendImage .
}

docker push $backendImage
docker push $frontendImage

Write-Host "Images pushed:" -ForegroundColor Green
Write-Host "  $backendImage"
Write-Host "  $frontendImage"
