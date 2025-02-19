package org.project;

import com.github.luben.zstd.Zstd;
public class ZstdCompressor {
    public static byte[] compressChunk(byte[] chunk) {
        return Zstd.compress(chunk);
    }

    public static byte[] decompressChunk(byte[] compressedChunk) {
        long decompressedSize = Zstd.decompressedSize(compressedChunk);
        return Zstd.decompress(compressedChunk, (int) decompressedSize);
    }
}


