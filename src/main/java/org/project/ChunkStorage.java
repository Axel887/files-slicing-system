package org.project;

import java.util.Set;

public interface ChunkStorage {
    void storeChunk(String chunkId, byte[] chunkData);

    byte[] getChunk(String chunkId);

    boolean contains(String chunkId);

    Set<String> getAllChunkIds();
}
