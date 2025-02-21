package org.project.storage;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class InMemoryChunkStorage implements ChunkStorage {
    private final Map<String, byte[]> storage = new HashMap<>();

    @Override
    public void storeChunk(String chunkId, byte[] chunkData) {
        storage.putIfAbsent(chunkId, chunkData);
    }

    @Override
    public byte[] getChunk(String chunkId) {
        return storage.get(chunkId);
    }

    @Override
    public boolean contains(String chunkId) {
        return storage.containsKey(chunkId);
    }

    @Override
    public Set<String> getAllChunkIds() {
        return storage.keySet();
    }

    @Override
    public void removeChunk(String chunkId) {
        storage.remove(chunkId);
    }

    @Override
    public byte[][] getAllChunks() {
        return storage.values().toArray(new byte[0][]);
    }
}
