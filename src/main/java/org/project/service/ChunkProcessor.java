package org.project.service;

import org.project.utils.JsonUtils;
import org.project.storage.ChunkStorage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.Base64;

public class ChunkProcessor {
    private final ChunkStorage chunkStorage;
    private final FileChunker fileChunker;
    private final Compressor compressor;

    public ChunkProcessor(ChunkStorage chunkStorage, FileChunker fileChunker) {
        this.chunkStorage = chunkStorage;
        this.fileChunker = fileChunker;
        this.compressor = new Compressor();
    }

    public void processFile(File file) throws IOException {
        System.out.println("\n=========================================");
        System.out.println(" 📂 Traitement du fichier : " + file.getName());
        System.out.println("=========================================\n");

        // Récupérer l'extension du fichier d'origine
        String extension = "";
        int lastDotIndex = file.getName().lastIndexOf(".");
        if (lastDotIndex != -1) {
            extension = file.getName().substring(lastDotIndex); // Inclut le point (ex: ".png")
        }

        // 🔹 Charger tous les chunks existants depuis `chunks.json`
        Map<String, String> chunkMapping = JsonUtils.readFromJsonFile("chunks.json", Map.class);
        if (chunkMapping == null) {
            chunkMapping = new LinkedHashMap<>();
        }
        System.out.println("📄 chunks.json chargé : " + chunkMapping.size() + " chunks uniques existants.");

        // 🔹 Créer une liste pour la séquence des chunks du fichier en cours (`result.json`)
        List<String> chunkSequence = new ArrayList<>();

        // 🔹 Découper le fichier en chunks
        List<byte[]> chunks = fileChunker.getChunks(file);
        int chunkCount = 1; // Numéro du chunk pour affichage
        int uniqueChunksStored = 0; // Nombre de nouveaux chunks ajoutés

        for (byte[] chunk : chunks) {
            boolean isNewChunk = processChunk(chunk, chunkCount, chunkMapping, chunkSequence);
            if (isNewChunk) {
                uniqueChunksStored++;
            }
            chunkCount++;
        }

        System.out.println("\n📊 Résumé du traitement :");
        System.out.println("✅ " + chunkSequence.size() + " chunks analysés.");
        System.out.println("✅ " + uniqueChunksStored + " nouveaux chunks stockés (hors doublons).");

        // 🔹 Sauvegarder tous les chunks dans `chunks.json` (mise à jour globale)
        JsonUtils.saveToJsonFile(chunkMapping, "chunks.json");

        // 🔹 Sauvegarder la séquence des chunks et l'extension dans `result.json`
        Map<String, Object> resultData = new HashMap<>();
        resultData.put("chunks", chunkSequence);
        resultData.put("extension", extension); // Ajout de l'extension

        JsonUtils.saveToJsonFile(resultData, "result.json");

        System.out.println("\n✅ Fichiers de sauvegarde mis à jour :");
        System.out.println("  📄 chunks.json  → Contient " + chunkMapping.size() + " chunks uniques.");
        System.out.println("  📄 result.json  → Contient la séquence des chunks pour la reconstruction.");
    }

    private boolean processChunk(byte[] chunk, int chunkCount, Map<String, String> chunkMapping, List<String> chunkSequence) {
        String chunkHash = Blake3Hasher.hashChunk(chunk);
        String chunkData = Base64.getEncoder().encodeToString(chunk);

        boolean isNewChunk = false;

        System.out.println("\n📦 Chunk " + chunkCount);
        System.out.println("  ○ Hash   : " + chunkHash);
        System.out.println("  ○ Taille : " + chunk.length + " bytes");

        // 🔹 Vérifier si le chunk existe déjà dans `chunks.json`
        if (!chunkMapping.containsKey(chunkHash)) {
            chunkMapping.put(chunkHash, chunkData);
            byte[] compressedChunk = compressor.compressChunkWithZstd(chunk);
            chunkStorage.storeChunk(chunkHash, compressedChunk);
            isNewChunk = true;

            System.out.println("  ⚡ Compression appliquée");
            System.out.println("  ⚡ Taille compressée : " + compressedChunk.length + " bytes");
        } else {
            System.out.println("  🔁 Chunk déjà existant (retrouvé dans chunks.json) !");
        }

        chunkSequence.add(chunkHash);
        System.out.println("-----------------------------------------");

        return isNewChunk;
    }

    public void compressChunksWithoutMessage(List<byte[]> chunks) {
        for (byte[] chunk : chunks) {
            String chunkHash = Blake3Hasher.hashChunk(chunk);
            if (!chunkStorage.contains(chunkHash)) {
                byte[] compressedChunk = compressor.compressChunkWithZstd(chunk);
                chunkStorage.storeChunk(chunkHash, compressedChunk);
            }
        }
    }

    public ChunkStorage getChunkStorage() {
        return this.chunkStorage;
    }
}
