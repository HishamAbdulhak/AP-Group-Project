package groupproject.apgroupproject.services;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FileAuthentication implements AuthenticationService {

    private final String studentFile;
    private final String adminFile;

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
    public boolean register(String id, String name, String email, String password, String passcode) {
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
            writer.write(id + "," + name + "," + email + "," + password + "," + passcode);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    @Override
    public boolean resetPassword(String id, String recoveryPasscode, String newPassword) {
        File file = new File(studentFile);
        if (!file.exists()) return false;

        List<String> lines = new ArrayList<>();
        boolean userFound = false;

        try (Scanner scanner = new Scanner(file)) {
            // 1. Read all lines into memory
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");

                // Check if this is the user AND the passcode matches
                // Assuming format: ID,Name,Email,Password,Passcode
                if (parts.length >= 5 && parts[0].trim().equals(id) && parts[4].trim().equals(recoveryPasscode)) {

                    // 2. Modify the password (index 3)
                    parts[3] = newPassword;

                    // Rebuild the line
                    String newLine = String.join(",", parts);
                    lines.add(newLine);
                    userFound = true;
                } else {
                    // Keep original line
                    lines.add(line);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }

        // 3. If user verified, overwrite the file
        if (userFound) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(studentFile))) {
                for (String line : lines) {
                    writer.write(line);
                    writer.newLine();
                }
                return true; // Success!
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return false; // User not found or passcode wrong
    }

    //Checks if ID/Username and Password match in a file
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

    //Checks if just the ID exists (for registration)
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