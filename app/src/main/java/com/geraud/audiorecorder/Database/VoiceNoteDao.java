package com.geraud.audiorecorder.Database;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Delete;
import androidx.room.Query;

@Dao
public interface VoiceNoteDao {

    @Insert
    void insert(VoiceNote voiceNote);

    @Delete
    void delete(VoiceNote voiceNote);

    @Query("DELETE  FROM notes_table")
    void deleteAllNotes();

    @Query("SELECT * FROM notes_table ORDER BY title DESC")
    LiveData<List<VoiceNote>> getAllNotes();
}
