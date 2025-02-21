package org.project.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;

public class JsonUtils {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Sauvegarde un objet en JSON dans un fichier.
     */
    public static void saveToJsonFile(Object data, String fileName) {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(fileName), data);
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
        try {
            return objectMapper.readValue(new File(fileName), valueType);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("❌ Erreur lors de la lecture du fichier JSON : " + fileName);
            return null;
        }
    }
}
