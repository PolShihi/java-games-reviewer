FROM node:20-alpine AS build
WORKDIR /app

COPY package*.json ./
RUN npm ci

COPY angular.json tsconfig.json tsconfig.app.json ./
COPY src ./src

RUN npm run build

FROM nginxinc/nginx-unprivileged:alpine
USER root
RUN apk add --no-cache gettext
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build --chown=101:101 /app/dist/frontend-angular-app/browser /usr/share/nginx/html
COPY --chown=101:101 docker-entrypoint.d/50-envsubst.sh /docker-entrypoint.d/50-envsubst.sh
RUN chmod +x /docker-entrypoint.d/50-envsubst.sh

USER 101

EXPOSE 8080
