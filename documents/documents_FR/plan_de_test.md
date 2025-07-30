# Inclusive Trip Planner – Plan de Test

## 1. Introduction

Le présent document a pour but de définir formellement l’approche de test utilisée pour valider l’application Inclusive Trip Planner. Ce projet mobile-first vise à permettre aux utilisateurs ayant des besoins en accessibilité de planifier et explorer en toute confiance des itinéraires de voyage sélectionnés. Il comporte un frontend Android natif développé avec Jetpack Compose, et un backend Java construit avec Spring Boot.

Ce plan de test décrit la stratégie, les outils, les cas de test et les environnements utilisés pour évaluer la qualité et la fiabilité du système. Il se concentre sur la vérification des fonctionnalités clés telles que l’authentification, la sélection des options d’accessibilité, la consultation d’itinéraires, les avis utilisateurs et la persistance des données.

Les tests sont réalisés à plusieurs niveaux : tests unitaires pour vérifier les composants individuels (par exemple ViewModels et classes de service), tests d’intégration pour valider l’API backend et le comportement de la base de données, ainsi que tests manuels pour simuler les flux d’interaction utilisateur.

Ce document est destiné à tous les acteurs du projet, y compris :
- Le **porteur et développeur du projet** (David Cuahonte Cuevas)
- Les **évaluateurs académiques**, responsables de l’évaluation technique et de la conformité
- Les futurs **contributeurs ou testeurs**, qui pourront s’appuyer sur cette base

L’objectif ultime de ce plan de test est de garantir que le MVP de l’Inclusive Trip Planner réponde aux exigences fonctionnelles et offre une expérience stable et inclusive à ses utilisateurs cibles.

## 2. Objectifs de Test

L’objectif principal de ce plan est de s’assurer que l’application fonctionne correctement, de manière fiable et sécurisée à travers ses composants clés. Les tests visent à valider la logique côté frontend et backend, en conformité avec les user stories et exigences fonctionnelles du MVP.

### Objectifs fonctionnels

- Vérifier que l’utilisateur peut compléter le processus d’inscription :
  - Saisie des informations personnelles (nom, e-mail/téléphone, mot de passe)
  - Sélection des préférences d’accessibilité
  - Choix des pays et destinations
- Assurer l’authentification via email/mot de passe et via login social (Google/Facebook)
- Vérifier que l’utilisateur peut :
  - Parcourir les itinéraires disponibles
  - Voir les détails et étapes d’un itinéraire
  - Enregistrer et supprimer des itinéraires
  - Laisser un avis avec note et commentaire
- Valider que les préférences sont bien enregistrées et reflétées dans l’interface

### Objectifs de sécurité

- Valider la génération, vérification et expiration du token JWT
- S’assurer que les endpoints protégés sont inaccessibles sans token valide
- Confirmer que les flux OAuth valident bien l’identité et empêchent les accès non autorisés

### Objectifs d’intégration

- Assurer un flux de données cohérent entre frontend et backend via Retrofit et API REST
- Confirmer la synchronisation des états utilisateurs, données d’itinéraires et avis entre client et serveur
- Valider la persistance des données dans une base relationnelle (PostgreSQL en production, H2 en test)

### Critères d’acceptation du MVP

L’application est considérée comme fonctionnellement complète si :
- Tous les ViewModels passent les tests unitaires (états normaux et échec)
- Les endpoints backend se comportent correctement dans les tests d’intégration (CRUD, sécurité)
- Tous les flux critiques utilisateurs sont exécutables sans erreur en test manuel
- L’intégrité des données est garantie dans tous les scénarios couverts

## 3. Portée

Cette section définit les limites de la campagne de tests du MVP. Elle précise les fonctionnalités incluses, ainsi que celles exclues pour cause de périmètre ou d’implémentation différée.

### Inclus dans la portée

Les éléments suivants sont couverts par des tests unitaires, d’intégration et/ou manuels :

- **Authentification et gestion de compte**
- **Flux d’inscription utilisateur**
- **Exploration d’itinéraires**
- **Itinéraires enregistrés (Saved)**
- **Avis utilisateurs**
- **Paramètres**
- **API backend et couche données (CRUD, sécurité, persistance)**

### Hors portée

Fonctionnalités exclues à ce stade :
- Vérification OTP / logique SMS
- Mécanismes hors-ligne ou de cache
- Filtrage dynamique d’itinéraires par accessibilité (non implémenté)
- Tests de performance et montée en charge
- Tests UI end-to-end automatisés (Espresso, UI Automator)
- Interface d’administration ou de création d’itinéraire
- Support multilingue et tests de localisation

## 4. Stratégie de Test

L’approche suit une structure en couches, combinant tests unitaires, d’intégration et validation manuelle, avec couverture fonctionnelle, technique et sécuritaire.

### Niveaux de test

| Niveau              | Description                                                                 |
|---------------------|-----------------------------------------------------------------------------|
| Test unitaire       | Vérifie les composants isolés (ex. ViewModel, services)                     |
| Test d’intégration  | Vérifie les échanges entre composants (API backend + base de données)       |
| Test manuel         | Simule les parcours utilisateurs complets sur émulateur Android             |

### Types de test

| Type                | Objectif                                                                 |
|---------------------|--------------------------------------------------------------------------|
| Fonctionnel         | Vérifie le respect des spécifications fonctionnelles                     |
| Sécurité            | Vérifie l’authentification, le JWT, la protection des routes             |
| Régression          | S’assure que les nouvelles modifs ne cassent pas le comportement existant|

## 5. Environnement de Test

### Environnement frontend

| Composant                | Description                               |
|--------------------------|-------------------------------------------|
| Plateforme               | Android (SDK cible : 33)                  |
| Framework UI             | Jetpack Compose                          |
| Langage                  | Kotlin                                   |
| Émulateur                | Pixel 5, Android 13                      |
| Outils de test           | JUnit5, MockK, Coroutine Test, Turbine   |
| Injection de dépendance  | Manuelle (par constructeur)              |
| API simulée              | Retrofit utilisé pour mocking            |

Les tests frontend sont exécutés dans Android Studio avec émulateur pour validation visuelle.

### Environnement backend

| Composant                | Description                                        |
|--------------------------|----------------------------------------------------|
| Framework                | Spring Boot 3.x                                    |
| Langage                  | Java 17                                            |
| Base de données (test)   | H2 en mémoire                                      |
| Base de données (prod)   | PostgreSQL                                         |
| ORM                      | Spring Data JPA                                    |
| Sécurité                 | Authentification stateless par JWT                 |
| Tests d’intégration      | JUnit5, MockMvc, SpringBootTest, Testcontainers (optionnel) |

Les tests d’intégration utilisent un profil `test` avec setup custom (`@BeforeEach`). Liquibase est désactivé en test pour éviter les interférences avec les données générées en code.

### CI/CD et exécution

| Aspect              | Statut                        |
|---------------------|-------------------------------|
| Tests continus      | Manuels (via IDE ou CLI)      |
| GitHub Actions      | Non encore configuré          |
| Fréquence de tests  | À chaque merge ou validation manuelle |

## 6. Gestion des Données de Test

Des tests fiables et reproductibles nécessitent des stratégies claires pour initialiser et gérer les données. Le projet Inclusive Trip Planner utilise à la fois des approches automatisées et manuelles, selon la couche testée.

### Stratégie pour le backend

Les tests d’intégration utilisent une base H2 en mémoire pour isoler les données de production et éviter tout effet de bord. Chaque classe de test initialise uniquement les entités nécessaires.

- **Isolation** : les tests sont indépendants et réinitialisent leur état avec `@BeforeEach`
- **UUIDs** : toutes les entités utilisent des UUID générés pour éviter les collisions
- **JWT** : les tokens JWT sont générés à la volée pour simuler des accès authentifiés
- **Relations** : les entités liées (ex. utilisateur avec préférences) sont créées dans chaque test
- **Liquibase** : désactivé pour permettre une configuration manuelle contrôlée en test

Cette stratégie assure un contrôle complet sur le cycle de vie des données sans interférences entre tests.

### Stratégie pour le frontend

Les tests ViewModel utilisent des données mockées pour simuler les appels API et entrées utilisateur. Aucun accès réseau ni base réelle n’est utilisé.

- **MockK** définit les retours attendus des appels API simulés
- **Turbine** observe les émissions de `Flow`/`StateFlow` dans les ViewModels
- **UUIDs** réalistes sont utilisés pour les objets de test
- **Vérification d’état** couvre :
  - état vide
  - état de chargement
  - succès avec données mockées
  - erreur simulée

Chaque test vérifie précisément les valeurs attendues dans le state après exécution.

### Résumé

| Couche     | Stratégie                            | Outils utilisés                            |
|------------|---------------------------------------|--------------------------------------------|
| Backend    | Base H2 + setup manuel                | JUnit5, H2, MockMvc, SpringBootTest         |
| Frontend   | API mockée + UUIDs simulés            | JUnit5, MockK, Coroutine Test, Turbine     |

L’objectif global est d’assurer la reproductibilité, l’isolation, et la fidélité aux scénarios réels.

## 7. Structure des Cas de Test

Tous les cas de test du projet suivent une structure standardisée pour assurer clarté, reproductibilité et vérifiabilité.

### Format de cas de test

Chaque test contient les champs suivants :

- **ID** : identifiant unique (ex. `VM-001`, `API-005`)
- **Titre** : description courte du but du test
- **Composant** : élément testé (ex. ViewModel, Controller)
- **Préconditions** : configuration requise
- **Étapes** : liste ordonnée d’actions à effectuer
- **Résultat attendu** : comportement attendu
- **Statut** : Passé ou Échoué

Ce format est suivi en code pour tous les tests automatisés.

---

### Exemple : Test unitaire Frontend

| Champ              | Exemple                                                       |
|--------------------|---------------------------------------------------------------|
| ID                 | `VM-003`                                                      |
| Titre              | `fetchCountries should update state on API success`          |
| Composant          | `CountryViewModel`                                            |
| Préconditions      | Mock API retourne 2 pays                                      |
| Étapes             | 1. Appeler `fetchCountries()`<br>2. Avancer le coroutine      |
| Résultat attendu   | Le ViewModel contient la liste, `isLoading = false`           |
| Statut             | Passé (assertions validées)                                   |

---

### Exemple : Test d’intégration Backend

| Champ              | Exemple                                                        |
|--------------------|----------------------------------------------------------------|
| ID                 | `API-010`                                                      |
| Titre              | `POST /api/reviews - should return 201 Created`            |
| Composant          | `ReviewController`                                             |
| Préconditions      | JWT valide, utilisateur et itinéraire existants               |
| Étapes             | 1. Authentifier<br>2. Faire POST avec note/commentaire        |
| Résultat attendu   | Réponse HTTP 201 + avis sauvegardé dans le corps              |
| Statut             | Passé                                                          |

---

### Types d’assertions

- **Valeur** : vérifier les données retournées ou affichées
- **État** : chargement, erreur, succès
- **Structure** : taille d’un tableau, champs non nuls
- **Sécurité** : accès refusé sans token
- **Gestion d’erreur** : comportement attendu en cas d’exception simulée

Cette structure uniforme facilite le suivi de la couverture de test à travers toutes les couches.

## 8. Couverture des Tests Frontend

Cette section détaille la couverture unitaire des ViewModels. Chaque test valide la logique métier, les transitions d’état et les réponses aux API (succès ou échec).

Les tests sont réalisés avec **JUnit 5**, **MockK**, **Coroutine Test**, et **Turbine**.

### ViewModels et scénarios testés

| ViewModel                        | Scénarios testés                                                                 |
|----------------------------------|----------------------------------------------------------------------------------|
| AccessibilityFeatureViewModel   | - Récupération des fonctionnalités<br>- Récupération par ID<br>- Mappage des labels |
| CountryViewModel                | - Chargement des pays<br>- Comportement en cas d’échec API                      |
| ItineraryAccessibilityViewModel| - Liste par défaut et données statiques                                         |
| ItineraryStepViewModel         | - Chargement des étapes<br>- Gestion d’erreurs<br>- Mise à jour d’état erreur   |
| ItineraryViewModel             | - Récupération globale ou par ID<br>- Gestion de `not found` et erreurs         |
| ReviewViewModel                | - Récupération des avis<br>- Filtrage par itinéraire<br>- Soumission avec succès ou erreur |
| SavedItinerariesViewModel      | - Chargement des itinéraires enregistrés<br>- Enregistrement / suppression<br>- Définir le plan suivant |
| SettingsViewModel              | - Chargement des paramètres globaux et utilisateurs<br>- PUT & POST<br>- Gestion erreurs |
| SignUpViewModel                | - Mise à jour des champs<br>- Soumission des préférences<br>- Envoi des pays/destinations<br>- Flow complet d’inscription |
| UserViewModel                  | - Création utilisateur<br>- Récupération par ID/email<br>- Connexion + stockage token<br>- Gestion identifiants invalides |

### Comportements validés

- **Validation d’état** : `StateFlow` contient les valeurs attendues
- **Indicateur de chargement** : `isLoading` géré correctement
- **Gestion d’erreur** : exceptions simulées → fallback logique exécuté
- **Callbacks** : succès ou échec lors d’actions critiques (connexion, avis, etc.)

### Résumé

| Catégorie               | Valeur                        |
|--------------------------|-------------------------------|
| Total ViewModels         | 10                            |
| Total tests unitaires    | 40+                           |
| API simulées             | Mockées intégralement         |
| Coroutines               | Testées avec `runTest`        |
| Flows/StateFlow          | Observés avec `Turbine`       |

Cette couche de test garantit la robustesse de la logique réactive dans les ViewModels et leur comportement asynchrone.

## 9. Couverture des Tests Backend

Les tests backend sont réalisés via des tests d’intégration simulant des appels HTTP réels, validant toute la chaîne (controller → service → repository). L’objectif est de tester la logique, la sécurité et l’intégrité des données.

### Outils et configuration

- **Framework** : Spring Boot 3.x + JUnit 5
- **Base de test** : H2 (isolée par classe)
- **Sécurité** : authentification JWT simulée avec tokens valides
- **Exécution** : `@SpringBootTest`, `@AutoConfigureMockMvc`, `@ActiveProfiles("test")`
- **Setup** : données personnalisées injectées via `@BeforeEach`, Liquibase désactivé

### Entités et classes testées

| Entité / Module                  | Classe de test                                    | Fonctionnalités couvertes                     |
|----------------------------------|----------------------------------------------------|-----------------------------------------------|
| User                             | `UserServiceTest`                                 | Création, login, récupération                 |
| Auth                             | `AuthServiceTest`, `JwtServiceTest`               | JWT, login via Firebase                       |
| Setting                          | `SettingServiceTest`                              | Paramètres globaux par défaut                 |
| UserSetting                      | `UserSettingServiceTest`                          | Création, modification, récupération          |
| AccessibilityFeature             | `AccessibilityFeatureServiceTest`                 | Liste, récupération, association              |
| UserAccessibilityFeature         | `UserAccessibilityFeatureServiceTest`             | Lien utilisateur / accessibilité              |
| Country                          | `CountryServiceTest`                              | Création, filtre, disponibilité               |
| UserCountryAccess                | `UserCountryAccessServiceTest`                    | Gestion d’accès utilisateur / pays            |
| Destination                      | `DestinationServiceTest`                          | Filtrage, lien pays                           |
| UserSelectedDestination          | `UserSelectedDestinationServiceTest`              | Sélections par utilisateur                    |
| Itinerary                        | `ItineraryServiceTest`, `ItinerarySecurityIntegrationTest` | CRUD, JWT                |
| ItineraryStep                   | `ItineraryStepServiceTest`                        | Étapes, position, lien itinéraire             |
| ItineraryAccessibility           | `ItineraryAccessibilityServiceTest`               | Lien accessibilité / itinéraire               |
| SavedItinerary                   | `SavedItineraryServiceTest`                       | Sauvegarde, suppression, récupération         |
| Review                           | `ReviewServiceTest`                               | Envoi et récupération des avis                |

## 10. Limites et Contraintes

Bien que le MVP soit couvert par des tests unitaires et d’intégration solides, certaines limites techniques subsistent, liées au périmètre défini et à la priorité donnée aux fonctionnalités principales. Ces limitations sont connues et intégrées dans la roadmap des itérations futures.

### Limites techniques

- **Pas de tests end-to-end (E2E)**  
  Aucune automatisation complète des parcours UI (type Espresso ou UI Automator). Les flux utilisateurs ont été testés manuellement via l’émulateur Android.

- **Pas de tests de performance ou charge**  
  Aucun test de montée en charge ou de concurrence n’a été mis en place. Ce type de tests sera nécessaire en cas de scalabilité future.

- **Pas de validation automatique de l’accessibilité**  
  Bien que le projet vise l’inclusion, des outils comme axe-core ou Accessibility Scanner ne sont pas encore intégrés.

- **Couverture d’erreurs centrée sur les cas courants**  
  Les échecs simulés sont standards (erreur réseau, 404, etc.). Les cas plus complexes (timeouts, lenteurs, retries) ne sont pas encore couverts.

- **Expiration et renouvellement de token non testés**  
  La logique JWT est bien testée, mais la gestion de l’expiration et du refresh automatique est reportée à une future phase.

- **Fonctionnalités placeholder non testées**  
  Des écrans comme la vérification OTP sont présents dans l’UI mais pas encore connectés au backend, donc exclus des tests.

### Considérations de périmètre

Ces limites résultent de choix conscients visant à prioriser la stabilité, la testabilité et la couverture des flux critiques. Elles n’impactent pas la qualité fonctionnelle du MVP, mais pourraient limiter sa résilience en cas d’usage extrême ou long terme.

Des cycles futurs de développement viendront renforcer les tests, ajouter de l’automatisation, et couvrir ces dimensions.

## 11. Résumé des Résultats de Test

Ce chapitre résume les résultats globaux des efforts de test pour le MVP, couvrant tests unitaires, d’intégration et validation manuelle.

### Résultats des tests unitaires

Les ViewModels sont testés pour les transitions d’état, réponses API, et scénarios d’erreur. Chaque test utilise des mocks isolés avec des dispatchers coroutine simulés.

| Couche         | Composant              | Statut   | Remarques                                 |
|----------------|------------------------|----------|--------------------------------------------|
| Frontend       | Logique ViewModel      | ✅ OK    | Tous les états critiques sont testés       |
| Frontend       | Interactions API mock  | ✅ OK    | Tous les endpoints clés sont simulés       |
| Frontend       | StateFlow/Flow         | ✅ OK    | Validé avec Turbine et assertions          |

Nombre total de tests unitaires : **47+**  
Échecs : **0**

### Résultats des tests d’intégration

Les tests couvrent toutes les entités du backend via Spring Boot, MockMvc et base H2. Chaque test est isolé et s’exécute dans un vrai contexte Spring.

| Couche         | Module                  | Statut   | Remarques                                 |
|----------------|--------------------------|----------|--------------------------------------------|
| Backend        | Toutes les entités       | ✅ OK    | CRUD + logique relationnelle validés       |
| Backend        | Authentification JWT     | ✅ OK    | Tokens valides/invalides testés            |
| Backend        | Endpoints sécurisés      | ✅ OK    | 401/403 vérifiés selon droits              |
| Backend        | Gestion des erreurs      | ✅ OK    | Statuts HTTP corrects et cohérents         |

Nombre de classes de test : **16**  
Échecs : **0**

### Résultats des tests manuels

Testés via Android Emulator, en conditions réalistes. Vérification des interactions frontend/backend.

| Flux testé                          | Résultat   | Remarques                                           |
|------------------------------------|------------|-----------------------------------------------------|
| Inscription utilisateur complète   | ✅ OK      | Données persistées et relues sans erreur           |
| Authentification (email + OAuth)   | ✅ OK      | JWT généré, stocké et réutilisé                    |
| Navigation et sauvegarde itinéraire| ✅ OK      | Affichage dynamique + définition Next Plan         |
| Soumission et affichage d’avis     | ✅ OK      | Mises à jour visibles en temps réel                |
| Modification des paramètres        | ✅ OK      | PUT/POST fonctionnels, UI mise à jour              |

Test manuel : **tous les flux critiques validés**  
Blocants ou bugs critiques : **aucun détecté**

### État global

| Composant         | Résultat   |
|-------------------|------------|
| Tests unitaires   | ✅ Validés |
| Tests d’intégration| ✅ Validés |
| Tests manuels     | ✅ Validés |
| Échecs connus     | ❌ Aucun   |
| Stabilité MVP     | ✔ Confirmée |

Le système est stable, fonctionnellement complet et conforme aux critères de validation définis.

## 12. Prochaines Étapes

Avec le MVP validé, plusieurs axes d’amélioration sont planifiés pour renforcer la couverture, la robustesse et l’évolutivité du projet.

### Améliorations de couverture

- **Mettre en place des tests UI E2E automatisés**  
  Utiliser Espresso ou UI Automator pour valider les parcours utilisateurs complets.

- **Étendre les tests de défaillance**  
  Simuler timeouts, retries, données corrompues, expiration de tokens, etc.

- **Tests de performance / montée en charge**  
  Simuler des utilisateurs simultanés avec JMeter ou Gatling pour évaluer la scalabilité.

- **Validation automatique de l’accessibilité**  
  Intégrer axe-core, Accessibility Scanner pour détecter les non-conformités WCAG.

### Renforcement de l’infrastructure

- **Configurer l’intégration continue**  
  Automatiser l’exécution des tests sur chaque push ou pull request via GitHub Actions.

- **Générer des rapports de test**  
  Intégrer JaCoCo, Surefire ou autres pour produire des tableaux de bord de couverture.

### Couverture des fonctionnalités futures

Les futures évolutions nécessiteront de nouveaux tests adaptés, notamment :

- Recommandations d’itinéraires selon les besoins d’accessibilité
- Assistant de planification basé sur une interface conversationnelle
- Interface admin pour création/modération d’itinéraires
- Support multilingue (français / anglais)
- Mode hors-ligne et gestion du cache

### Objectifs QA à long terme

- Définir des procédures d’acceptation formelles pour les versions
- Répartir les rôles QA dans un projet multi-développeurs
- Maintenir un plan de test versionné aligné sur la roadmap produit

Le but est de transformer ce plan de test MVP en un socle de qualité durable, au service de l’évolution continue de l’Inclusive Trip Planner.
