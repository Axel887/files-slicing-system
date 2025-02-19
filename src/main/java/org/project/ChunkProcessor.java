package org.project;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ChunkProcessor {
    private final ChunkStorage chunkStorage;
    private final LZ4ChunkCompressor compressor = new LZ4ChunkCompressor();
    private final FileChunker fileChunker;

    public ChunkProcessor(ChunkStorage chunkStorage, FileChunker fileChunker) {
        this.chunkStorage = chunkStorage;
        this.fileChunker = fileChunker;
    }

    public void processFile(File file) throws IOException {
        List<byte[]> chunks = fileChunker.getChunks(file);

        for (byte[] chunk : chunks) {
            String chunkHash = Blake3Hasher.hashChunk(chunk);
            if (!chunkStorage.contains(chunkHash)) {
                chunkStorage.storeChunk(chunkHash, chunk);
            } else {
                System.out.println("Chunk dupliqué détecté : " + chunkHash);
            }
        }

        chunkStorage.displayChunks();
    }
}
