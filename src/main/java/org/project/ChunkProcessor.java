package org.project;

import java.io.File;
import java.io.IOException;
import java.util.List;
import org.xerial.snappy.Snappy;

public class ChunkProcessor {
    private final ChunkStorage chunkStorage;
    private final FileChunker fileChunker;

    public ChunkProcessor(ChunkStorage chunkStorage, FileChunker fileChunker) {
        this.chunkStorage = chunkStorage;
        this.fileChunker = fileChunker;
    }

    public void processFile(File file) throws IOException {
        List<byte[]> chunks = fileChunker.getChunks(file);
        int chunkCount = 1;

        for (byte[] chunk : chunks) {
            String chunkHash = Blake3Hasher.hashChunk(chunk);

            if (!chunkStorage.contains(chunkHash)) {
                displayChunk(chunkCount, chunkHash, chunk);

                // Compression du chunk avec Snappy
                byte[] compressedChunk = Snappy.compress(chunk);

                // Stocker le chunk compressé
                chunkStorage.storeChunk(chunkHash, compressedChunk);

                // Affichage des tailles
                System.out.println("Chunk #" + chunkCount + " info taille original et compressé :");
                System.out.println("  - Taille originale : " + chunk.length + " bytes");
                System.out.println("  - Taille compressée : " + compressedChunk.length + " bytes");
            } else {
                System.out.println("Chunk #" + chunkCount + " déjà existant (doublon détecté).");
            }

            chunkCount++;
        }
    }

    private void displayChunk(int chunkNumber, String chunkId, byte[] chunkData) {
        System.out.println(
                "Chunk (" + chunkNumber + ")" + " : " + chunkId + ", Size: " + chunkData.length + " bytes");
    }
}
