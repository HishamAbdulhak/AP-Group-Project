package groupproject.apgroupproject.models;

import java.time.LocalDate;

public class DocumentsMetadata {
    private String fileName;
    private LocalDate uploadDate;
    private String status;

    public DocumentsMetadata(String fileName, LocalDate uploadDate, String status) {
        this.fileName = fileName;
        this.uploadDate = uploadDate;
        this.status = status;
    }

    // Getters
    public String getFileName() {
        return fileName;
    }

    public LocalDate getUploadDate() {
        return uploadDate;
    }

    public String getStatus() {
        return status;
    }

    // Setter
    public void setStatus(String status) {
        this.status = status;
    }
}