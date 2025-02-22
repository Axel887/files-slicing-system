package org.project.utils;

import org.project.service.ChunkProcessor;
import org.project.service.FileReconstructor;
import org.project.service.Compressor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.Base64;

public class PerformanceUtils {
    private final ChunkProcessor chunkProcessor;
    private final byte[] fileData;
    private final File file;
    private final Compressor compressor;
    private final FileReconstructor fileReconstructor;

    public PerformanceUtils(ChunkProcessor processor, File file) throws IOException {
        this.chunkProcessor = processor;
        this.fileData = Files.readAllBytes(file.toPath());
        this.file = file;
        this.compressor = new Compressor();
        this.fileReconstructor = new FileReconstructor();
    }

    public void compareCompressionPerformance() throws IOException {
        System.out.println("\n=== 📊 Comparaison des performances de compression ===");

        long fileOriginalSize = fileData.length;

        // 🔹 Compression globale du fichier
        long startTimeCompressionGlobal = System.nanoTime();
        byte[] compressedGlobal = this.compressor.compressChunkWithZstd(this.fileData);
        long endTimeCompressionGlobal = System.nanoTime();

        // 🔹 Découpage + compression par chunks
        this.chunkProcessor.processFile(this.file, false);
        double timeSlicing = this.chunkProcessor.getTimeSlicingFile();
        double timeCompressionChunks = this.chunkProcessor.getTimeCompressionFile();

        // 🔹 Charger les tailles réelles des chunks compressés
        File compressedChunksFile = new File("compressed_chunks.json");
        long totalCompressedChunksSize = 0;

        if (compressedChunksFile.exists()) {
            Map<String, String> compressedChunksMap = JsonUtils.readFromJsonFile("compressed_chunks.json", Map.class);
            for (String base64Chunk : compressedChunksMap.values()) {
                byte[] chunkBytes = Base64.getDecoder().decode(base64Chunk);  // ✅ Conversion correcte
                totalCompressedChunksSize += chunkBytes.length;
            }
        } else {
            System.err.println("⚠️ `compressed_chunks.json` introuvable. Comparaison impossible.");
        }

        // 🔹 Décompression des chunks
        long startTimeDecompressionChunks = System.nanoTime();
        this.fileReconstructor.reconstructFile();
        long endTimeDecompressionChunks = System.nanoTime();

        // 🔹 Affichage des résultats
        System.out.println("📌 Taille originale du fichier : " + fileOriginalSize + " octets");

        System.out.println("\n📉 Compression globale : ");
        System.out.println("   🔹 Taille compressée : " + compressedGlobal.length + " octets");
        System.out.println("   🔹 Temps : " + ((endTimeCompressionGlobal - startTimeCompressionGlobal) / 1e6) + " ms");

        System.out.println("\n📦 Compression par chunks : ");
        System.out.println("   🔹 Taille compressée totale (somme des chunks) : " + totalCompressedChunksSize + " octets");
        System.out.println("   🔹 Temps de découpage : " + timeSlicing + " ms");
        System.out.println("   🔹 Temps de compression : " + timeCompressionChunks + " ms");
        System.out.println("   🔹 Temps de décompression : " + ((endTimeDecompressionChunks - startTimeDecompressionChunks) / 1e6) + " ms");

        // 🔹 Comparaison des ratios de compression
        double ratioGlobal = ((double) compressedGlobal.length / fileOriginalSize) * 100;
        double ratioChunks = ((double) totalCompressedChunksSize / fileOriginalSize) * 100;

        System.out.println("\n📊 Comparaison des ratios de compression : ");
        System.out.println("   🔹 Compression globale : " + String.format("%.2f", 100 - ratioGlobal) + "% de réduction");
        System.out.println("   🔹 Compression par chunks : " + String.format("%.2f", 100 - ratioChunks) + "% de réduction");

        System.out.println("\n🚀 Comparaison terminée !");
    }
}
