# Inclusive Trip Planner – Plan de Suivi et de Correction des Bugs

## 1. Introduction

Ce document décrit la stratégie utilisée pour identifier, classer et résoudre les bugs logiciels tout au long du cycle de développement et de test de l'application **Inclusive Trip Planner**.

Bien que le projet ait été développé par un contributeur unique, une approche structurée a été adoptée pour gérer les bugs détectés lors des tests unitaires, des tests d'intégration du backend, et des tests manuels sur l'émulateur Android.

L'objectif de ce plan est d'assurer la traçabilité des problèmes connus, d'expliquer comment ils ont été résolus, et de définir un processus durable pour gérer les bugs dans les futures itérations du projet. Il vise à démontrer que l'application a été construite avec un fort engagement envers la stabilité et la maintenabilité.

## 2. Classification des Bugs

Pour gérer efficacement les bugs, les problèmes rencontrés pendant le développement et les tests ont été classés par niveau de gravité. Chaque niveau indique l’impact potentiel sur la fonctionnalité, l'expérience utilisateur ou la stabilité.

### Niveaux de Gravité

| Gravité       | Description                                                                 | Exemple                                                      |
|---------------|-----------------------------------------------------------------------------|--------------------------------------------------------------|
| **Critique**  | Bloque des fonctionnalités clés ou provoque un crash. À corriger en urgence.| L’app plante lors de la soumission du formulaire sans données|
| **Majeur**    | Affecte significativement l’expérience ou la fonctionnalité sans crash.     | Les itinéraires enregistrés ne s’affichent pas après redémarrage |
| **Mineur**    | Problème esthétique ou mineur sans impact sur les workflows.               | Le spinner de chargement reste visible après un appel API    |
| **Amélioration** | Pas un bug, mais une suggestion ou un ajustement UX.                   | Ajouter un retour visuel après l’enregistrement d’un itinéraire |

### Processus de Classification

Tous les problèmes détectés pendant le développement ont été immédiatement évalués selon leur gravité et leur impact. J’ai attribué une catégorie à chaque problème avant de décider s’il devait être corrigé immédiatement ou reporté à une version ultérieure.

Dans la mesure du possible, la couverture de test a été élargie pour éviter que le problème ne se reproduise de manière invisible.

![Figure 1 – Échec d’un test unitaire sur SavedItinerariesViewModelTest](images/test-failure-screenshot.png)

_Figure 1 – Échec d’un test unitaire découvert pendant le développement._

## 3. Processus de Signalement des Bugs

Dans le contexte d’un développement en solo, le signalement des bugs a suivi une approche légère mais cohérente. Les bugs ont été découverts via une combinaison de résultats de tests automatisés, de tests manuels, et d'inspection directe du code.

### Canaux de Signalement

- **Échecs de tests automatisés** :  
  Les bugs sont souvent apparus lors de l'exécution des tests unitaires et d'intégration. Les erreurs étaient inspectées dans l’IDE (Android Studio ou IntelliJ), et les traces d’exception analysées pour localiser la cause.

- **Tests manuels** :  
  Lors des sessions de test sur émulateur, tout comportement inattendu de l’UI ou crash était noté dans un fichier Markdown local ou directement commenté dans le code.

- **Suivi via Git** :  
  Les correctifs étaient documentés avec des messages Git classiques utilisant les préfixes `fix:` ou `bug:`, facilitant la recherche ultérieure (ex. `git log --grep fix:`).

### Méthode Informelle de Suivi

En l’absence d’un outil de gestion de bugs formel, le processus suivant a été utilisé :

1. **Détection** – Identifier un problème via un test ou une observation.
2. **Description** – Rédiger un court résumé dans le code ou en commentaire.
3. **Classification** – Déterminer la gravité (Critique, Majeur, Mineur, Amélioration).
4. **Correction** – Corriger ou noter un `TODO` si reporté.
5. **Vérification** – Relancer les tests ou cas manuels pour s'assurer de la résolution.

Ce processus pragmatique a permis une gestion efficace des bugs même sans outil dédié.

## 4. Processus de Correction

Une fois un bug identifié et classé, un workflow de correction cohérent a été suivi afin de garantir que le problème soit résolu, testé, et validé sans introduire de régressions.

### Étapes du Workflow

| Étape          | Description                                                                     |
|----------------|----------------------------------------------------------------------------------|
| 1. **Reproduire** | Confirmer le bug via un test ou une interaction avec l’émulateur.              |
| 2. **Isoler**     | Identifier la cause dans la classe, l’API ou la logique concernée.             |
| 3. **Corriger**   | Appliquer un changement minimal. Mettre à jour les mocks ou données si besoin. |
| 4. **Vérifier**   | Relancer le test (ou le scénario manuel) pour confirmer la résolution.         |
| 5. **Régression** | Vérifier que d’autres fonctionnalités n’ont pas été impactées.                 |
| 6. **Commiter**   | Commiter avec un message clair préfixé de `fix:` dans Git.                     |

### Discipline de Commit

Chaque correction a été commise avec un message clair identifiant le problème, et si possible, lié au test ayant échoué. Cela a permis un historique Git propre et traçable.

Ce workflow de correction simple mais rigoureux a assuré la stabilité d’une base de code évoluant rapidement pendant la phase MVP.

## 5. Leçons Retenues

Le développement du MVP de l’Inclusive Trip Planner a été l’occasion d’appliquer des pratiques de test et de débogage structurées dans un contexte solo. Voici les enseignements clés de ce processus de suivi et de correction :

### 1. Les Tests Facilitent le Débogage

L’exécution régulière de tests automatisés a permis de détecter les régressions rapidement. Les bugs ont pu être reproduits facilement via des tests en échec, ce qui a facilité leur résolution.

> **Exemple** : Une incohérence de chargement dans `ReviewViewModel` a été détectée via un test Turbine en échec, puis corrigée avant la mise en production.

### 2. La Discipline de Commit Favorise la Traçabilité

L’utilisation systématique des préfixes `fix:` et `bug:` dans les commits Git a créé un historique clair des problèmes et solutions. Cette méthode légère a prouvé son efficacité même sans tracker.

> **Réflexion** : Le tag `fix:` permet non seulement de savoir *ce* qui a changé, mais surtout *pourquoi* – utile pour les tests ou les démonstrations.

### 3. Le Test Manuel Reste Incontournable

Malgré une bonne couverture de tests, les sessions manuelles ont révélé des bugs liés à l’UX, aux délais d’UI, ou à des comportements asynchrones.

> **Leçon** : Les feedbacks visuels comme les loaders ou états vides doivent être testés par interaction, pas uniquement par assertions.

### 4. Prévenir les Bugs Plutôt que les Corriger

De nombreux bugs ont été évités grâce à une approche proactive : écrire des tests de cas limites, simuler des erreurs, penser comme un utilisateur.

> **À retenir** : Le meilleur moyen de gérer les bugs est de concevoir avec le test en tête dès le départ – pas à la fin.

### 5. Un Processus Simple Vaut Mieux que Rien

Même sans Jira ou GitHub Issues, le processus informel (détecter → décrire → classer → corriger → vérifier) a fonctionné. Il a permis une rigueur suffisante sans surcharge.

> **Conclusion** : Ce qui compte, c’est la constance. Une méthode simple et répétable suffit pour garantir la qualité, notamment dans un projet solo.

---

Ces leçons constituent le socle d’un état d’esprit orienté qualité, qui guidera les prochaines phases du projet : nouvelles fonctionnalités, intégration CI, travail collaboratif, etc.

## 6. Tableau Récapitulatif des Bugs

Ce tableau résume les principaux bugs rencontrés lors du développement et des tests du MVP. Chaque ligne documente le type, la gravité, l’état de résolution et une courte description.

| ID Bug | Type         | Gravité   | Statut     | Description                                                                    |
|--------|--------------|-----------|------------|---------------------------------------------------------------------------------|
| BUG-001| Fonctionnel  | Critique  | Résolu     | Crash de l’application lors de l’enregistrement d’un itinéraire sans ID valide.|
| BUG-002| UI/UX        | Mineur    | Résolu     | Le spinner restait actif après un échec de récupération d’itinéraire.          |
| BUG-003| Logique API  | Majeur    | Résolu     | Les itinéraires enregistrés n’étaient pas rechargés après connexion.           |
| BUG-004| Synchronisation | Majeur | Résolu     | `SignUpViewModel` ne réinitialisait pas les champs après soumission.          |
| BUG-005| Sécurité     | Majeur    | Résolu     | Accès autorisé aux endpoints sans vérifier l’expiration du JWT.                |
| BUG-006| Liaison Données | Mineur | Résolu     | L’UI des paramètres ne reflétait pas les préférences après mise à jour.        |
| BUG-007| Amélioration | N/A       | Implémenté | Ajout d’une confirmation visuelle ("Enregistré !") après un enregistrement.    |

> _Remarque_ : Les IDs de bugs sont utilisés à des fins de traçabilité interne. Tous les bugs ont été résolus pendant la phase MVP avant la soumission finale et validés via des tests unitaires ou d’intégration associés.
