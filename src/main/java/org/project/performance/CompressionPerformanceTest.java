package org.project.performance;

import org.project.performance.utils.PerformanceUtils;
import org.project.service.ChunkProcessor;

import java.io.IOException;
import java.io.File;

public class CompressionPerformanceTest {
        private final ChunkProcessor processor;

        public CompressionPerformanceTest(ChunkProcessor processor) {
                this.processor = processor;
        }

        public void runPerformanceTestFile(File file) throws IOException {
                PerformanceUtils performanceUtils = new PerformanceUtils(this.processor, file);
                performanceUtils.compareCompressionPerformance();
        }
}
