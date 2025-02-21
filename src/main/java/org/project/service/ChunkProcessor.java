package org.project.service;

import org.project.storage.ChunkStorage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
        int chunkCount = 1;

        for (byte[] chunk : chunks) {
            processChunk(chunk, chunkCount);
            chunkCount++;
        }

        System.out.println((chunkCount - 1) + " chunks analysés.");
    }

    private void processChunk(byte[] chunk, int chunkCount) {
        String chunkHash = Blake3Hasher.hashChunk(chunk);
        boolean isDuplicate = chunkStorage.contains(chunkHash);
        System.out.println(chunk);
        System.out.println("📦 Chunk " + chunkCount);
        System.out.println("  ○ Hash   : " + chunkHash);
        System.out.println("  ○ Taille : " + chunk.length + " bytes");

        if (!isDuplicate) {
            byte[] compressedChunk = compressor.compressChunkWithZstd(chunk);
            chunkStorage.storeChunk(chunkHash, compressedChunk);

            System.out.println("  ⚡ Compression appliquée");
            System.out.println("  ⚡️ Taille compressée : " + compressedChunk.length + " bytes");
        } else {
            System.out.println(" ‼️ Chunk déjà existant (doublon détecté)");
        }

        System.out.println("-----------------------------------------\n");
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
