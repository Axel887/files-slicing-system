package org.project;

import org.rabinfingerprint.fingerprint.RabinFingerprintLong;
import org.rabinfingerprint.polynomial.Polynomial;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class FileFingerprintTest {

    public static void main(String[] args) {
        // Spécifiez le chemin du fichier en dur
        String filePath = "C:/Users/houen/OneDrive/Bureau/Work/HETIC/java/files-slicing-system/test.pdf";
        File file = new File(filePath);
        Polynomial polynomial = Polynomial.createIrreducible(53); // Use a 53-bit polynomial
        RabinFingerprintLong fingerprinter = new RabinFingerprintLong(polynomial);

        try (FileInputStream inputStream = new FileInputStream(file)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                fingerprinter.pushBytes(buffer, 0, bytesRead);
            }
            System.out.println("The fingerprint of the file is: " + fingerprinter.getFingerprint());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}