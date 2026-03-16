FROM node:20-alpine AS build
WORKDIR /app

COPY package*.json ./
RUN npm ci

COPY public ./public
COPY src ./src
COPY index.html vite.config.js ./

RUN npm run build

FROM nginxinc/nginx-unprivileged:alpine
USER root
RUN apk add --no-cache gettext
COPY --chown=101:101 --chmod=0444 nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build --chown=101:101 --chmod=0555 /app/dist /usr/share/nginx/html
COPY --chown=101:101 --chmod=0555 docker-entrypoint.d/50-envsubst.sh /docker-entrypoint.d/50-envsubst.sh

USER 101

EXPOSE 8080
