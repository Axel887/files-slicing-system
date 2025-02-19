package org.project;

import org.rabinfingerprint.polynomial.Polynomial;
import org.rabinfingerprint.fingerprint.RabinFingerprintLongWindowed;
import com.google.common.io.ByteStreams;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class FileChunker {

    // Paramètres de configuration
    private static final int WINDOW_SIZE = 48; // bytes
    private static final int MASK = 0xFFF; // Condition de découpe : fingerprint & MASK == 0
    private static final int MIN_CHUNK_SIZE = 1024; // Taille minimale d'un chunk en octets
    private static final int MAX_CHUNK_SIZE = 8192; // Taille maximale d'un chunk en octets
    private final ChunkStorage chunkStorage;

    public FileChunker(ChunkStorage storage) {
        this.chunkStorage = storage;
    }

    public void chunkFile(File file) throws IOException {
        Polynomial poly = Polynomial.createFromLong(0x3DA3358B4DC173L);
        // Création d'une fenêtre de fingerprinting avec la taille définie
        RabinFingerprintLongWindowed window = new RabinFingerprintLongWindowed(poly, WINDOW_SIZE);

        // Lecture du fichier entier en tableau de bytes (nécessite Guava)
        byte[] fileBytes = ByteStreams.toByteArray(new FileInputStream(file));

        ByteArrayOutputStream chunkBuffer = new ByteArrayOutputStream();
        int chunkCount = 0;

        // Parcours de chaque octet du fichier . Decoupage CDC
        for (byte b : fileBytes) {
            window.pushByte(b); // Met à jour la fenêtre glissante
            chunkBuffer.write(b); // Accumule les octets dans le chunk courant

            // Vérifie si la taille minimale est atteinte pour envisager une découpe
            if (chunkBuffer.size() >= MIN_CHUNK_SIZE) {
                long fingerprint = window.getFingerprintLong();
                // Si la condition de découpe est satisfaite ou si la taille maximale est
                // atteinte
                if ((fingerprint & MASK) == 0 || chunkBuffer.size() >= MAX_CHUNK_SIZE) {
                    byte[] chunk = chunkBuffer.toByteArray();

                    /*
                     * 
                     * Calcul de l'empreinte SHA-256 pour indexer le chunk a implementer ici par
                     * axel
                     * 
                     */

                    // pour l'instant j'utilise un hash tempraire qui est le meme

                    String chunkId = Integer.toString(chunkCount);

                    System.out.println("Chunk " + (++chunkCount) + " (" + chunk.length + " octets) - fingerprint: "
                            + Long.toString(fingerprint, 16) +
                            " - hash: " + chunkId);
                    chunkBuffer.reset();

                    if (!chunkStorage.contains(chunkId)) {
                        chunkStorage.storeChunk(chunkId, chunk);
                    } else {
                        System.out.println("Chunk dupliqué détecté : " + chunkId);
                    }
                }
            }
        }
        // Traitement du dernier chunk, s'il reste des données
        if (chunkBuffer.size() > 0) {
            long fingerprint = window.getFingerprintLong();
            System.out.println("Chunk final " + (++chunkCount) + " (" + chunkBuffer.size() + " octets) - fingerprint: "
                    + Long.toString(fingerprint, 16));
        }
    }

}
