# 📌 Projet : Système de découpage intelligent de fichiers en Java

## 🚀 Introduction
Ce projet implémente un système de découpage intelligent de fichiers en utilisant l'algorithme **Content-Defined Chunking (CDC)**. L'objectif est de découper les fichiers dynamiquement, détecter les doublons et appliquer une compression efficace à la volée.

Les chunks générés sont stockés dans le dossier `chunks`. Lorsqu'un chunk est détecté comme étant déjà présent (grâce à son empreinte unique calculée avec **BLAKE3**), il n'est pas réenregistré, ce qui permet d'économiser de l'espace et d'optimiser les performances.

---

## 📌 Fonctionnalités principales

### 🔹 Phase 1 : Découpage dynamique des fichiers (Chunking)
- Utilisation de **Rabin Fingerprinting** pour identifier les points de coupure optimaux.
- Découpage des fichiers en chunks de tailles variables.
- Stockage des chunks sur disque avec indexation.

### 🔹 Phase 2 : Détection des doublons
- Utilisation de **BLAKE3** pour générer des empreintes uniques des chunks.
- Stockage et indexation des empreintes pour éviter la duplication des blocs.
- Vérification rapide pour réduire l’espace de stockage.
- **Les chunks uniques sont stockés dans le dossier `chunks`, et ceux déjà existants sont simplement référencés.**

### 🔹 Phase 3 : Compression à la volée
- Compression de chaque chunk avec **Zstd**.
- Comparaison entre compression globale et compression par chunk.

### 🔹 Phase 4 : Tests de performance
- Mesure du **temps de découpage des fichiers**.
- Évaluation du **gain de stockage** grâce à la détection des doublons.
- Analyse du **temps de reconstruction des fichiers**.
- Comparaison de l’impact de la compression.
- Tests sur différents types de fichiers (**texte, CSV, images, binaires, logs, archives ZIP**).

---

## 🛠️ Technologies utilisées
- **Langage** : Java 17+
- **Découpage** : Content-Defined Chunking (CDC) avec Rabin Fingerprinting
- **Hashing** : BLAKE3 pour l’identification unique des chunks
- **Compression** : Zstd
- **Stockage des empreintes** : HashMap en mémoire pour une détection rapide des doublons
- **Gestion des chunks** : Stockage sur disque avec indexation des empreintes
- **Tests de performance** : Mesure des temps d’exécution, des gains en stockage et de la rapidité de reconstruction des fichiers


## 📦 Structure du projet
```
📂 files-slicing-system
├── 📁 src
│   ├── 📁 main
│   │   ├── 📁 java
│   │   │   ├── 📁 org.project.service        # Services principaux
│   │   │   ├── 📁 org.project.utils         # Utilitaires JSON & performance
│   │   │   ├── 📁 org.project.storage       # Gestion des chunks
│   │   │   ├── 📁 org.project.controller    # Gestion des entrées utilisateur
│   │   │   ├── 📄 App.java
├── 📁 chunks                                # Stockage des chunks compressés
├── 📄 result.json                           # Métadonnées des fichiers découpés
├── 📄 README.md                             # Documentation du projet
└── 📄 pom.xml                                # Fichier de configuration Maven
```

## 📖 Installation et exécution
### 🔹 Prérequis
- Java 17+
- Maven

### 🔹 Cloner le dépôt
```bash
git clone https://github.com/votre-repo/files-slicing-system.git
cd files-slicing-system
```

### 🔹 Compiler et exécuter
```bash
mvn clean install
java -jar target/files-slicing-system.jar
```

### 🔹 Lancer le programme
Au lancement, le programme demandera d’entrer le chemin du fichier à découper :
```bash
🖍️ Entrez le chemin du fichier à découper : /path/to/your/file.png
```
Après le traitement, les résultats seront affichés et stockés dans `result.json`.

## 📊 Tests de performance
Pour évaluer l’efficacité du système, un mode **benchmark** est disponible.

```bash
Voulez-vous effectuer un test de performance ? (oui/non) 🖍️: oui
```
Il mesurera :
- Le temps de découpage et de compression
- Le gain en stockage
- La rapidité de reconstruction du fichier original

## 📌 Améliorations possibles
- Intégration avec une base de données pour stocker les empreintes
- Ajout d’une API REST pour interagir avec le système

---

