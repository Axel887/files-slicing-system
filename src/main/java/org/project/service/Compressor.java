package org.project.service;

import net.jpountz.lz4.*;
import com.github.luben.zstd.Zstd;
import org.xerial.snappy.Snappy;

import java.io.IOException;
import java.util.Arrays;

public class Compressor {
    private final LZ4Factory lz4Factory = LZ4Factory.fastestInstance();
    private final LZ4Compressor lz4Compressor = lz4Factory.fastCompressor();
    private final LZ4FastDecompressor lz4Decompressor = lz4Factory.fastDecompressor();

    /**
     * Compression avec LZ4.
     */
    public byte[] compressChunkWithLZ4(byte[] chunk) {
        int maxCompressedLength = lz4Compressor.maxCompressedLength(chunk.length);
        byte[] compressed = new byte[maxCompressedLength];
        int compressedLength = lz4Compressor.compress(chunk, 0, chunk.length, compressed, 0, maxCompressedLength);
        return Arrays.copyOf(compressed, compressedLength);
    }

    /**
     * Compression avec Zstd.
     */
    public byte[] compressChunkWithZstd(byte[] chunk) {
        return Zstd.compress(chunk);
    }

    /**
     * Décompression avec LZ4.
     */
    public byte[] decompressChunkWithLZ4(byte[] compressedChunk, int originalSize) {
        byte[] decompressed = new byte[originalSize];
        lz4Decompressor.decompress(compressedChunk, 0, decompressed, 0, originalSize);
        return decompressed;
    }

    /**
     * Décompression avec Zstd.
     */
    public byte[] decompressChunkWithZstd(byte[] compressedChunk) {
        long decompressedSize = Zstd.decompressedSize(compressedChunk);
        return Zstd.decompress(compressedChunk, (int) decompressedSize);
    }

    /**
     * Compression avec Snappy.
     */
    public byte[] compressChunkWithSnappy(byte[] chunk) {
        try {
            return Snappy.compress(chunk);
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la compression Snappy", e);
        }
    }

    /**
     * Décompression avec Snappy.
     */
    public byte[] decompressChunkWithSnappy(byte[] compressedChunk) {
        try {
            return Snappy.uncompress(compressedChunk);
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la décompression Snappy", e);
        }
    }
}
