package org.project.controller;

import org.project.ChunkProcessor;
import org.project.CompressionPerformanceTest;
import org.project.FileChunker;
import org.project.InMemoryChunkStorage;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class ProgramController {
    private final Scanner scanner;
    private final InMemoryChunkStorage storage;
    private final FileChunker chunker;
    private final ChunkProcessor processor;
    private final CompressionPerformanceTest performanceTest;

    public ProgramController() {
        this.scanner = new Scanner(System.in);
        this.storage = new InMemoryChunkStorage();
        this.chunker = new FileChunker();
        this.processor = new ChunkProcessor(storage, chunker);
        this.performanceTest = new CompressionPerformanceTest();
    }

    public void start() {
        boolean closeProgram = false;

        System.out.println("=========================================");
        System.out.println("       ✂️ SYSTEME DE CHUNKING ✂️ ");
        System.out.println("=========================================\n");

        while (!closeProgram) {
            processFile();
            closeProgram = askToStop();
        }

        scanner.close();
    }

    private void processFile() {
        System.out.print("\n🖍️ Entrez le chemin du fichier à découper : ");
        String filePath = scanner.nextLine();

        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("❌ Erreur : Le fichier spécifié n'existe pas.\n");
            return;
        }

        System.out.println("\n📂 Fichier détecté : " + file.getName());
        System.out.println("🏁 Début du découpage...\n");

        try {
            processor.processFile(file);

            System.out.print("Voulez-vous effectuer un test de performance ? (oui/non) 🖍️: ");
            String performTest = scanner.nextLine();
            if (performTest.equalsIgnoreCase("oui")) {
                try {
                    performanceTest.runPerformTestFile(file);
                } catch (IOException e) {
                    System.err.println("Erreur lors du test de performance : " + e.getMessage());
                }
            }

            System.out.println("\n✅ Découpage terminé avec succès !");
        } catch (IOException e) {
            System.err.println("\n❌ Erreur lors du découpage du fichier : " + e.getMessage());
        }
    }

    private boolean askToStop() {
        while (true) {
            System.out.print("\n🔁 Voulez-vous traiter un autre fichier ? (oui/non) : ");
            String choiceValue = scanner.nextLine().trim().toLowerCase();

            if (choiceValue.equals("oui")) {
                return false;
            } else if (choiceValue.equals("non")) {
                System.out.println("Programme terminé !");
                return true;
            } else {
                System.out.println("❌ Entrée invalide. Veuillez répondre par 'oui' ou 'non'.");
            }
        }
    }
}
