package org.project;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.io.File;
import java.nio.file.Files;

public class CompressionPerformanceTest {
        private final LZ4ChunkCompressor compressor = new LZ4ChunkCompressor();
        private final FileChunker fileChunker = new FileChunker();

        public void runTest(File file) throws IOException {
                byte[] fileData = Files.readAllBytes(file.toPath());
                // Compression globale
                long startGlobal = System.nanoTime();
                byte[] compressedGlobal = compressor.compressChunk(fileData);
                long endGlobal = System.nanoTime();
                System.out.println("Compression globale : " + (endGlobal - startGlobal) / 1e6 + " ms, taille : "
                                + compressedGlobal.length + " octets");

                long startChunks = System.nanoTime();
                List<byte[]> chunks = fileChunker.getChunks(file);
                int  compressedChunkSize = getSizeCompressedChunks(chunks);
                long endChunks = System.nanoTime();

                double chunkTimeMs = (endChunks - startChunks) / 1e6;

                System.out.println(
                                "Compression par chunk : " + chunkTimeMs + " ms, taille totale : " + compressedChunkSize
                                                + " octets");
                System.out.println("⏱️ Temps découpage des chunks: " + fileChunker.getSlicingTime() + " ms");
        }

        private int getSizeCompressedChunks(List<byte[]> chunks) throws IOException {
                int compressedChunkSize = 0;

                for (byte[] chunk : chunks) {
                        // compression du chuck avec LZ4
                        // byte[] compressedChunk = compressor.compressChunk(chunk);

                        // Compression de chunk avec Zstd
                        byte[] compressedChunk = ZstdCompressor.compressChunk(chunk);
                        compressedChunkSize += compressedChunk.length;

                        // Compression du chunk avec Snappy
                        // byte[] compressedChunk = Snappy.compress(chunk);

                }

                return compressedChunkSize;
        }
}
