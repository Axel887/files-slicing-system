package org.project;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class App {
  public static void main(String[] args)
  {
    Scanner scanner = new Scanner(System.in);

    // Demande à l'utilisateur d'entrer le chemin du fichier
    System.out.print("Entrez le chemin du fichier : ");
    String filePath = scanner.nextLine();

    // Vérification de l'existence du fichier
    Path path = Paths.get(filePath);
    if (Files.exists(path)) {
      System.out.println("Fichier existant : " + filePath);
    } else {
      System.out.println("Fichier non trouvé : " + filePath);
    }

    scanner.close();
  }
}
