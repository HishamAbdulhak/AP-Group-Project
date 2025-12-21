package groupproject.apgroupproject.services;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FileAuthentication implements AuthenticationService {

    private final File studentFile;
    private final File adminFile;

    public FileAuthentication(String studentFilePath, String adminFilePath) {
        this.studentFile = new File(studentFilePath);
        this.adminFile = new File(adminFilePath);
        createFileIfNotExists(studentFile);
        createFileIfNotExists(adminFile);
    }

    private void createFileIfNotExists(File file) {
        try {
            if (!file.exists()) {
                file.createNewFile();
                // If it is the admin file, add a default admin for safety
                if (file.getName().equals("admins.txt")) {
                    try (FileWriter fw = new FileWriter(file); PrintWriter pw = new PrintWriter(fw)) {
                        pw.println("admin,admin123");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 1. Login Student
    @Override
    public boolean loginStudent(String id, String password) {
        try (Scanner scanner = new Scanner(studentFile)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                // Format: ID, Name, Email, Password, SecurityCode
                if (parts.length >= 4 && parts[0].trim().equals(id) && parts[3].trim().equals(password)) {
                    return true;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 2. Login Admin
    @Override
    public boolean loginAdmin(String username, String password) {
        try (Scanner scanner = new Scanner(adminFile)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                // Format: Username, Password
                if (parts.length >= 2 && parts[0].trim().equals(username) && parts[1].trim().equals(password)) {
                    return true;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 3. Register New Student
    @Override
    public boolean register(String id, String name, String email, String password, String securityCode) {
        if (isStudentIdTaken(id)) {
            return false;
        }

        try (FileWriter fw = new FileWriter(studentFile, true);
             BufferedWriter bw = new BufferedWriter(fw)) {

            // Check if file has content. If so, start on a new line first.
            // This prevents the new user from being attached to the end of the previous line.
            if (studentFile.length() > 0) {
                bw.newLine();
            }

            // Write the data (ID, Name, Email, Password, SecurityCode)
            bw.write(id + "," + name + "," + email + "," + password + "," + securityCode);

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean isStudentIdTaken(String id) {
        try (Scanner scanner = new Scanner(studentFile)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
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

    // 4. Reset Password
    @Override
    public boolean resetPassword(String id, String securityCode, String newPassword) {
        List<String> lines = new ArrayList<>();
        boolean found = false;

        try (Scanner scanner = new Scanner(studentFile)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");

                // Format: ID(0), Name(1), Email(2), Pass(3), Code(4)
                if (parts.length >= 5 && parts[0].trim().equals(id) && parts[4].trim().equals(securityCode)) {
                    // Update Password. Keep others same.
                    line = parts[0] + "," + parts[1] + "," + parts[2] + "," + newPassword + "," + parts[4];
                    found = true;
                }
                lines.add(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }

        if (!found) return false;

        return writeLinesToFile(lines);
    }

    // 5. Get Student Name
    @Override
    public String getStudentName(String id) {
        try (Scanner scanner = new Scanner(studentFile)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                if (parts.length >= 2 && parts[0].trim().equals(id)) {
                    return parts[1];
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return "Student";
    }

    // 6. Get Student Email
    @Override
    public String getStudentEmail(String id) {
        try (Scanner scanner = new Scanner(studentFile)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                if (parts.length >= 3 && parts[0].trim().equals(id)) {
                    return parts[2];
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return "student@uni.edu";
    }

    // 7. Update Student Profile
    @Override
    public boolean updateStudentProfile(String id, String newName, String newEmail, String newPassword) {
        List<String> lines = new ArrayList<>();
        boolean found = false;

        try (Scanner scanner = new Scanner(studentFile)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");

                // Format: ID(0), Name(1), Email(2), Pass(3), Code(4)
                if (parts.length >= 5 && parts[0].trim().equals(id)) {
                    // Update Name, Email, Password. Keep ID and Security Code same.
                    String securityCode = parts[4];
                    line = id + "," + newName + "," + newEmail + "," + newPassword + "," + securityCode;
                    found = true;
                }
                lines.add(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }

        if (!found) return false;

        return writeLinesToFile(lines);
    }

    // Helper method to write list of lines back to file
    private boolean writeLinesToFile(List<String> lines) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(studentFile))) {
            for (int i = 0; i < lines.size(); i++) {
                pw.print(lines.get(i));
                if (i < lines.size() - 1) {
                    pw.println(); // Add newline only between items, not at the very end
                }
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}