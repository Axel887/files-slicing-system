package org.project.service;

import org.project.utils.JsonUtils;
import org.project.storage.ChunkStorage;
import java.io.File;
import java.io.IOException;
import java.util.*;

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

        List<byte[]> chunks = fileChunker.getChunks(file);
        Map<String, String> chunkMapping = new LinkedHashMap<>(); // Stocke les chunks uniques
        List<String> chunkSequence = new ArrayList<>(); // Stocke l'ordre des hashes pour la reconstruction

        int chunkCount = 1; // Numéro du chunk pour affichage
        for (byte[] chunk : chunks) {
            processChunk(chunk, chunkCount, chunkMapping, chunkSequence);
            chunkCount++;
        }

        System.out.println("\n📊 Résumé du traitement :");
        System.out.println("✅ " + chunkSequence.size() + " chunks analysés.");
        System.out.println("✅ " + chunkMapping.size() + " chunks uniques stockés.");

        // Sauvegarde tous les chunks uniques
        JsonUtils.saveToJsonFile(chunkMapping, "chunks.json");

        // Sauvegarde la séquence des chunks pour la reconstruction
        Map<String, Object> resultData = new HashMap<>();
        resultData.put("chunks", chunkSequence);
        JsonUtils.saveToJsonFile(resultData, "result.json");

        System.out.println("\n✅ Fichiers de sauvegarde générés :");
        System.out.println("  📄 chunks.json  → Contient " + chunkMapping.size() + " chunks uniques.");
        System.out.println("  📄 result.json  → Contient la séquence des chunks pour la reconstruction.");
    }

    private void processChunk(byte[] chunk, int chunkCount, Map<String, String> chunkMapping, List<String> chunkSequence) {
        String chunkHash = Blake3Hasher.hashChunk(chunk);
        String chunkData = new String(chunk); // Convertir le chunk en texte

        // Ajouter à la liste de séquence des chunks
        chunkSequence.add(chunkHash);

        System.out.println("\n📦 Chunk " + chunkCount);
        System.out.println("  ○ Hash   : " + chunkHash);
        System.out.println("  ○ Taille : " + chunk.length + " bytes");

        // Vérifier si le chunk est déjà stocké
        if (!chunkMapping.containsKey(chunkHash)) {
            chunkMapping.put(chunkHash, chunkData);
            byte[] compressedChunk = compressor.compressChunkWithZstd(chunk);
            chunkStorage.storeChunk(chunkHash, compressedChunk);

            System.out.println("  ⚡ Compression appliquée");
            System.out.println("  ⚡ Taille compressée : " + compressedChunk.length + " bytes");
        } else {
            System.out.println("  🔁 Chunk déjà existant (doublon détecté) !");
        }

        System.out.println("-----------------------------------------");
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
