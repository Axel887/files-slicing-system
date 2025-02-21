package org.project.service;

import org.project.utils.JsonUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Base64;

public class FileReconstructor {

    public void reconstructFile() throws IOException {
        File chunksFile = new File("chunks.json");
        File resultFile = new File("result.json");

        if (!chunksFile.exists() || !resultFile.exists()) {
            System.err.println("❌ Erreur : Les fichiers chunks.json ou result.json sont introuvables !");
            return;
        }

        Map<String, String> chunkMapping = JsonUtils.readFromJsonFile("chunks.json", Map.class);
        Map<String, Object> resultData = JsonUtils.readFromJsonFile("result.json", Map.class);

        if (chunkMapping == null || resultData == null) {
            System.err.println("❌ Erreur : Impossible de charger les données JSON !");
            return;
        }

        List<String> chunkSequence = (List<String>) resultData.get("chunks");
        String extension = (String) resultData.getOrDefault("extension", "");

        if (chunkSequence == null || chunkSequence.isEmpty()) {
            System.err.println("❌ Erreur : Aucune donnée de chunks trouvée dans result.json !");
            return;
        }

        String outputFileName = "fichier_reconstruit" + extension;
        File outputFile = new File(outputFileName);

        System.out.println("\n📂 Reconstruction du fichier : " + outputFileName);

        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            for (String chunkHash : chunkSequence) {
                String chunkData = chunkMapping.get(chunkHash);

                if (chunkData != null) {
                    byte[] chunkBytes = Base64.getDecoder().decode(chunkData);
                    fos.write(chunkBytes);
                } else {
                    System.err.println("⚠️ Erreur : Chunk non trouvé pour le hash " + chunkHash);
                }
            }
        }

        System.out.println("✅ Fichier reconstruit avec succès : " + outputFileName);
    }
}