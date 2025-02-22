# **Rapport d'Analyse du Système de Chunking et Compression**

## 1. **Introduction**

Ce rapport analyse les performances du système de chunking et de compression en utilisant deux masques différents : `0x1FFF` et `0x3FFF`. L'objectif est de comparer les résultats en termes de **nombre de chunks générés, taux de compression, temps de traitement et efficacité globale**.

Les fichiers analysés ont été traités en enregistrant les métadonnées dans `result.json`, contenant :
- L'extension du fichier
- La liste des chunks générés (identifiés par un hash unique)
- La taille compressée des chunks stockés

Tous les chunks générés sont stockés dans le dossier `chunks`. Lorsqu'un chunk est déjà présent, il est réutilisé pour optimiser l'espace de stockage et améliorer les performances.

---

## 2. **Comparaison des Données Analytiques**

### **📂 Fichiers Testés**

| Fichier              | Masque | Taille Originale | Chunks Analysés | Chunks Stockés | Déduplication | Taille Comprimée | Gain Stockage |
|----------------------|--------|-----------------|---------------|---------------|--------------|----------------|--------------|
| `test1.txt`         | `0x1FFF` | 358,884 bytes   | 16            | 16            | 0%          | 2,412 bytes    | 0%          |
| `test1.txt`         | `0x3FFF` | 358,884 bytes   | 13            | 13            | 0%          | 2,659 bytes    | 0%          |
| `test2.txt`         | `0x1FFF` | 374,778 bytes   | 17            | 2             | 88.24%      | 2,412 bytes    | 93.96%      |
| `test2.txt`         | `0x3FFF` | 374,778 bytes   | 14            | 2             | 85.71%      | 1,974 bytes    | 92.91%      |
| `test.pdf`          | `0x1FFF` | 56,028 bytes    | 3             | 3             | 0%          | 1,390 bytes    | 0%          |
| `test.pdf`          | `0x3FFF` | 56,028 bytes    | 2             | 2             | 0%          | 1,186 bytes    | 0%          |
| `cat.png`           | `0x1FFF` | 916,149 bytes   | 38            | 38            | 0%          | 371 bytes      | 0%          |
| `cat.png`           | `0x3FFF` | 916,149 bytes   | 34            | 34            | 0%          | 5107 bytes     | 0%          |

---

## 3. **Comparaison des Performances de Compression**

| Fichier              | Masque | Compression Globale (octets) | Compression Par Chunks (octets) | Temps Compression (ms) | Temps Décompression (ms) |
|----------------------|--------|----------------------------|--------------------------------|------------------------|--------------------------|
| `test1.txt`         | `0x1FFF` | 538                         | 2,412                          | 2.99                   | 11.69                    |
| `test1.txt`         | `0x3FFF` | 538                         | 5,063                          | 3.21                   | 1.30                     |
| `test2.txt`         | `0x1FFF` | 5,432                       | 1,390                          | 2.05                   | 0.87                     |
| `test2.txt`         | `0x3FFF` | 5,432                       | 10,397                         | 2.52                   | 1.25                     |
| `test.pdf`          | `0x1FFF` | 50,420                      | 519                            | 0.29                   | 0.56                     |
| `test.pdf`          | `0x3FFF` | 50,420                      | 50,490                         | 1.20                   | 0.90                     |
| `cat.png`           | `0x1FFF` | 914,929                     | 916,376                        | 6.06                   | 2.19                     |
| `cat.png`           | `0x3FFF` | 914,929                     | 916,336                        | 6.16                   | 1.57                     |

---

## 4. **Analyse des Résultats**

### **🔍 Points Clés :**
- **Déduplication :** Le masque `0x1FFF` a permis une meilleure réduction des chunks redondants.
- **Nombre de chunks :** Le masque `0x3FFF` produit moins de chunks que `0x1FFF`, ce qui peut être un avantage pour la gestion du stockage.
- **Temps de compression :** `0x3FFF` offre de **meilleures performances en compression et décompression** pour certains fichiers.
- **Compression par chunks :** `0x3FFF` donne des tailles compressées légèrement supérieures sur certains fichiers, mais optimise le nombre de chunks.


