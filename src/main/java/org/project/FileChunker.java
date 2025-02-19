package org.project;

import org.rabinfingerprint.polynomial.Polynomial;
import org.rabinfingerprint.fingerprint.RabinFingerprintLongWindowed;
import com.google.common.io.ByteStreams;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileChunker {

    // Paramètres de configuration
    private static final int WINDOW_SIZE = 48; // bytes
    private static final int MASK = 0xFFF; // Condition de découpe : fingerprint & MASK == 0
    private static final int MIN_CHUNK_SIZE = 1024; // Taille minimale d'un chunk en octets
    private static final int MAX_CHUNK_SIZE = 8192; // Taille maximale d'un chunk en octets

    public List<byte[]> getChunks(File file) throws IOException {
        Polynomial poly = Polynomial.createFromLong(0x3DA3358B4DC173L);
        RabinFingerprintLongWindowed window = new RabinFingerprintLongWindowed(poly, WINDOW_SIZE);

        byte[] fileBytes = ByteStreams.toByteArray(new FileInputStream(file));
        List<byte[]> chunks = new ArrayList<>();
        ByteArrayOutputStream chunkBuffer = new ByteArrayOutputStream();
        int chunkCount = 0;

        for (byte b : fileBytes) {
            window.pushByte(b);
            chunkBuffer.write(b);

            if (chunkBuffer.size() >= MIN_CHUNK_SIZE) {
                long fingerprint = window.getFingerprintLong();
                if ((fingerprint & MASK) == 0 || chunkBuffer.size() >= MAX_CHUNK_SIZE) {
                    byte[] chunk = chunkBuffer.toByteArray();
                    chunks.add(chunk);

                    System.out.println("Chunk " + (++chunkCount) + " (" + chunk.length + " octets) - fingerprint: "
                            + Long.toString(fingerprint, 16));
                    chunkBuffer.reset();
                }
            }
        }

        if (chunkBuffer.size() > 0) {
            long fingerprint = window.getFingerprintLong();
            byte[] chunk = chunkBuffer.toByteArray();
            chunks.add(chunk);

            System.out.println("Chunk final " + (++chunkCount) + " (" + chunk.length + " octets) - fingerprint: "
                    + Long.toString(fingerprint, 16));

        }

        return chunks;
    }
}
