package com.geraud.audiorecorder.Repository;

import android.app.Application;
import android.os.AsyncTask;

import com.geraud.audiorecorder.Database.VoiceNote;
import com.geraud.audiorecorder.Database.VoiceNoteDao;
import com.geraud.audiorecorder.Database.VoiceNoteDatabase;

import java.util.List;

import androidx.lifecycle.LiveData;

public class VoiceNoteRepository {

    private VoiceNoteDao voiceNoteDao;
    private LiveData<List<VoiceNote>> allVoiceNotes;

    public VoiceNoteRepository(Application application) {
        VoiceNoteDatabase database = VoiceNoteDatabase.getInstance(application);
        voiceNoteDao = database.voiceNoteDao();
        allVoiceNotes = voiceNoteDao.getAllNotes();
    }

    public void insert(VoiceNote voiceNote) {
        new insertVoiceNoteAsyncTask(voiceNoteDao).execute(voiceNote);
    }

    public void delete(VoiceNote voiceNote) {
        new deleteVoiceNoteAsyncTask(voiceNoteDao).execute(voiceNote);
    }

    public void deleteAllNotes() {
        new deleteAllVoiceNoteAsyncTask(voiceNoteDao).execute();
    }

    public LiveData<List<VoiceNote>> getAllVoiceNotes() {
        return allVoiceNotes;
    }


    //create Async task to run the database queries because they dont run on the main thread
    private static class insertVoiceNoteAsyncTask extends AsyncTask<VoiceNote, Void, Void> {
        private VoiceNoteDao voiceNoteDao;

        private insertVoiceNoteAsyncTask(VoiceNoteDao voiceNoteDao) {
            this.voiceNoteDao = voiceNoteDao;
        }

        @Override
        protected Void doInBackground(VoiceNote... voiceNotes) {
            voiceNoteDao.insert(voiceNotes[0]);
            return null;
        }
    }

    private static class deleteVoiceNoteAsyncTask extends AsyncTask<VoiceNote, Void, Void> {
        private VoiceNoteDao voiceNoteDao;

        private deleteVoiceNoteAsyncTask(VoiceNoteDao voiceNoteDao) {
            this.voiceNoteDao = voiceNoteDao;
        }

        @Override
        protected Void doInBackground(VoiceNote... voiceNotes) {
            voiceNoteDao.delete(voiceNotes[0]);
            return null;
        }
    }

    private static class deleteAllVoiceNoteAsyncTask extends AsyncTask<Void, Void, Void> {
        private VoiceNoteDao voiceNoteDao;

        private deleteAllVoiceNoteAsyncTask(VoiceNoteDao voiceNoteDao) {
            this.voiceNoteDao = voiceNoteDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            voiceNoteDao.deleteAllNotes();
            return null;
        }
    }
}
