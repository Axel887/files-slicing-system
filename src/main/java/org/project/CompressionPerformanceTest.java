package org.project;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.io.File;
import java.nio.file.Files;

public class CompressionPerformanceTest {
    private final LZ4ChunkCompressor compressor = new LZ4ChunkCompressor();
    InMemoryChunkStorage storage = new InMemoryChunkStorage();
    FileChunker chunker = new FileChunker();
    ChunkProcessor processor = new ChunkProcessor(storage, chunker);

    public void runTest(File file) throws IOException {
        byte[] fileData = Files.readAllBytes(file.toPath());

        // Compression globale
        long startGlobal = System.nanoTime();
        byte[] compressedGlobal = compressor.compressChunk(fileData);
        long endGlobal = System.nanoTime();
        System.out.println("Compression globale : " + (endGlobal - startGlobal) / 1e6 + " ms, taille : "
                + compressedGlobal.length + " octets");

        // Compression par chunk avec un timer
        ChunkStorage storage = new InMemoryChunkStorage();
        long startChunks = System.nanoTime();
        processor.processFile(file);
        long endChunks = System.nanoTime();

        long chunkTimeMs = (endChunks - startChunks) / 1_000_000;

        // Calcul de la taille totale des chunks compressés
        int totalCompressedSize = storage.getAllChunkIds().stream()
                .mapToInt(id -> storage.getChunk(id).length)
                .sum();

        System.out.println(
                "Compression par chunk : " + chunkTimeMs + " ms, taille totale : " + totalCompressedSize + " octets");
    }
}
