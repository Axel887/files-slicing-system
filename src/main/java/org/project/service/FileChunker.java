package org.project.service;

import org.rabinfingerprint.polynomial.Polynomial;
import org.rabinfingerprint.fingerprint.RabinFingerprintLongWindowed;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileChunker {

    // ✅ Augmenter la taille des chunks pour mieux compresser
    private static final int WINDOW_SIZE = 48; // Fenêtre d'analyse (augmente stabilité)
    private static final int MASK = 0x3FFF; // Condition de découpe (empreinte & MASK == 0)
    private static final int MIN_CHUNK_SIZE = 16 * 1024; // 16 Ko (min)
    private static final int MAX_CHUNK_SIZE = 64 * 1024; // 64 Ko (max)

    public List<byte[]> getChunks(File file) throws IOException {
        Polynomial poly = Polynomial.createFromLong(0x3DA3358B4DC173L);
        RabinFingerprintLongWindowed window = new RabinFingerprintLongWindowed(poly, WINDOW_SIZE);

        List<byte[]> chunks = new ArrayList<>();
        ByteArrayOutputStream chunkBuffer = new ByteArrayOutputStream();

        // Lecture optimisée du fichier
        try (BufferedInputStream fileStream = new BufferedInputStream(new FileInputStream(file))) {
            int byteRead;
            while ((byteRead = fileStream.read()) != -1) {
                window.pushByte((byte) byteRead);
                chunkBuffer.write(byteRead);

                if (chunkBuffer.size() >= MIN_CHUNK_SIZE) {
                    long fingerprint = window.getFingerprintLong();
                    if ((fingerprint & MASK) == 0 || chunkBuffer.size() >= MAX_CHUNK_SIZE) {
                        chunks.add(chunkBuffer.toByteArray());
                        chunkBuffer.reset();
                    }
                }
            }
        }

        // Ajouter le dernier chunk restant
        if (chunkBuffer.size() > 0) {
            chunks.add(chunkBuffer.toByteArray());
        }

        return chunks;
    }
}
