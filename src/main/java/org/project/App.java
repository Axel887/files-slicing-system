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
        Scanner scanner = new Scanner(System.in);

        System.out.println("=========================================");
        System.out.println("       ✂️ SYSTEME DE CHUNKING ✂️ ");
        System.out.println("=========================================\n");

        while (!closeProgram) {
            System.out.print("\n🖍️ Entrez le chemin du fichier à découper : ");
            String filePath = scanner.nextLine();

            File file = new File(filePath);
            if (!file.exists()) {
                System.out.println("❌ Erreur : Le fichier spécifié n'existe pas.\n");
                continue;
            }

            System.out.println("\n📂 Fichier détecté : " + file.getName());
            System.out.println("🏁 Début du découpage...\n");

            try {
                processor.processFile(file);
                System.out.println("\n✅ Découpage terminé avec succès !");
            } catch (IOException e) {
                System.err.println("\n❌ Erreur lors du découpage du fichier : " + e.getMessage());
            }

            System.out.print("\n🔁 Voulez-vous traiter un autre fichier ? (1 = Oui, autre = Non) : ");
            String choiceValue = scanner.nextLine();

            if (!choiceValue.equals("1")) {
                closeProgram = true;
                System.out.println("\n🙋🏾 Chunk... chunk... programme terminé!");
            }
        }

        scanner.close();
    }
}


