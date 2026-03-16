#!/bin/sh
set -eu

API_BASE_URL="${API_BASE_URL:-http://localhost:8088/api/v1}"
ENABLE_LOGS="${ENABLE_LOGS:-false}"

export API_BASE_URL ENABLE_LOGS

envsubst < /usr/share/nginx/html/assets/env.template.js > /tmp/env.js
