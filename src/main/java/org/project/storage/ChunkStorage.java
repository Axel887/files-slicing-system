package org.project.storage;

import java.util.Set;

public interface ChunkStorage {
    void storeChunk(String chunkId, byte[] chunkData);
    byte[] getChunk(String chunkId);
    boolean contains(String chunkId);
    Set<String> getAllChunkIds();
    void removeChunk(String chunkId);
    byte[][] getAllChunks(); // ✅ Nouvelle méthode pour récupérer tous les chunks
}
