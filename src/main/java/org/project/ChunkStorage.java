package org.project;

public interface ChunkStorage {
    void storeChunk(String chunkId, byte[] chunkData);
    byte[] getChunk(String chunkId);
    boolean contains(String chunkId);
}
