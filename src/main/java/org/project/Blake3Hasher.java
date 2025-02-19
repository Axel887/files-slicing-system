package org.project;

import org.bouncycastle.crypto.digests.Blake3Digest;

public class Blake3Hasher {


     //Calcule le hash BLAKE3 d'un chunk.

    public static String hashChunk(byte[] chunk) {
        Blake3Digest digest = new Blake3Digest(256); // 256 bits de sortie
        digest.update(chunk, 0, chunk.length);
        byte[] hashBytes = new byte[32]; // 256 bits = 32 octets
        digest.doFinal(hashBytes, 0);
        return bytesToHex(hashBytes);
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    }


}