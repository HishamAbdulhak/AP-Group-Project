package groupproject.apgroupproject.models;

public class UserSession {

    private static UserSession instance;

    // User Data
    private String id;
    private String name;
    private String email;
    private String password;
    private boolean isAdmin;

    // Navigation Data (Temporary Storage)
    private String pendingQuestion;
    private String documentCategory; // <--- NEW FIELD

    private UserSession(String id, String name, String email, String password, boolean isAdmin) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.isAdmin = isAdmin;
    }

    public static void startSession(String id, String name, String email, String password, boolean isAdmin) {
        instance = new UserSession(id, name, email, password, isAdmin);
    }

    public static UserSession getInstance() {
        if (instance == null) {
            System.out.println("Warning: UserSession is null");
        }
        return instance;
    }

    public static void cleanSession() {
        instance = null;
    }

    // --- Getters ---
    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getEmail() {
        return email;
    }
    public String getPassword() {
        return password;
    }
    public boolean isAdmin() {
        return isAdmin;
    }

    // --- Pending Question Logic (Dashboard -> Chat) ---
    public void setPendingQuestion(String question) {
        this.pendingQuestion = question;
    }

    public String getPendingQuestionAndClear() {
        String temp = this.pendingQuestion;
        this.pendingQuestion = null;
        return temp;
    }

    public void setDocumentCategory(String category) {
        this.documentCategory = category;
    }

    public String getDocumentCategory() {
        return documentCategory;
    }

    public String getDocumentCategoryAndClear() {
        String temp = this.documentCategory;
        this.documentCategory = null;
        return temp;
    }
}