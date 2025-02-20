package org.project;

import java.io.File;
import java.io.IOException;
import java.util.List;
import org.xerial.snappy.Snappy;


public class ChunkProcessor {
    private final ChunkStorage chunkStorage;
    private final LZ4ChunkCompressor compressor = new LZ4ChunkCompressor();
    private final FileChunker fileChunker;

    public ChunkProcessor(ChunkStorage chunkStorage, FileChunker fileChunker) {
        this.chunkStorage = chunkStorage;
        this.fileChunker = fileChunker;
    }

    public void processFile(File file) throws IOException {
        System.out.println("\n=========================================");
        System.out.println(" 📂 Traitement du fichier : " + file.getName());
        System.out.println("=========================================\n");

        List<byte[]> chunks = fileChunker.getChunks(file);
        int chunkCount = 1;

        for (byte[] chunk : chunks) {
            String chunkHash = Blake3Hasher.hashChunk(chunk);
            boolean isDuplicate = chunkStorage.contains(chunkHash);

            System.out.println("📦 Chunk " + chunkCount);
            System.out.println("  ○ Hash   : " + chunkHash);
            System.out.println("  ○ Taille : " + chunk.length + " bytes");

            if (!isDuplicate) {

                // compression du chuck avec LZ4
                 byte[] compressedChunk = compressor.compressChunk(chunk);

                // Compression de chunk avec Zstd
                // byte[] compressedChunk = ZstdCompressor.compressChunk(chunk);
                // chunkStorage.storeChunk(chunkHash, compressedChunk);

                // Compression du chunk avec Snappy
                // byte[] compressedChunk = Snappy.compress(chunk);

                // Affichage des tailles
                System.out.println("  ⚡ Compression appliquée");
                System.out.println(" ⚡️ Taille compressée : " + compressedChunk.length + " bytes");
            } else {
                System.out.println(" ‼️ Chunk déjà existant (doublon détecté)");
            }

            System.out.println("-----------------------------------------\n");
            chunkCount++;
        }

        System.out.println((chunkCount - 1) + " chunks analysés.");
    }
}
