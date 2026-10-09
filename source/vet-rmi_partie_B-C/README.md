# TP1 HAI704I — Cabinet vétérinaire en Java RMI, Spring Boot et Swing

Projet réalisé pour le TP1 de l'UE **HAI704I — Architectures logicielles distribuées**.

Cette version constitue une évolution de la première partie du TP, réalisée initialement avec du **Java RMI classique**, sans framework et avec une interface en ligne de commande.

Dans cette nouvelle version, l'architecture RMI est conservée mais le projet est organisé avec **Maven**, le serveur et le client utilisent **Spring Boot**, et le client dispose désormais d'une **interface graphique Swing**.

Le projet couvre les fonctionnalités demandées dans le sujet, notamment la gestion des patients, des dossiers de suivi, des observations, des états de santé et des alertes de seuil.

---

## 1. Évolution par rapport à la version classique

La première version du projet reposait uniquement sur les mécanismes standards de Java :

* Java RMI ;
* `rmiregistry` ;
* compilation avec `javac` ;
* lancement avec `java` ;
* interface utilisateur en ligne de commande.

Cette nouvelle version conserve le principe de communication **client / serveur avec RMI**, mais ajoute plusieurs éléments :

| Version classique                         | Nouvelle version                            |
| ----------------------------------------- | ------------------------------------------- |
| Java RMI                                  | Java RMI                                    |
| Compilation `javac`                       | Maven                                       |
| Serveur avec `main` classique             | Serveur Spring Boot                         |
| Client CLI                                | Client Spring Boot                          |
| Interface console                         | Interface graphique Swing                   |
| Architecture en 3 projets                 | Architecture Maven multi-modules            |
| Gestion manuelle du registre              | Registre RMI configuré par Spring           |
| Appels RMI directement depuis l'interface | `ClientLogic` centralise les opérations RMI |

L'objectif est donc de **conserver les concepts étudiés dans la partie RMI**, tout en utilisant une architecture plus structurée et plus proche d'une application Java moderne.

---

## 2. Architecture générale

Le projet est organisé sous forme d'un projet **Maven multi-modules** composé de trois modules :

```text
vet-rmi/
├── vet-common/
├── vet-server/
└── vet-client/
```

Le principe reste proche de l'architecture du projet classique :

```text
                         ┌─────────────────────┐
                         │      vet-common     │
                         │                     │
                         │ Interfaces RMI      │
                         │ Classes partagées   │
                         └──────────┬──────────┘
                                    │
                     ┌──────────────┴──────────────┐
                     │                             │
                     ▼                             ▼
             ┌───────────────┐             ┌───────────────┐
             │   vet-server  │             │   vet-client  │
             │               │             │               │
             │ Spring Boot   │◄──── RMI ───►│ Spring Boot   │
             │ + RMI         │             │ + Swing       │
             └───────────────┘             └───────────────┘
```

Le module `vet-common` contient les éléments nécessaires aux deux côtés.

Le module `vet-server` contient l'implémentation du cabinet et des patients.

Le module `vet-client` contient la logique cliente, l'interface console et l'interface graphique.

---

## 3. Structure du projet

L'architecture essentielle du projet est la suivante :

```text
vet-rmi/
├── pom.xml
│
├── vet-common/
│   ├── pom.xml
│   └── src/main/java/veterinaire/
│       ├── Espece.java
│       ├── EspeceChien.java
│       ├── IAnimal.java
│       ├── ICabinetVeterinaire.java
│       ├── IDossierSuivi.java
│       └── IObservateur.java
│
├── vet-server/
│   ├── pom.xml
│   └── src/main/
│       ├── java/veterinaire/
│       │   ├── Animal.java
│       │   ├── CabinetService.java
│       │   ├── DossierSuivi.java
│       │   ├── PatientRegistry.java
│       │   ├── RmiServerConfig.java
│       │   └── ServerApplication.java
│       │
│       └── resources/
│           └── application.properties
│
└── vet-client/
    ├── pom.xml
    └── src/main/
        ├── java/veterinaire/
        │   ├── ClientApplication.java
        │   ├── ClientLogic.java
        │   ├── ConsoleInterface.java
        │   ├── ErreurServiceDistant.java
        │   ├── InfoAnimal.java
        │   ├── InfoDossier.java
        │   ├── Observateur.java
        │   └── VeterinaireGUI.java
        │
        └── resources/
```

Les dossiers `target/` générés par Maven ne sont pas nécessaires au fonctionnement du projet et peuvent être supprimés avec :

```bash
mvn clean
```

---

## 4. Fonctionnalités de l'interface graphique

L'interface graphique permet de gérer les patients directement depuis une fenêtre Swing.

### Gestion des patients

* afficher la liste des patients ;
* actualiser la liste ;
* rechercher un patient par son nom ;
* ajouter un nouveau patient ;
* retirer un patient.

### Gestion du dossier

Pour un patient sélectionné :

* consulter son dossier ;
* afficher son état de santé ;
* afficher son historique d'observations ;
* modifier son état de santé ;
* ajouter une observation.

Après une modification du dossier, celui-ci est automatiquement reconsulté afin que l'interface affiche immédiatement les nouvelles informations.

### Alertes

L'interface permet également :

* de s'abonner aux alertes ;
* de se désabonner ;
* d'afficher les notifications reçues du serveur ;
* de générer des patients de test pour vérifier le fonctionnement des seuils.

---

## 5. Différences avec la première version

La première version du TP était principalement destinée à mettre en pratique les mécanismes fondamentaux de Java RMI :

```text
Java
 │
 └── RMI
      ├── Registry
      ├── Remote
      ├── Stub
      ├── UnicastRemoteObject
      ├── Serializable
      └── Callback
```

Cette seconde version conserve ces concepts et ajoute une couche applicative :

```text
Spring Boot
     │
     ├── Serveur
     │     └── RMI
     │
     └── Client
           ├── ClientLogic
           └── Swing
```

Le cœur du système reste donc distribué avec **RMI**, tandis que Spring Boot est utilisé pour structurer et démarrer les applications serveur et cliente.
