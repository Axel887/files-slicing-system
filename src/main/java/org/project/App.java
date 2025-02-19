package org.project;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        boolean closeProgram = false;
        FileChunker fileChunker = new FileChunker();
        Scanner scanner;

        do {
            scanner = new Scanner(System.in);
            System.out.print("Entrez le chemin du fichier à découper : ");
            String filePath = scanner.nextLine();

            File file = new File(filePath);
            if (!file.exists()) {
                System.out.println("Erreur : Le fichier spécifié n'existe pas.");
                return;
            }

            try {
                fileChunker.chunkFile(file);
            } catch (IOException e) {
                System.err.println("Erreur lors du découpage du fichier : " + e.getMessage());
            }

            scanner = new Scanner(System.in);
            System.out.print("Continuer (1) ou stopper (tous caracteres) : ");
            String choiceValue = scanner.nextLine();

            if (!choiceValue.equals("1")) {
                closeProgram = true;
            }
        } while (!closeProgram);

    scanner.close();
  }
}
