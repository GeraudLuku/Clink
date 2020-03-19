package com.geraud.audiorecorder.Repository;

import android.app.Application;

import com.geraud.audiorecorder.Database.VoiceNote;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class VoiceNoteViewModel extends AndroidViewModel {

    private VoiceNoteRepository noteRepository;
    private LiveData<List<VoiceNote>> allVoiceNotes;

    public VoiceNoteViewModel(@NonNull Application application) {
        super(application);
        noteRepository = new VoiceNoteRepository(application);
        allVoiceNotes = noteRepository.getAllVoiceNotes();
    }

    public void insert(VoiceNote voiceNote) {
        noteRepository.insert(voiceNote);
    }

    public void delete(VoiceNote voiceNote) {
        noteRepository.delete(voiceNote);
    }

    public void deleteAllNotes() {
        noteRepository.deleteAllNotes();
    }

    public LiveData<List<VoiceNote>> getAllVoiceNotes() {
        return allVoiceNotes;
    }
}
