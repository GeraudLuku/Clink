package com.geraud.audiorecorder.Database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "notes_table")
public class VoiceNote {

    @PrimaryKey(autoGenerate = true)
    private String id;

    private String title;
    private String description;
    private String path;

    //empty constructor
    public VoiceNote(){

    }

    public VoiceNote(String title, String description, String path) {
        this.title = title;
        this.description = description;
        this.path = path;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
