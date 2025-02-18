package org.project;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Entrez le chemin du fichier à découper : ");
        String filePath = scanner.nextLine();
        scanner.close();

        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("Erreur : Le fichier spécifié n'existe pas.");
            return;
        }

        try {
            new FileChunker().chunkFile(file);
        } catch (IOException e) {
            System.err.println("Erreur lors du découpage du fichier : " + e.getMessage());
        }
    }
}
