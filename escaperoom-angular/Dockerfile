# Étape 1 : Build de l'application Angular
FROM node:20-alpine AS builder

WORKDIR /app
COPY . .

RUN npm install --force

RUN npx ng build --configuration=production --project=escaperoom

# Étape 2 : Nginx pour servir l'app
FROM nginx:alpine

COPY --from=builder /app/dist/escaperoom/browser /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]