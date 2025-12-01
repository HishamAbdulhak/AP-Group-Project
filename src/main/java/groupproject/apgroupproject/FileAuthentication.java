package groupproject.apgroupproject;

import java.io.*;
import java.util.Scanner;

public class FileAuthentication implements AuthenticationService {

    private final String studentFile;
    private final String adminFile;

    // ✅ FIXED CONSTRUCTOR: Initializes both variables
    public FileAuthentication(String studentFile, String adminFile) {
        this.studentFile = studentFile;
        this.adminFile = adminFile;
    }

    @Override
    public boolean loginStudent(String id, String password) {
        // Index 0 = ID, Index 3 = Password
        return checkCredentials(studentFile, id, password, 0, 3);
    }

    @Override
    public boolean loginAdmin(String username, String password) {
        // Index 0 = Username, Index 1 = Password (for admins.txt)
        return checkCredentials(adminFile, username, password, 0, 1);
    }

    @Override
    public boolean register(String id, String name, String email, String password) {
        // 1. Check if ID exists in students.txt
        if (checkCredentials(studentFile, id, password, 0, 3) || isIdTaken(id)) {
            return false; // Student already registered
        }

        // 2. Append new user to students.txt
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(studentFile, true))) {
            // Check if file is empty to handle new line correctly (optional cosmetic fix)
            File file = new File(studentFile);
            if (file.length() > 0) {
                writer.newLine();
            }
            writer.write(id + "," + name + "," + email + "," + password);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Helper: Checks if ID/Username and Password match in a file
    private boolean checkCredentials(String filePath, String targetId, String targetPass, int idIndex, int passIndex) {
        File file = new File(filePath);
        if (!file.exists()) return false;

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                // Skip empty lines
                if (line.trim().isEmpty()) continue;

                String[] parts = line.split(",");

                // Ensure line has enough parts to avoid IndexOutOfBoundsException
                if (parts.length > Math.max(idIndex, passIndex)) {
                    String fileId = parts[idIndex].trim();
                    String filePass = parts[passIndex].trim();

                    if (fileId.equals(targetId) && filePass.equals(targetPass)) {
                        return true;
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Helper: Checks if just the ID exists (for registration)
    private boolean isIdTaken(String id) {
        File file = new File(studentFile);
        if (!file.exists()) return false;

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.trim().isEmpty()) continue;

                String[] parts = line.split(",");
                if (parts.length > 0 && parts[0].trim().equals(id)) {
                    return true;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }
}