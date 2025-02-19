package org.project;

import java.util.HashMap;
import java.util.Map;

public class InMemoryChunkStorage implements ChunkStorage {
    private final Map<String, byte[]> storage = new HashMap<>();

    @Override
    public void storeChunk(String chunkId, byte[] chunkData) {
        // On stocke le chunk s’il n’existe pas déjà
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

    public void displayChunks() {
        for (Map.Entry<String, byte[]> entry : storage.entrySet()) {
            String chunkId = entry.getKey();
            byte[] chunkData = entry.getValue();
            System.out.println("Chunk ID: " + chunkId + ", Size: " + chunkData.length + " bytes");
        }
    }
}
