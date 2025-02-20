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
                ChunkProcessor processor = new ChunkProcessor(storage, chunker);

                // Compression globale
                long startGlobal = System.nanoTime();
                byte[] compressedGlobal = compressor.compressChunk(fileData);
                long endGlobal = System.nanoTime();
                System.out.println("Compression globale : " + (endGlobal - startGlobal) / 1e6 + " ms, taille : "
                                + compressedGlobal.length + " octets");

                List<byte[]> chunks = fileChunker.getChunks(file);
                int  compressedChunkSize = getSizeCompressedChunks(chunks);

                long startChunks = System.nanoTime();
                processor.compressChunks(chunks);
                long endChunks = System.nanoTime();

                int totalChunkSize = chunks.stream().mapToInt(chunk -> chunk.length).sum();
                long chunkTimeMs = (endChunks - startChunks) / 1_000_000;

                System.out.println(
                                "Compression par chunk : " + chunkTimeMs + " ms, taille totale : " + compressedChunkSize
                                                + " octets");
        }

        private int getSizeCompressedChunks(List<byte[]> chunks) throws IOException {
                int compressedChunkSize = 0;

                for (byte[] chunk : chunks) {
                        byte[] compressedChunk = ZstdCompressor.compressChunk(chunk);
                        compressedChunkSize += compressedChunk.length;
                }

                return compressedChunkSize;
        }
}
