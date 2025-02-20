package org.project;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.io.File;
import java.nio.file.Files;

public class CompressionPerformanceTest {
        private final LZ4ChunkCompressor compressor = new LZ4ChunkCompressor();
        InMemoryChunkStorage storage = new InMemoryChunkStorage();
        FileChunker chunker = new FileChunker();
        private final FileChunker fileChunker = new FileChunker();

        public void runTest(File file) throws IOException {
                byte[] fileData = Files.readAllBytes(file.toPath());
                int  compressedChunkSize = getSizeCompressedChunks(file);
                ChunkProcessor processor = new ChunkProcessor(storage, chunker);

                // Compression globale
                long startGlobal = System.nanoTime();
                byte[] compressedGlobal = compressor.compressChunk(fileData);
                long endGlobal = System.nanoTime();
                System.out.println("Compression globale : " + (endGlobal - startGlobal) / 1e6 + " ms, taille : "
                                + compressedGlobal.length + " octets");

                long startChunks = System.nanoTime();
                processor.processFile(file, true);
                long endChunks = System.nanoTime();

                long chunkTimeMs = (endChunks - startChunks) / 1_000_000;

                System.out.println(
                                "Compression par chunk : " + chunkTimeMs + " ms, taille totale : " + compressedChunkSize
                                                + " octets");
        }

        private int getSizeCompressedChunks(File file) throws IOException {
                List<byte[]> chunks = fileChunker.getChunks(file);
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
