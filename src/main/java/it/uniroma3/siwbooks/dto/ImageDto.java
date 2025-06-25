package it.uniroma3.siwbooks.dto;

public class ImageDto {
    private Long id;
    private String fileName;
    private String originalFileName;
    private String url;
    private Long fileSize;
    private String contentType;

    public ImageDto() {}

    public ImageDto(Long id, String fileName, String originalFileName,
                    String url, Long fileSize, String contentType) {
        this.id = id;
        this.fileName = fileName;
        this.originalFileName = originalFileName;
        this.url = url;
        this.fileSize = fileSize;
        this.contentType = contentType;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}