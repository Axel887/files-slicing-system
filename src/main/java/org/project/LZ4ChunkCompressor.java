package org.project;

import net.jpountz.lz4.*;
import java.util.Arrays;

public class LZ4ChunkCompressor {
    private final LZ4Factory factory = LZ4Factory.fastestInstance();
    private final LZ4Compressor compressor = factory.fastCompressor();

    /**
     * Compresse le tableau d'octets donné.
     * 
     * @param data Le chunk à compresser.
     * @return Le tableau d'octets compressé.
     */
    public byte[] compressChunk(byte[] data) {
        int maxCompressedLength = compressor.maxCompressedLength(data.length);
        byte[] compressed = new byte[maxCompressedLength];
        int compressedLength = compressor.compress(data, 0, data.length, compressed, 0, maxCompressedLength);
        return Arrays.copyOf(compressed, compressedLength);
    }
}