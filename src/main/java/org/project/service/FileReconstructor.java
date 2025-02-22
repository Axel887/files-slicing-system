package org.project.service;

import com.github.luben.zstd.Zstd;
import java.io.*;
import java.util.List;
import java.util.Map;
import org.project.utils.JsonUtils;

public class FileReconstructor {

    public void reconstructFile() throws IOException {
        File resultFile = new File("result.json");
        if (!resultFile.exists()) {
            System.err.println("❌ Erreur : result.json introuvable !");
            return;
        }

        Map<String, Object> resultData = JsonUtils.readFromJsonFile("result.json", Map.class);
        if (resultData == null) {
            System.err.println("❌ Erreur : Impossible de charger result.json !");
            return;
        }

        List<String> chunkSequence = (List<String>) resultData.get("chunks");
        String extension = (String) resultData.get("extension");
        Map<String, Integer> compressedChunkSizes = (Map<String, Integer>) resultData.get("compressedChunkSizes");

        if (chunkSequence == null || chunkSequence.isEmpty()) {
            System.err.println("❌ Erreur : Aucune donnée de chunks trouvée !");
            return;
        }

        String outputFileName = "fichier_reconstruit" + extension;
        File outputFile = new File(outputFileName);
        System.out.println("\n📂 Reconstruction du fichier : " + outputFileName);

        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            for (String chunkHash : chunkSequence) {
                File chunkFile = new File("chunks/" + chunkHash + ".zst");
                if (!chunkFile.exists() || chunkFile.length() < 10) {
                    System.err.println("⚠️ Erreur : Chunk " + chunkHash + " introuvable ou corrompu !");
                    continue;
                }

                byte[] compressedChunk = readFile(chunkFile);

                // Vérifier si le fichier est réellement compressé avec Zstd
                if (!isZstdCompressed(compressedChunk)) {
                    System.err.println("⚠️ Erreur : Le chunk " + chunkHash + " n'est pas un fichier Zstd valide.");
                    continue;
                }

                long estimatedSize = Zstd.decompressedSize(compressedChunk);
                if (estimatedSize <= 0) estimatedSize = compressedChunk.length * 5;

                int attempt = 0;
                while (attempt < 3) {
                    try {
                        byte[] decompressedChunk = new byte[(int) estimatedSize];
                        long actualDecompressedSize = Zstd.decompress(decompressedChunk, compressedChunk);

                        if (actualDecompressedSize > decompressedChunk.length) {
                            throw new RuntimeException("Taille du tampon insuffisante !");
                        }

                        fos.write(decompressedChunk, 0, (int) actualDecompressedSize);
                        break;

                    } catch (RuntimeException e) {
                        System.err.println("⚠️ Tampon insuffisant, tentative " + (attempt + 1));
                        estimatedSize *= 2;
                        attempt++;
                    }
                }
            }
        }

        System.out.println("✅ Fichier reconstruit avec succès : " + outputFileName);
    }

    private byte[] readFile(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file);
             ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
            byte[] temp = new byte[8192];
            int bytesRead;
            while ((bytesRead = fis.read(temp)) != -1) {
                buffer.write(temp, 0, bytesRead);
            }
            return buffer.toByteArray();
        }
    }

    private boolean isZstdCompressed(byte[] data) {
        return data.length > 4 && data[0] == 0x28 && data[1] == (byte) 0xb5;
    }
}
