# Guide de Démarrage Rapide - API Gestion des Demandes de Visa

## 📦 Installation

### Prérequis
- Java 17+
- Maven 3.6+
- PostgreSQL 12+
- Git

### 1. Configuration de la Base de Données

#### Créer la base de données
```bash
# Connexion à PostgreSQL
psql -U postgres

# Créer la base de données
CREATE DATABASE visa_db;

# Créer l'utilisateur
CREATE USER visa_user WITH PASSWORD 'visa_password';

# Accorder les permissions
GRANT ALL PRIVILEGES ON DATABASE visa_db TO visa_user;
GRANT ALL PRIVILEGES ON SCHEMA public TO visa_user;

# Se connecter à la base
\c visa_db

# Les tables seront créées automatiquement par Flyway
```

### 2. Configuration du Projet

#### Éditer `application-dev.yml`
```yaml
spring:
  application:
    name: visa-back-office
  
  datasource:
    url: jdbc:postgresql://localhost:5432/visa_db
    username: visa_user
    password: visa_password
    driver-class-name: org.postgresql.Driver
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5
      connection-timeout: 30000
  
  jpa:
    hibernate:
      ddl-auto: validate
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: true
        jdbc:
          batch_size: 20
        fetch:
          batch_size: 50
    show-sql: true
    open-in-view: false
  
  flyway:
    enabled: true
    locations: classpath:db/migration
    baseline-on-migrate: true
    baseline-version: 0

server:
  port: 8080
  servlet:
    context-path: /
  compression:
    enabled: true
    min-response-size: 1024

logging:
  level:
    root: INFO
    com.demo.gestionVisa: DEBUG
    org.springframework.web: DEBUG
    org.hibernate.SQL: DEBUG
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} - %msg%n"
```

#### Éditer `application-prod.yml`
```yaml
spring:
  application:
    name: visa-back-office
  
  datasource:
    url: jdbc:postgresql://db-host:5432/visa_db_prod
    username: ${DB_USER}
    password: ${DB_PASSWORD}
    driver-class-name: org.postgresql.Driver
    hikari:
      maximum-pool-size: 20
      minimum-idle: 10
      connection-timeout: 30000
  
  jpa:
    hibernate:
      ddl-auto: validate
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        jdbc:
          batch_size: 20
  
  flyway:
    enabled: true
    locations: classpath:db/migration

server:
  port: 8080
  compression:
    enabled: true
    min-response-size: 1024

logging:
  level:
    root: WARN
    com.demo.gestionVisa: INFO
```

### 3. Dépendances Maven

Assurez-vous que votre `pom.xml` contient :
```xml
<!-- PostgreSQL -->
<dependency>
  <groupId>org.postgresql</groupId>
  <artifactId>postgresql</artifactId>
  <version>42.7.0</version>
  <scope>runtime</scope>
</dependency>

<!-- Flyway (Migrations de BD) -->
<dependency>
  <groupId>org.flywaydb</groupId>
  <artifactId>flyway-core</artifactId>
</dependency>

<!-- Spring Data JPA -->
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<!-- Spring Web -->
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

### 4. Compiler et Démarrer

```bash
# Aller dans le dossier backend
cd backend

# Compiler le projet
mvn clean install -DskipTests

# Démarrer en environnement développement
mvn spring-boot:run -Dspring-boot.run.arguments='--spring.profiles.active=dev'

# Ou depuis une distribution
java -jar target/visa-back-office-1.0-SNAPSHOT.jar --spring.profiles.active=dev
```

---

## 🧪 Test de l'API

### Créer un Demandeur

```bash
curl -X POST http://localhost:8080/api/demandeurs \
  -H "Content-Type: application/json" \
  -d '{
    "nom": "Dupont",
    "prenom": "Jean",
    "dateNaissance": "1990-05-15",
    "situationFamiliale": "MARIE",
    "nationalite": "Française",
    "adresseMadagascar": "123 Rue de la Paix, Antananarivo",
    "telephone": "+261381234567"
  }'
```

### Créer un Visa Transformable

```bash
curl -X POST http://localhost:8080/api/visas-transformables \
  -H "Content-Type: application/json" \
  -d '{
    "reference": "VISA-2024-001",
    "demandeurId": 1,
    "dateEntree": "2024-01-15",
    "lieuEntree": "Ivato",
    "dateExpiration": "2025-01-15"
  }'
```

### Créer une Demande de Visa

```bash
curl -X POST http://localhost:8080/api/demandes/enregistrer \
  -H "Content-Type: application/json" \
  -d '{
    "demandeur": {
      "nom": "Dupont",
      "prenom": "Jean",
      "dateNaissance": "1990-05-15",
      "situationFamiliale": "MARIE",
      "nationalite": "Française",
      "adresseMadagascar": "123 Rue de la Paix, Antananarivo"
    },
    "visaTransformable": {
      "reference": "VISA-2024-001",
      "demandeurId": 1,
      "dateEntree": "2024-01-15",
      "lieuEntree": "Ivato",
      "dateExpiration": "2025-01-15"
    },
    "typeDemande": "TRAVAILLEUR",
    "piecesJustificatives": []
  }'
```

### Ajouter une Pièce Justificative

```bash
curl -X POST http://localhost:8080/api/demandes/1/pieces \
  -H "Content-Type: application/json" \
  -d '{
    "typePiece": "PASSEPORT",
    "nomFichier": "passeport.pdf",
    "sousmise": true
  }'
```

---

## 🐛 Dépannage

### Erreur de connexion à la base de données
```
Erreur: Unable to connect to database
Solution: 
- Vérifier que PostgreSQL est en cours d'exécution
- Vérifier les identifiants (username/password)
- Vérifier l'URL JDBC
```

### Erreur de migration Flyway
```
Erreur: Flyway migration failed
Solution:
- Vérifier que les fichiers de migration sont dans /db/migration
- Vérifier la syntaxe SQL
- Vérifier les permissions de l'utilisateur PostgreSQL
```

### Erreur 404 - Endpoint non trouvé
```
Solution:
- Vérifier le chemin de l'endpoint
- Vérifier l'annotation @RequestMapping
- Vérifier que le controller est dans le bon package
```

---

## 📊 Structure de la Base de Données

```sql
-- Voir V1__CreateVisaDemandTables.sql pour les détails
```

### Relations
```
demandeur (1) ----< (n) visa_transformable
demandeur (1) ----< (n) demande
visa_transformable (1) ----< (n) demande
demande (1) ----< (n) piece_justificative
```

---

## 🔧 Commandes Utiles

### Maven
```bash
# Compiler sans tests
mvn clean install -DskipTests

# Exécuter les tests
mvn test

# Générer la documentation Javadoc
mvn javadoc:javadoc

# Vérifier les dépendances
mvn dependency:tree
```

### PostgreSQL
```bash
# Se connecter à PostgreSQL
psql -U visa_user -d visa_db -h localhost

# Lister les tables
\dt

# Voir la structure d'une table
\d demandeur

# Exécuter un script
\i script.sql

# Quitter
\q
```

---

## 📈 Performance

### Indexes Créés
- `idx_demandeur_nationalite`
- `idx_visa_transformable_demandeur_id`
- `idx_visa_transformable_reference`
- `idx_demande_demandeur_id`
- `idx_demande_visa_transformable_id`
- `idx_demande_statut`
- `idx_demande_type_demande`
- `idx_piece_justificative_demande_id`
- `idx_piece_justificative_sousmise`

---

## 📝 Logs

Vérifier les logs dans :
```
target/logs/application.log
```

Configurer les logs dans `application.yml` :
```yaml
logging:
  file:
    name: logs/application.log
    max-size: 10MB
    max-history: 30
```

---

## 🔒 Sécurité

Recommandations :
1. Utiliser HTTPS en production
2. Implémenter Spring Security avec JWT/OAuth2
3. Ajouter une validation CORS appropriée
4. Utiliser des variables d'environnement pour les credentials
5. Implémenter la limitation de taux (rate limiting)

---

## 📞 Support

Pour toute question, consultez :
- La documentation API : `VISA_API_DOCUMENTATION.md`
- Les logs : `target/logs/application.log`
- La structure du code : `/backend/src/main/java/com/demo/gestionVisa/`
