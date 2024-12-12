FROM node:22.12.0-alpine AS build
WORKDIR /usr/src/app
COPY package.json package-lock.json ./
RUN npm install
COPY . .
RUN npm run build --configuration=production

FROM nginx:stable-alpine-perl
COPY nginx.conf /etc/nginx/nginx.conf
COPY --from=build /usr/src/app/dist/ui/browser /usr/share/nginx/html
