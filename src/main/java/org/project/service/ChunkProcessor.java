package org.project.service;

import org.project.utils.JsonUtils;
import java.io.*;
import java.util.*;

public class ChunkProcessor {
    private final FileChunker fileChunker;
    private final Set<String> existingChunks;
    private double timeSlicingFile;
    private double timeCompressionFile;
    private final Compressor compressor;

    public ChunkProcessor(FileChunker fileChunker) {
        this.fileChunker = fileChunker;
        this.existingChunks = loadExistingChunks();
        this.compressor = new Compressor();
    }

    private Set<String> loadExistingChunks() {
        File chunksDirectory = new File("chunks");
        Set<String> chunkSet = new HashSet<>();
        if (chunksDirectory.exists()) {
            for (File file : Objects.requireNonNull(chunksDirectory.listFiles())) {
                if (file.getName().endsWith(".zst")) {
                    chunkSet.add(file.getName().replace(".zst", ""));
                }
            }
        }
        return chunkSet;
    }

    public void processFile(File file, boolean withMessage) throws IOException {
        if (withMessage) {
            System.out.println("\n=========================================");
            System.out.println(" 📂 Traitement du fichier : " + file.getName());
            System.out.println("=========================================\n");
        }

        // 🔹 Récupérer l'extension du fichier
        String extension = "";
        int lastDotIndex = file.getName().lastIndexOf(".");
        if (lastDotIndex != -1) {
            extension = file.getName().substring(lastDotIndex);
        }

        long startTimeSlicing = System.nanoTime();
        List<byte[]> chunks = this.fileChunker.getChunks(file);
        long endTimeSlicing = System.nanoTime();
        this.timeSlicingFile = (endTimeSlicing - startTimeSlicing) / 1e6;

        List<String> chunkSequence = new ArrayList<>();
        Map<String, Integer> compressedChunkSizes = new HashMap<>();
        Map<String, String> compressedChunksData = new LinkedHashMap<>();
        int chunkCount = 1;
        int totalOriginalSize = 0;
        int totalCompressedSize = 0;
        int newChunksStored = 0;
        int duplicateChunks = 0;
        int totalOriginalChunkSize = 0;
        int totalStoredChunkSize = 0;

        File chunksDirectory = new File("chunks");
        if (!chunksDirectory.exists()) {
            chunksDirectory.mkdir();
        }

        long startTimeCompression = System.nanoTime();
        for (byte[] chunk : chunks) {
            String chunkHash = Blake3Hasher.hashChunk(chunk);
            int chunkSize = chunk.length;
            totalOriginalSize += chunkSize;
            totalOriginalChunkSize += chunkSize;

            File chunkFile = new File("chunks/" + chunkHash + ".zst");
            byte[] compressedChunk;

            if (!chunkFile.exists()) {
                // 🔹 Nouveau chunk → compression et stockage
                compressedChunk = this.compressor.compressChunkWithZstd(chunk);
                totalCompressedSize += compressedChunk.length;
                compressedChunkSizes.put(chunkHash, compressedChunk.length);
                newChunksStored++;
                totalStoredChunkSize += chunkSize; // Ajoute la taille uniquement pour les chunks uniques

                try (FileOutputStream fos = new FileOutputStream(chunkFile)) {
                    fos.write(compressedChunk);
                }

                existingChunks.add(chunkHash);

                if (withMessage) {
                    System.out.println("\n📦 Chunk " + chunkCount + " [Nouveau]");
                    System.out.println("  ○ Hash   : " + chunkHash);
                    System.out.println("  ○ Taille originale : " + chunk.length + " bytes");
                    System.out.println("  ⚡ Compression appliquée");
                    System.out.println("  ⚡ Taille compressée : " + compressedChunk.length + " bytes");
                }
            } else {
                duplicateChunks++;
                compressedChunk = readFile(chunkFile); // Charger le chunk déjà existant

                if (withMessage) {
                    System.out.println("\n📦 Chunk " + chunkCount + " [Déjà existant]");
                    System.out.println("  ○ Hash   : " + chunkHash);
                    System.out.println("  ○ Taille originale : " + chunk.length + " bytes");
                    System.out.println("  ✅ Chunk déjà stocké, réutilisation.");
                }
            }

            // 🔹 Stocker TOUS les chunks compressés du fichier actuel
            compressedChunksData.put(chunkHash, Base64.getEncoder().encodeToString(compressedChunk));

            chunkSequence.add(chunkHash);
            chunkCount++;
        }
        long endTimeCompression = System.nanoTime();
        this.timeCompressionFile = (endTimeCompression - startTimeCompression) / 1e6;

        double deduplicationRatio = ((double) duplicateChunks / (chunkSequence.isEmpty() ? 1 : chunkSequence.size())) * 100;
        double storageGain = ((double) (totalOriginalChunkSize - totalStoredChunkSize) / totalOriginalChunkSize) * 100;

        // 🔹 Sauvegarder dans `compressed_chunks.json`
        JsonUtils.saveToJsonFile(compressedChunksData, "compressed_chunks.json");

        if (withMessage) {
            File resultFile = new File("result.json");
            long compressedSize = resultFile.length(); // Taille du fichier result.json

            System.out.println("\n📊 Résumé du traitement :");
            System.out.println("✅ " + chunkSequence.size() + " chunks analysés.");
            System.out.println("✅ " + newChunksStored + " nouveaux chunks stockés.");
            System.out.println("✅ " + duplicateChunks + " chunks supprimés (déduplication)");
            System.out.println("✅ Ratio de déduplication : " + String.format("%.2f", deduplicationRatio) + "%");
            System.out.println("✅ Gain de stockage grâce à la déduplication : " + String.format("%.2f", storageGain) + "%");
            System.out.println("✅ Taille originale : " + totalOriginalSize + " bytes");
            System.out.println("✅ Taille compressée : " + compressedSize + " bytes");

            // 🔹 Enregistrer les métadonnées dans result.json
            Map<String, Object> resultData = new HashMap<>();
            resultData.put("chunks", chunkSequence);
            resultData.put("extension", extension);
            resultData.put("compressedChunkSizes", compressedChunkSizes);
            JsonUtils.saveToJsonFile(resultData, "result.json");

            System.out.println("\n✅ Fichiers de sauvegarde mis à jour :");
            System.out.println("  📄 result.json  → Métadonnées de reconstruction enregistrées.");
            System.out.println("  📄 compressed_chunks.json  → Données compressées du fichier actuel.");
        }
    }

    private byte[] readFile(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file);
             ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
            byte[] temp = new byte[8192];
            int bytesRead;
            while ((bytesRead = fis.read(temp)) != -1) {
                buffer.write(temp, 0, bytesRead);
            }
            return buffer.toByteArray();
        }
    }

    public double getTimeSlicingFile() {
        return this.timeSlicingFile;
    }

    public double getTimeCompressionFile() {
        return this.timeCompressionFile;
    }
}
