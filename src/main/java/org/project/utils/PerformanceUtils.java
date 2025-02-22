package org.project.utils;

import org.project.service.ChunkProcessor;
import org.project.service.FileChunker;
import org.project.service.FileReconstructor;
import org.project.storage.ChunkStorage;
import org.project.service.Compressor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

public class PerformanceUtils {
    private final ChunkProcessor chunkProcessor;
    private final byte[] fileData;
    private final FileChunker fileChunker;
    private final File file;
    private final Compressor compressor;
    private final FileReconstructor fileReconstructor;

    public PerformanceUtils(ChunkProcessor processor, File file) throws IOException {
        this.fileChunker = new FileChunker();
        this.chunkProcessor = processor;
        this.fileData = Files.readAllBytes(file.toPath());
        this.file = file;
        this.compressor = new Compressor();
        this.fileReconstructor = new FileReconstructor();
    }

    public void compareCompressionPerformance() throws IOException {
        System.out.println("\n=== Comparaison des performances de compression ===");

        long fileOriginalSize = fileData.length;

        // 🔹 Compression globale du fichier
        long startTimeCompressionGlobal = System.nanoTime();
        byte[] compressedGlobal = this.compressor.compressChunkWithZstd(this.fileData);
        long endTimeCompressionGlobal = System.nanoTime();

        // 🔹 Découpage + compression chunks
        this.chunkProcessor.processFile(this.file, false);
        double timeSlicing = this.chunkProcessor.getTimeSlicingFile();
        double timeCompressionChunks = this.chunkProcessor.getTimeCompressionFile();

        // 🔹 Taille réelle de result.json (fichier contenant les chunks compressés)
        File resultFile = new File("result.json");
        long resultFileSize = resultFile.exists() ? resultFile.length() : 0;

        // 🔹 Décompression des chunks
        long startTimeDecompressionChunks = System.nanoTime();
        this.fileReconstructor.reconstructFile();
        long endTimeDecompressionChunks = System.nanoTime();

        // 🔹 Affichage des résultats
        System.out.println("📌 Fichier original : " + fileOriginalSize + " octets");
        System.out.println("📉 Compression globale : ");
        System.out.println("   🔹 Taille compressée : " + compressedGlobal.length + " octets");
        System.out.println("   🔹 Temps : " + ((endTimeCompressionGlobal - startTimeCompressionGlobal) / 1e6) + " ms");


        System.out.println("📦 Compression par chunks : ");
        System.out.println("   🔹 Taille compressée totale (result.json) : " + resultFileSize + " octets");
        System.out.println("   🔹 Temps de découpage : " + timeSlicing + " ms");
        System.out.println("   🔹 Temps de compression : " + timeCompressionChunks + " ms");
        System.out.println("   🔹 Temps de décompression : " + ((endTimeDecompressionChunks - startTimeDecompressionChunks) / 1e6) + " ms");

    }

}
