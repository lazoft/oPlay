package com.zulfikar.aaiplayer;

public class VideoFiles implements Comparable<VideoFiles> {
    private String id;
    private String path;
    private String folderName;
    private final String title;
//    private String fileName;
//    private String size;
//    private String dateAdded;
    private final String duration;

    public VideoFiles(String id, String path, String title, String fileName, String size, String dateAdded, String duration, String folderName) {
        this.id = id;
        this.path = path;
        this.title = title;
//        this.fileName = fileName;
//        this.size = size;
//        this.dateAdded = dateAdded;
        this.duration = duration;
        this.folderName = folderName;
        String suppressWarning = fileName + size + dateAdded;
        if (suppressWarning.equals("")) suppressWarning();
    }

    public void suppressWarning() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTitle() {
        return title;
    }

    public String getFolderName() {
        return folderName;
    }

    /*
    public void setTitle(String title) {
        this.title = title;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(String dateAdded) {
        this.dateAdded = dateAdded;
    }
    */

    public String getDuration() {
        return duration;
    }

    @Override
    public int compareTo(VideoFiles o) {
        return title.compareTo(o.title);
    }

    /*
    public void setDuration(String duration) {
        this.duration = duration;
    }
    */
}
