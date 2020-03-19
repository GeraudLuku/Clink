package com.geraud.audiorecorder.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.geraud.audiorecorder.R;
import com.google.android.material.textfield.TextInputEditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

public class SaveNoteDailog extends AppCompatDialogFragment {

    private TextInputEditText mTitleEditTxt, mDescriptionEditTxt;

    private SaveNoteDailogListener mSaveNoteDailogListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_save_dailog, null);

        builder.setView(view)
                .setTitle("Voice Note Created")
                .setCancelable(false) //cant cancel it file must be saved
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        //get tittle and description text
                        if (mTitleEditTxt.getText().toString().matches(""))
                            mTitleEditTxt.setError("Must include title");

                        if (mDescriptionEditTxt.getText().toString().matches(""))
                            mDescriptionEditTxt.setError("Must include title");

                        //set title and description
                        mSaveNoteDailogListener.saveNote(mTitleEditTxt.getText().toString().trim(),mDescriptionEditTxt.getText().toString().trim());
                    }
                });

        mTitleEditTxt = view.findViewById(R.id.title);
        mDescriptionEditTxt = view.findViewById(R.id.note);


        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            mSaveNoteDailogListener = (SaveNoteDailogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement save note dialog listener");
        }
    }

    public interface SaveNoteDailogListener {
        void saveNote(String title, String description);
    }
}
