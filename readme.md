# 🎯 Escaperoom - Fullstack Application

Escaperoom est une application web de réservation d’escape game permettant de consulter des événements, réserver des créneaux et gérer ses réservations via une **API REST sécurisée (JWT)**.

---

## 🌐 Accès à l’application

🔗 http://141.253.123.152/

---

## 🧱 Architecture

- Frontend : Angular 19
- Backend : Spring Boot (API REST)
- Base de données : SQL
- Architecture découplée (Frontend / Backend)

---

## ⚙️ Stack technique

### Frontend
- Angular, TypeScript
- Reactive Forms
- HttpClient + Routing

### Backend
- Spring Boot
- Spring Security
- JWT (authentification)
- API REST
- JPA / Hibernate
- Architecture en couches

### Base de données
- MySQL / PostgreSQL
- Utilisateurs, Événements, Réservations

---

## 🔐 Sécurité

- Authentification JWT
- Rôles : `ROLE_USER`, `ROLE_ADMIN`
- Protection des endpoints REST

---

## 🔌 API REST

- Authentification : login / register
- Événements : CRUD complet
- Réservations : création / suppression
- Gestion utilisateurs

---

## ☁️ Déploiement

- Backend containerisé avec **Docker**
- Déploiement sur **Oracle Cloud**
- API REST exposée publiquement
- Frontend connecté à l’API distante
- Base de données hébergée dans l’environnement cloud

---

## 👨‍💻 Auteur

Winy