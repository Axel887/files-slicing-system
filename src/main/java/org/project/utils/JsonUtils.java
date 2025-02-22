package org.project.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class JsonUtils {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Sauvegarde un objet en JSON dans un fichier.
     */
    public static void saveToJsonFile(Object data, String fileName) {
        try {
            File file = new File(fileName);

            // ✅ Vérifier et créer le répertoire parent seulement si nécessaire
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, data);
            System.out.println("✅ Données enregistrées dans " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("❌ Erreur lors de l'enregistrement du fichier JSON : " + fileName);
        }
    }

    /**
     * Lit un fichier JSON et le convertit en un objet de type donné.
     */
    public static <T> T readFromJsonFile(String fileName, Class<T> valueType) {
        File file = new File(fileName);

        // ✅ Vérifier si le fichier existe, sinon le créer avec un objet vide
        if (!file.exists()) {
            System.out.println("⚠️  " + fileName + " n'existe pas, création d'un fichier vide...");
            saveToJsonFile(getDefaultInstance(valueType), fileName);
        }

        try {
            return objectMapper.readValue(file, valueType);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("❌ Erreur lors de la lecture du fichier JSON : " + fileName);
            return getDefaultInstance(valueType);
        }
    }

    /**
     * Retourne une instance vide de l'objet JSON par défaut en fonction du type.
     */
    private static <T> T getDefaultInstance(Class<T> valueType) {
        if (valueType == Map.class) {
            return (T) new java.util.LinkedHashMap<>();
        } else if (valueType == List.class) {
            return (T) new java.util.ArrayList<>();
        }
        return null;
    }
}
