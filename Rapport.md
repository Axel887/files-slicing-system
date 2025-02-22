# Rapport d'Analyse du Système de Chunking et Compression

## 1️⃣ Introduction
Ce rapport présente une analyse approfondie des performances du système de découpage de fichiers (chunking) et de compression mis en place. L'analyse se base sur les données extraites de result.json, qui enregistre les métadonnées des fichiers traités :

- Extension du fichier
- Liste des chunks générés (identifiés par un hash unique)
- Taille compressée des chunks enregistrés

Tous les chunks générés sont stockés dans le dossier chunks. Lorsqu'un chunk est déjà présent, il n'est pas dupliqué, ce qui permet d'optimiser l'espace de stockage et d'améliorer les performances du système. Cette gestion efficace des doublons permet une réduction significative de la taille totale des fichiers stockés, en particulier pour les fichiers contenant des données répétitives.

Nous allons comparer les résultats obtenus sur différents types de fichiers afin de mettre en évidence les gains en stockage et performances du système.

---

## 2️⃣ Présentation des Données Analytiques
### 📌 **Fichiers Testés** (Masque utilisé : `0x1FFF`)

| Fichier              | Taille Originale | Chunks Analysés | Chunks Stockés | Déduplication | Taille Comprimée | Gain Stockage |
|----------------------|-----------------|---------------|---------------|--------------|----------------|--------------|
| `test1.txt`         | 358 884 bytes   | 16            | 16            | ❌ 0%        | 76 bytes       | ❌ 0%        |
| `test2.txt`         | 374 778 bytes   | 17            | 2             | ✅ 88.24%    | 2 412 bytes    | ✅ 93.96%    |
| `cat.png`           | 916 149 bytes   | 38            | 38            | ❌ 0%        | 1 390 bytes    | ❌ 0%        |
| `cat_duplicate.png` | 916 149 bytes   | 38            | 0             | ✅ 100%      | 5 699 bytes    | ✅ 100%      |
| `test.pdf`          | 56 028 bytes    | 3             | 3             | ❌ 0%        | 2 659 bytes    | ❌ 0%        |
| `test.zip`          | 160 bytes       | 1             | 1             | ❌ 0%        | 129 bytes      | ❌ 0%        |
| `fake.log`          | 5 026 bytes     | 1             | 1             | ❌ 0%        | 772 bytes      | ❌ 0%        |
| `fakesdatas.csv`    | 4 067 bytes     | 1             | 1             | ❌ 0%        | 975 bytes      | ❌ 0%        |

### **🔍 Analyse des résultats :**
- **`test1.txt`** a généré **16 chunks** mais **aucune déduplication** n'a été détectée.
- **`test2.txt`** contient **15 chunks en double**, ce qui a permis une **réduction de stockage de 93,96%**.
- **`cat_duplicate.png`** est une copie exacte de `cat.png`, donc **100% des chunks ont été réutilisés**.
- Les fichiers **compressés (`test.zip`, `test.pdf`) n'ont pas bénéficié de déduplication**, ce qui est logique puisque la compression les rend plus uniques.
- **Les fichiers logs et CSV** n'ont pas de doublons détectés et ont subi une **compression modérée**.

---

## 3️⃣ Comparaison des Performances de Compression (Masque `0x1FFF`)

| Fichier              | Compression Globale (octets) | Compression Par Chunks (octets) | Temps Compression (ms) | Temps Décompression (ms) |
|----------------------|----------------------------|--------------------------------|------------------------|--------------------------|
| `test1.txt`         | 538                         | 2 412                          | 2.99                   | 11.68                    |
| `test2.txt`         | 5 432                       | 1 390                          | 2.05                   | 0.87                     |
| `cat.png`           | 914 929                     | 5 699                          | 4.64                   | 1.94                     |
| `cat_duplicate.png` | 914 929                     | 2 659                          | 4.53                   | 7.24                     |
| `test.pdf`          | 50 420                      | 519                            | 0.29                   | 0.56                     |
| `test.zip`          | 129                         | 222                            | 0.03                   | 0.50                     |
| `fake.log`          | 772                         | 222                            | 0.06                   | 0.45                     |
| `fakesdatas.csv`    | 975                         | 222                            | 0.06                   | 0.45                     |

---

## 4️⃣ Comparaison des Performances avec d'autres Masques

Afin d'affiner l'analyse, les performances de découpage et compression ont été comparées avec d'autres masques (`0x3FFF` et `0x0FFF`). Voici les principales différences observées :

| Masque  | Nombre de Chunks | Ratio Compression (%) | Temps Compression (ms) | Temps Décompression (ms) |
|---------|----------------|----------------------|----------------------|----------------------|
| `0x1FFF` | 16             | **99.85%**           | 2.99                 | 11.69                |
| `0x3FFF` | 17             | **98.55%**           | 2.04                 | 0.87                 |
| `0x0FFF` | 19             | **99.85%**           | 2.74                 | 10.38                |

### **🔍 Analyse comparative :**
- **`0x1FFF` offre le meilleur équilibre** entre le nombre de chunks générés, la compression et la rapidité de traitement.
- **`0x3FFF` est plus rapide** en compression et décompression mais sacrifie légèrement le ratio de compression.
- **`0x0FFF` génère plus de chunks**, ce qui peut entraîner un surcoût de gestion tout en conservant un bon taux de compression.

---


