package com.geraud.audiorecorder.Features;


import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import es.dmoral.toasty.Toasty;

import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.geraud.audiorecorder.Database.VoiceNote;
import com.geraud.audiorecorder.R;
import com.geraud.audiorecorder.Dialogs.SaveNoteDailog;
import com.geraud.audiorecorder.Repository.VoiceNoteViewModel;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class RecordFragment extends Fragment implements View.OnClickListener {

    private NavController navController;

    private ImageButton listBtn;
    private ImageButton recordBtn;
    private TextView filenameText;

    private boolean isRecording = false;

    private String recordPermission = Manifest.permission.RECORD_AUDIO;
    private int PERMISSION_CODE = 21;

    private MediaRecorder mediaRecorder;
    private String recordFile;

    private Chronometer timer;

    private VoiceNoteViewModel mVoiceNoteViewModel;

    public RecordFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_record, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //attach to view model
        mVoiceNoteViewModel = new ViewModelProvider(getActivity()).get(VoiceNoteViewModel.class);

        //Intitialize Variables
        navController = Navigation.findNavController(view);
        listBtn = view.findViewById(R.id.record_list_btn);
        recordBtn = view.findViewById(R.id.record_btn);
        timer = view.findViewById(R.id.record_timer);
        filenameText = view.findViewById(R.id.record_filename);


        /* Setting up on click listener
           - Class must implement 'View.OnClickListener' and override 'onClick' method
         */
        listBtn.setOnClickListener(this);
        recordBtn.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        /*  Check, which button is pressed and do the task accordingly
         */
        switch (v.getId()) {
            case R.id.record_list_btn:
                /*
                Navigation Controller
                Part of Android Jetpack, used for navigation between both fragments
                 */
                if (isRecording) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                    alertDialog.setPositiveButton("OKAY", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            navController.navigate(R.id.action_recordFragment_to_audioListFragment);
                            isRecording = false;
                        }
                    });
                    alertDialog.setNegativeButton("CANCEL", null);
                    alertDialog.setTitle("Audio Still recording");
                    alertDialog.setMessage("Are you sure, you want to stop the recording?");
                    alertDialog.create().show();
                } else {
                    navController.navigate(R.id.action_recordFragment_to_audioListFragment);
                }
                break;

            case R.id.record_btn:
                if (isRecording) {
                    // Change button image and set Recording state to false
                    recordBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_mic_stopped, null));
                    //Stop Recording
                    stopRecording();
                    isRecording = false;
                } else {
                    //Check permission to record audio
                    if (checkPermissions()) {
                        //Start Recording
                        startRecording();

                        // Change button image and set Recording state to false
                        recordBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_mic_started, null));
                        isRecording = true;
                    }
                }
                break;
        }
    }

    private void stopRecording() {
        //Stop Timer, very obvious and also clear it
        timer.stop();
        timer.setBase(SystemClock.elapsedRealtime());

        //Change text on page to file saved
        filenameText.setText("Recording Stopped, File Saved : " + recordFile);

        //Stop media recorder and set it to null for further use to record new audio
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;

        //show alert dialog
//        SaveNoteDailog saveNoteDailog = new SaveNoteDailog();
//        saveNoteDailog.show(getChildFragmentManager(), "SAVE_NOTE_DIALOG");


        //custom alert dailog
        // create an alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Voice Note Created")
                .setCancelable(false);

        // set the custom layout
        View customLayout = getLayoutInflater().inflate(R.layout.layout_save_dailog, null);
        builder.setView(customLayout);

        //get the views
        final EditText title = customLayout.findViewById(R.id.title);
        final EditText description = customLayout.findViewById(R.id.note);

        // add a button
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // store note if fields are not empty
                //get tittle and description text
                if (title.getText().toString().matches("") || description.getText().toString().matches("")) {
                    title.setError("Fill all fields");
                } else {
                    //save note
                    //write note file to the database .......  ND path = recordPath + recordFile

                    String tit = title.getText().toString();
                    String desc = description.getText().toString();

                    String path = recordPath + "/" + recordFile;
                    mVoiceNoteViewModel.insert(new VoiceNote(tit, desc, path));

                    //display success toast
                    Toasty.custom(getContext(), "Successfully Created Voice Note : " + tit + "!", R.drawable.ic_done, R.color.colorPrimaryDark, Toast.LENGTH_SHORT, true,
                            true).show();
                }

            }
        });

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private String recordPath;

    private void startRecording() {
        //Start timer from 0
        timer.setBase(SystemClock.elapsedRealtime());
        timer.start();

        //Get app external directory path
        recordPath = getActivity().getExternalFilesDir("/mnt/sdcard/").getAbsolutePath();

        //Get current date and time
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss", Locale.CANADA);
        Date now = new Date();

        //initialize filename variable with date and time at the end to ensure the new file wont overwrite previous file
        recordFile = "Recording_" + formatter.format(now) + ".3gp";

        filenameText.setText("Recording, File Name : " + recordFile);

        //Setup Media Recorder for recording
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(recordPath + "/" + recordFile);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Start Recording
        mediaRecorder.start();
    }

    private boolean checkPermissions() {
        //Check permission
        if (ActivityCompat.checkSelfPermission(getContext(), recordPermission) == PackageManager.PERMISSION_GRANTED) {
            //Permission Granted
            return true;
        } else {
            //Permission not granted, ask for permission
            ActivityCompat.requestPermissions(getActivity(), new String[]{recordPermission}, PERMISSION_CODE);
            return false;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (isRecording) {
            stopRecording();
        }
    }

//    @Override
//    public void saveNote(String title, String description) {
//        //write note file to the database .......  ND path = recordPath + recordFile
//        String path = recordPath + "/" + recordFile;
//        mVoiceNoteViewModel.insert(new VoiceNote(title, description, path));
//    }
}
