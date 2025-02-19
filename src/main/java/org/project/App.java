package org.project;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        boolean closeProgram = false;
        InMemoryChunkStorage storage = new InMemoryChunkStorage();
        FileChunker chunker = new FileChunker();
        ChunkProcessor processor = new ChunkProcessor(storage, chunker);
        CompressionPerformanceTest performanceTest = new CompressionPerformanceTest();
        Scanner scanner;
        String filePath;

        do {
            scanner = new Scanner(System.in);
            System.out.print("Entrez le chemin du fichier à découper : ");
            filePath = scanner.nextLine();

            File file = new File(filePath);
            if (!file.exists()) {
                System.out.println("Erreur : Le fichier spécifié n'existe pas.");
                return;
            }

            try {
                processor.processFile(file);

                System.out.print("Voulez-vous effectuer un test de performance ? (oui/non) : ");
                String performTest = scanner.nextLine();
                if (performTest.equalsIgnoreCase("oui")) {
                    try {
                        performanceTest.runTest(file);
                    } catch (IOException e) {
                        System.err.println("Erreur lors du test de performance : " + e.getMessage());
                    }
                    return;
                }

            } catch (IOException e) {
                System.err.println("Erreur lors du découpage du fichier : " + e.getMessage());
            }

            System.out.print("Continuer (1) ou stopper (tous caracteres) : ");
            String choiceValue = scanner.nextLine();

            if (!choiceValue.equals("1")) {
                closeProgram = true;
            }
        } while (!closeProgram);

        scanner.close();
    }
}
