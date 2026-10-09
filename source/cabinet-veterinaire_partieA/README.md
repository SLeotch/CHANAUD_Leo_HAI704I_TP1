# TP1 HAI704I — Cabinet vétérinaire en Java RMI

Projet réalisé pour le TP1 de l'UE HAI704I (Architectures logicielles
distribuées). Toutes les étapes A0 à A8 du sujet sont couvertes. Ce README
concere le dossier cabinet_veterinaire_partieA uniquement, comme précisé
dans le rapport, ce TP est divisé en 2 parties distinctes, une pour
l'étape A et une autre pour l'application finale complète avec la partie
B et C (vet-rmi_partie_B-C).

## 1. Ce que fait le projet

Un cabinet vétérinaire tourne dans un serveur RMI. Les animaux (patients)
vivent dans la JVM du serveur. Un ou plusieurs clients en ligne de commande
se connectent au serveur pour consulter, ajouter des patients, lire/modifier
leur dossier de suivi, et s'abonner à des alertes envoyées automatiquement
par le serveur quand le nombre de patients franchit 100, 500 ou 1000. Ce projet
a été réalisé en partie avec de l'IA, pour tout ce qui est affichage, réalisation
de README.md et structure du code / lisibilité. La logique de code et les
concepts appris en CM sont le fruit de mon travail.

## 2. Architecture : trois projets

Comme demandé par le sujet, le code est séparé en trois dossiers classiques :

```
cabinet-veterinaire/
├── common/veterinaire/    → contrat partagé par le client et le serveur
├── server/veterinaire/    → le serveur uniquement
└── client/veterinaire/    → le client uniquement
```

`server` et `client` ont tous les deux `common` sur leur classpath au moment
de la compilation. **Le client ne dépend d'aucune classe de `server`** :
c'est vérifiable directement en regardant les imports de `client/veterinaire`.

### `common` — le contrat distant

- `IAnimal` : interface distante d'un animal (nom, maître, race, espèce,
  accès au dossier de suivi).
- `ICabinetVeterinaire` : interface distante du cabinet (liste des patients,
  recherche par nom, ajout de patient, abonnement/désabonnement aux
  alertes). C'est le seul objet publié dans le registre.
- `IDossierSuivi` : interface distante du dossier de suivi d'un animal.
- `IObservateur` : interface que le client implémente pour recevoir les
  alertes du serveur (callback).
- `Espece` : classe `Serializable` (pas `Remote`) — nom + espérance de vie.
  Transmise **par copie** : chaque appel à `getEspece()` renvoie un nouvel
  objet, propre à celui qui le demande.
- `EspeceChien` : sous-classe d'`Espece`, utilisée pour l'expérience de
  sérialisation de l'étape A7 (voir § 6).

**Décision de conception (A3)** : le dossier de suivi est un objet **distant**
(`Remote`), pas une simple copie. Tous les clients qui appellent
`getDossierSuivi()` sur le même animal reçoivent un stub vers le **même**
objet côté serveur : une modification faite par un vétérinaire est donc
immédiatement visible par les autres. C'est l'inverse du choix fait pour
`Espece`, qui elle est volontairement une copie (voir § 5).

### `server` — les servants

- `Animal` : implémente `IAnimal`, étend `UnicastRemoteObject` (export
  implicite au constructeur). Crée son propre `DossierSuivi` à la création.
- `DossierSuivi` : implémente `IDossierSuivi`, objet distant exporté
  séparément de l'animal qui le porte.
- `CabinetVeterinaire` : implémente `ICabinetVeterinaire`. Gère la liste des
  patients, la recherche par nom, la création de nouveaux patients, et la
  liste des observateurs abonnés (avec détection des seuils 100/500/1000).
- `Server` : le `main` du serveur — crée le cabinet, crée le registre,
  publie le cabinet sous le nom `"cabinet"`.

### `client` — trois responsabilités séparées (étape A8)

- **`ClientMain`** (point d'entrée) : lit les arguments (hôte, port,
  éventuellement le nom du vétérinaire), fait le `lookup`, lance
  `ConsoleInterface`. Avec `ClientLogic`, c'est le seul endroit du client à
  importer `java.rmi`.
- **`ClientLogic`** (logique client) : toutes les opérations RMI. Attrape
  chaque `RemoteException` et la retraduit en `ErreurServiceDistant`, une
  exception non vérifiée et purement locale au client.
- **`ConsoleInterface`** (interface) : affiche le menu, lit et valide les
  saisies (`Scanner`), affiche les résultats. **Aucun import `java.rmi`** —
  elle ne connaît que `ClientLogic` et les classes de données locales
  (`InfoAnimal`, `InfoDossier`, `ErreurServiceDistant`).
- `InfoAnimal` / `InfoDossier` : petites classes de données locales (pas de
  `RemoteException` dans leurs méthodes), qui transportent l'état d'un
  animal ou d'un dossier jusqu'à la console sans exposer les stubs RMI.
- `Observateur` : implémente `IObservateur`, exporté par le client lors de
  l'abonnement aux alertes.

Les fichiers `Client_A1A2A3.java`, `Client_A4A5A7.java` et `Client_A6.java`
sont des scénarios de test écrits en dur pendant les étapes intermédiaires
(A1 à A7), conservés à titre de trace de la démarche. **L'application
finale à utiliser est `ClientMain`.**

## 3. Environnement

- JDK 17 minimum (testé avec le JDK 21).
- Aucune dépendance externe, seulement la bibliothèque standard.
- `rmiregistry`, `javac`, `java` fournis par le JDK.
- Développé avec [IntelliJ IDEA / VS Code / éditeur au choix] et compilé en
  ligne de commande.

## 4. Compilation

Depuis `cabinet-veterinaire/`, on peut tout compiler d'un coup et tout
effacer pour pouvoir re-compiler, par exemple sur Linux :

```bash
find . -name "*.class" -delete 
javac $(find . -name "*.java")*

```

Sur Windows : 

```bash
javac (Get-ChildItem -Recurse -Filter *.java).FullName
```

## 5. Lancement

Le registre est créé **dans la JVM du serveur** (`LocateRegistry.createRegistry`),
sur le port **1099** par défaut. Le nom du service publié est `"cabinet"`.

**Terminal 1 — serveur :**
```bash
java -cp "server:common" veterinaire.Server
```

**Terminal 2 et 3 — au moins deux clients, dans deux terminaux séparés :**
```bash
java -cp "client:common" veterinaire.ClientMain
```

## 6. Utiliser la CLI

Au lancement, `ClientMain` se connecte au registre et affiche le menu :

```
1. Lister les patients
2. Rechercher un patient par nom
3. Enregistrer un nouveau patient
4. Consulter le dossier de suivi d'un patient
5. Mettre à jour l'état de santé d'un patient
6. Ajouter une observation au dossier d'un patient
7. S'abonner aux alertes de seuil
8. Se désabonner des alertes
9. Quitter
```

On tape le numéro de l'option, puis on répond aux questions posées
(nom, maître, race, espèce...). Une saisie invalide (par exemple un texte
là où un nombre est attendu) affiche un message et revient au menu, sans
arrêter le client. Si le serveur est arrêté ou injoignable, chaque option
échoue avec un message clair au lieu de planter.

**Alertes** : après l'option 7, les alertes du serveur (franchissement des
seuils 100/500/1000) s'affichent directement dans la console, y compris
pendant qu'on est en train de taper une réponse à une autre question —
l'affichage peut donc apparaître au milieu d'un prompt, puisqu'il arrive sur
un thread du runtime RMI indépendant de la lecture au clavier.

**Quitter (option 9)** : si le client était abonné, il se désabonne avant de
fermer, ce qui libère l'objet `Observateur` exporté. Sans ça, les threads
d'écoute RMI ouverts par l'export garderaient le processus en vie après la
fin du `main`.

## 7. Étape A7 — expérience de sérialisation

`EspeceChien` (dans `common`) sert à reproduire l'échec demandé par le
sujet :
1. Déplacer temporairement `EspeceChien.java` dans `client/veterinaire/`.
2. Recompiler **uniquement** le client (le serveur ne doit plus connaître la
   classe).
3. Envoyer une instance d'`EspeceChien` au serveur via l'ajout de patient.
4. Observer côté serveur une `ClassNotFoundException` (encapsulée dans une
   `UnmarshalException`, puis une `ServerException` côté client) : le flux
   sérialisé transporte le nom et l'état de la classe, jamais son bytecode.
5. Corriger en remettant simplement le fichier dans `common`, connu des deux
   côtés — c'est la correction la plus simple demandée par le sujet.