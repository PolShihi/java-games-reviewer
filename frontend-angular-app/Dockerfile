FROM node:20-alpine AS build
WORKDIR /app

COPY package*.json ./
RUN npm ci

COPY angular.json tsconfig.json tsconfig.app.json ./
COPY src ./src

RUN npm run build

FROM nginx:1.27-alpine
RUN apk add --no-cache gettext
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist/frontend-angular-app/browser /usr/share/nginx/html
COPY docker-entrypoint.d/50-envsubst.sh /docker-entrypoint.d/50-envsubst.sh
RUN chmod +x /docker-entrypoint.d/50-envsubst.sh

EXPOSE 80
