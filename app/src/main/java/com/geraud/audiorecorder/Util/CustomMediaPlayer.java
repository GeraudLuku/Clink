package com.geraud.audiorecorder.Util;

import android.media.MediaPlayer;

import java.io.IOException;

public class CustomMediaPlayer extends MediaPlayer {
    private String dataSource;

    @Override
    public void setDataSource(String path) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {
        // TODO Auto-generated method stub
        super.setDataSource(path);
        dataSource = path;
    }

    public String getDataSource() {
        return dataSource;
    }
}