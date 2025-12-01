package groupproject.apgroupproject.models;

public class UserSession {
    //Class used to differentiate between different levels of users, students and admins.

    //Static variables hold data globally as long as the app is running
    private static String currentId;
    private static String currentName;
    private static String currentEmail;
    private static String currentPassword;

    //Tracks if the logged-in user is an Admin
    private static boolean isAdmin = false;

    //Start a Session (Called on Login)
    public static void startSession(String id, String name, String email, String pass, boolean adminParams) {
        currentId = id;
        currentName = name;
        currentEmail = email;
        currentPassword = pass;
        isAdmin = adminParams; // true = Admin, false = Student
    }

    //End a Session (Called on Logout)
    public static void cleanSession() {
        currentId = null;
        currentName = null;
        currentEmail = null;
        currentPassword = null;
        isAdmin = false; // Reset to default
    }

    //Getters
    public static String getId() {
        return currentId;
    }
    public static String getName() {
        return currentName;
    }
    public static String getEmail() {
        return currentEmail;
    }
    public static String getPassword() {
        return currentPassword;
    }
    public static boolean isAdmin() {
        return isAdmin;
    }
}