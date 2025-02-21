package org.project.performance.utils;

import org.project.service.ChunkProcessor;
import org.project.service.FileChunker;
import org.project.storage.ChunkStorage;
import org.project.service.Compressor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class PerformanceUtils {
    private final ChunkProcessor chunkProcessor;
    private final byte[] fileData;
    private final FileChunker fileChunker;
    private final File file;
    private final Compressor compressor;

    public PerformanceUtils(ChunkProcessor processor, File file) throws IOException {
        this.fileChunker = new FileChunker();
        this.chunkProcessor = processor;
        this.fileData = Files.readAllBytes(file.toPath());
        this.file = file;
        this.compressor = new Compressor();
    }

    public void compareCompressionPerformance() throws IOException {
        System.out.println("\n=== Comparaison des performances de compression ===");

        long fileOriginalSize = fileData.length;

        // 🔹 Compression globale du fichier
        long startTimeCompressionGlobal = System.nanoTime();
        byte[] compressedGlobal = this.compressor.compressChunkWithZstd(this.fileData);
        long endTimeCompressionGlobal = System.nanoTime();

        // 🔹 Découpage du fichier en chunks
        long startTimeSlicing = System.nanoTime();
        List<byte[]> chunks = this.fileChunker.getChunks(this.file);
        long endTimeSlicing = System.nanoTime();

        // 🔹 Compression des chunks
        long startChunks = System.nanoTime();
        this.chunkProcessor.compressChunksWithoutMessage(chunks);
        long endChunks = System.nanoTime();

        ChunkStorage chunkStorage = this.chunkProcessor.getChunkStorage();

        // 🔹 Décompression des chunks
        long startTimeDecompressionChunks = System.nanoTime();
        this.decompressChunks(chunkStorage);
        long endTimeDecompressionChunks = System.nanoTime();

        // 🔹 Calcul des tailles
        int compressedChunkSize = calculateTotalSize(chunkStorage);
        int totalChunks = chunks.size();

        // 🔹 Temps d'exécution
        double compressedGlobalTime = (endTimeCompressionGlobal - startTimeCompressionGlobal) / 1e6;
        double slicingTime = (endTimeSlicing - startTimeSlicing) / 1e6;
        double chunkCompressedTime = (endChunks - startChunks) / 1e6;
        double chunkDecompressedTime = (endTimeDecompressionChunks - startTimeDecompressionChunks) / 1e6;

        // 🔹 Calcul des gains de compression
        double globalCompressionRatio = (1 - ((double) compressedGlobal.length / fileOriginalSize)) * 100;
        double chunkCompressionRatio = (1 - ((double) compressedChunkSize / fileOriginalSize)) * 100;

        // 🔹 Facteur de réduction
        double globalReductionFactor = (double) fileOriginalSize / compressedGlobal.length;
        double chunkReductionFactor = (double) fileOriginalSize / compressedChunkSize;

        // 🔹 Ratio de déduplication

        // 🔹 Affichage des résultats
        System.out.println("📌 Fichier original : " + fileOriginalSize + " octets");
        System.out.println("📉 Compression globale : ");
        System.out.println("   🔹 Taille compressée : " + compressedGlobal.length + " octets");
        System.out.println("   🔹 Gain : " + String.format("%.2f", globalCompressionRatio) + "%");
        System.out.println("   🔹 Facteur de réduction : " + String.format("%.2fx", globalReductionFactor));
        System.out.println("   🔹 Temps : " + compressedGlobalTime + " ms");

        System.out.println("📦 Compression par chunks : ");
        System.out.println("   🔹 Taille compressée totale : " + compressedChunkSize + " octets");
        System.out.println("   🔹 Gain : " + String.format("%.2f", chunkCompressionRatio) + "%");
        System.out.println("   🔹 Facteur de réduction : " + String.format("%.2fx", chunkReductionFactor));
        System.out.println("   🔹 Temps de découpage : " + slicingTime + " ms");
        System.out.println("   🔹 Temps de compression : " + chunkCompressedTime + " ms");
        System.out.println("   🔹 Temps de décompression : " + chunkDecompressedTime + " ms");
        System.out.println("   🔹 Nombre total de chunks : " + totalChunks);
    }

    private int calculateTotalSize(ChunkStorage storage) {
        int totalSize = 0;

        for (byte[] chunk : storage.getAllChunks()) {
            totalSize += chunk.length;
        }
        return totalSize;
    }

    public void decompressChunks(ChunkStorage compressedStorage) {
        for (byte[] compressedChunk : compressedStorage.getAllChunks()) {
            //byte[] decompressedChunk = compressor.decompressChunkWithZstd(compressedChunk);
            compressor.decompressChunkWithZstd(compressedChunk);
            //boolean isSame = new String(compressedChunk).equals(new String(decompressedChunk));
            //System.out.println(new String(compressedChunk));
            //System.out.println(new String(decompressedChunk)); // Afficher en texte (si lisible)
        }
    }
}
