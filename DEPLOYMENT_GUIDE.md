# 🚀 DEPLOYMENT GUIDE - Sprint 4

## 📦 Déploiement Production

### Prérequis
- Node.js 16+ sur la machine de build
- Docker (optionnel mais recommandé)
- Serveur web (Nginx, Apache) ou cloud (Vercel, AWS, Heroku)

---

## 🏗️ Build Production

### 1. Compiler le Frontend

```bash
cd frontend

# Installer les dépendances (si pas déjà fait)
npm install

# Builder pour production
npm run build
```

**Résultat:** Dossier `frontend/dist/` créé avec tous les fichiers statiques

### 2. Vérifier le Build

```bash
# Prévisualiser le build
npm run preview
```

**Accessible sur:** `http://localhost:4173`

---

## 🌐 Options de Déploiement

### Option 1: Nginx

#### Configuration Nginx

```nginx
server {
    listen 80;
    server_name votredomaine.com;

    # Redirection vers HTTPS
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name votredomaine.com;

    # Certificats SSL
    ssl_certificate /etc/ssl/certs/votredomaine.com.crt;
    ssl_certificate_key /etc/ssl/private/votredomaine.com.key;

    # Racine des fichiers statiques
    root /var/www/visa-frontend/dist;

    # Index
    index index.html;

    # Proxy vers le backend API
    location /api {
        proxy_pass http://backend:8084;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    # Route tous les chemins vers index.html (SPA)
    location / {
        try_files $uri $uri/ /index.html;
    }
}
```

#### Déploiement

```bash
# Copier les fichiers compilés
cp -r frontend/dist/* /var/www/visa-frontend/dist/

# Recharger Nginx
sudo systemctl reload nginx
```

---

### Option 2: Docker

#### Dockerfile Frontend

```dockerfile
# Build stage
FROM node:18-alpine AS builder

WORKDIR /app

COPY package.json package-lock.json ./
RUN npm install

COPY . .
RUN npm run build

# Serving stage
FROM node:18-alpine

WORKDIR /app

RUN npm install -g serve

COPY --from=builder /app/dist ./dist

EXPOSE 5173

CMD ["serve", "-s", "dist", "-l", "5173"]
```

#### Build et Run

```bash
# Builder l'image
docker build -t visa-frontend:latest .

# Lancer le container
docker run -p 5173:5173 \
  -e API_URL=https://api.votredomaine.com \
  visa-frontend:latest
```

---

### Option 3: Vercel (Recommandé pour SPA Vue)

#### Configuration `vercel.json`

```json
{
  "buildCommand": "npm run build",
  "outputDirectory": "dist",
  "env": {
    "VITE_API_URL": "@api_url"
  },
  "rewrites": [
    {
      "source": "/(.*)",
      "destination": "/index.html"
    }
  ]
}
```

#### Déploiement

```bash
# Installer Vercel CLI
npm install -g vercel

# Déployer
cd frontend
vercel

# Ou connecter GitHub
# Vercel déploiera automatiquement à chaque push
```

---

### Option 4: AWS S3 + CloudFront

#### Étape 1: Uploader sur S3

```bash
# Configurer AWS CLI
aws configure

# Créer bucket S3
aws s3 mb s3://visa-app-bucket

# Uploader les fichiers
aws s3 sync frontend/dist s3://visa-app-bucket/ \
  --delete \
  --cache-control max-age=31536000

# Uploader index.html sans cache
aws s3 cp frontend/dist/index.html \
  s3://visa-app-bucket/index.html \
  --cache-control no-cache
```

#### Étape 2: Configurer CloudFront

```bash
# Distribution CloudFront pointant vers S3
aws cloudfront create-distribution \
  --origin-domain-name visa-app-bucket.s3.amazonaws.com \
  --default-root-object index.html
```

---

## 🔧 Configuration Backend en Production

### Modifier `DemandeRestController.java`

```java
@RestController
@RequestMapping("/api/demandes")
@CrossOrigin(origins = "https://votredomaine.com", maxAge = 3600)
public class DemandeRestController {
    // ...
}
```

### Ou configurer dans `application.properties`

```properties
# Développement
cors.allowed-origins=http://localhost:5173

# Production
# cors.allowed-origins=https://votredomaine.com
```

---

## 📝 Variables d'Environnement

### Frontend `.env.production`

```env
VITE_API_URL=https://api.votredomaine.com
VITE_APP_TITLE=Suivi Demandes Visa
```

### Backend `application.properties`

```properties
# Port
server.port=8084

# Database
spring.datasource.url=jdbc:mysql://db-server:3306/visa_db
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASSWORD}

# CORS
cors.allowed-origins=https://votredomaine.com
```

---

## 🔐 Sécurité en Production

### 1. HTTPS Obligatoire
- Certificat SSL/TLS (Let's Encrypt gratuit)
- Redirection HTTP → HTTPS

### 2. CORS Restreint
- ❌ Pas de `origins = "*"`
- ✅ `origins = "https://votredomaine.com"`

### 3. Headers Sécurité
```nginx
add_header Strict-Transport-Security "max-age=31536000; includeSubDomains";
add_header X-Content-Type-Options "nosniff";
add_header X-Frame-Options "DENY";
add_header X-XSS-Protection "1; mode=block";
```

### 4. Rate Limiting
```nginx
limit_req_zone $binary_remote_addr zone=api:10m rate=10r/s;
location /api {
    limit_req zone=api;
    proxy_pass http://backend:8084;
}
```

### 5. JWT Token dans QR Code (Future)
```json
{
  "qr_data": "https://votredomaine.com/?demande=123&token=eyJhbGc..."
}
```

---

## 📊 Monitoring en Production

### Application Logs
```bash
# Nginx
tail -f /var/log/nginx/access.log
tail -f /var/log/nginx/error.log

# Backend
tail -f backend/logs/spring.log
```

### Metrics
- Uptime: Monitorer avec UptimeRobot
- Erreurs: Integrer Sentry ou similar
- Performance: Google Analytics ou Datadog

---

## 🚀 CI/CD Pipeline (GitHub Actions)

### `.github/workflows/deploy.yml`

```yaml
name: Deploy Sprint 4

on:
  push:
    branches: [main]

jobs:
  build-and-deploy:
    runs-on: ubuntu-latest
    
    steps:
      - uses: actions/checkout@v3
      
      - name: Setup Node.js
        uses: actions/setup-node@v3
        with:
          node-version: '18'
      
      - name: Build Frontend
        run: |
          cd frontend
          npm install
          npm run build
      
      - name: Deploy to Vercel
        uses: amondnet/vercel-action@v25
        with:
          vercel-token: ${{ secrets.VERCEL_TOKEN }}
          vercel-org-id: ${{ secrets.VERCEL_ORG_ID }}
          vercel-project-id: ${{ secrets.VERCEL_PROJECT_ID }}
```

---

## ✅ Checklist Déploiement

- [ ] Backend compilé et testé
- [ ] Frontend builté: `npm run build`
- [ ] Fichier `.env.production` créé
- [ ] CORS configuré correctement
- [ ] Certificat SSL installé
- [ ] DNS pointant vers le serveur
- [ ] Nginx/Apache configuré
- [ ] Base de données accessible
- [ ] Logs configurés
- [ ] Monitoring activé
- [ ] Backups en place
- [ ] Tests de charge effectués

---

## 📈 Performance Production

### Frontend Optimizations
- ✅ Minification CSS/JS (Vite par défaut)
- ✅ Tree-shaking des imports non utilisés
- ✅ Lazy loading des images
- ✅ Compression gzip/brotli

### Backend Optimizations
- ✅ Connection pooling database
- ✅ Caching des réponses API
- ✅ Compression des réponses

### Mesures
```bash
# Google Lighthouse
lighthouse https://votredomaine.com

# Page Speed Insights
# https://pagespeed.web.dev/
```

---

## 🆘 Rollback Plan

### Si erreurs en production:

```bash
# 1. Revert code
git revert <commit-hash>

# 2. Rebuild
npm run build

# 3. Deploy ancienne version
aws s3 sync backup/dist s3://visa-app-bucket/

# 4. Invalider CloudFront cache
aws cloudfront create-invalidation \
  --distribution-id E123ABC \
  --paths "/*"
```

---

## 📞 Support Production

- Logs: `/var/log/nginx/` et `/backend/logs/`
- Error Tracking: Sentry, LogRocket
- Monitoring: Datadog, New Relic
- Alerts: PagerDuty

---

**Sprint 4 - Prêt pour Production 🚀**
