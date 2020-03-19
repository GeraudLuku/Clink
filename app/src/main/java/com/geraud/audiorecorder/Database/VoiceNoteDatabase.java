package com.geraud.audiorecorder.Database;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {VoiceNote.class}, version = 1)
public abstract class VoiceNoteDatabase extends RoomDatabase {

    private static VoiceNoteDatabase instance;

    public abstract VoiceNoteDao voiceNoteDao();

    public static synchronized VoiceNoteDatabase getInstance(Context context) {

        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    VoiceNoteDatabase.class,
                    "notes_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
        }

        return instance;
    }

    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            Log.d(getClass().getSimpleName(), "Database created");
        }

        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
            Log.d(getClass().getSimpleName(), "Database opened");
        }
    };

}
