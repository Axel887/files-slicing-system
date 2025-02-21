package org.project.service;

import org.project.utils.JsonUtils;
import org.project.storage.ChunkStorage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class FileReconstructor {
    private final Compressor compressor;

    public FileReconstructor() {
        this.compressor = new Compressor();
    }

    /**
     * Reconstruit le fichier original à partir des chunks stockés.
     */
    public void reconstructFile(String outputFileName) throws IOException {
        // Charger les chunks compressés depuis chunks.json
        Map<String, String> chunkMapping = JsonUtils.readFromJsonFile("chunks.json", Map.class);
        // Charger la séquence des chunks depuis result.json
        Map<String, Object> resultData = JsonUtils.readFromJsonFile("result.json", Map.class);
        List<String> chunkSequence = (List<String>) resultData.get("chunks");

        if (chunkMapping == null || chunkSequence == null) {
            System.err.println("❌ Erreur : Impossible de charger les données pour la reconstruction !");
            return;
        }

        // Reconstruction du fichier
        File outputFile = new File(outputFileName);
        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            for (String chunkHash : chunkSequence) {
                String compressedChunkData = chunkMapping.get(chunkHash);

                if (compressedChunkData != null) {
                    // Convertir en bytes pour la décompression
                    byte[] compressedBytes = compressedChunkData.getBytes();

                    // 🔹 Décompression du chunk
                    byte[] decompressedBytes = compressor.decompressChunkWithZstd(compressedBytes);

                    fos.write(decompressedBytes);
                }
            }
        }
    }
}
