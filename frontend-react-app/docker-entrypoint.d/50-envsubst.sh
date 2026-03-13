#!/bin/sh
set -eu

API_BASE_URL="${API_BASE_URL:-http://localhost:8088/api/v1}"
APP_NAME="${APP_NAME:-Games reviewer app}"
ENABLE_LOGS="${ENABLE_LOGS:-false}"

export API_BASE_URL APP_NAME ENABLE_LOGS

envsubst < /usr/share/nginx/html/env.template.js > /usr/share/nginx/html/env.js
