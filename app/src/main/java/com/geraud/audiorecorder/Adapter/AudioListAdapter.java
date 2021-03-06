package com.geraud.audiorecorder.Adapter;

import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.geraud.audiorecorder.Database.VoiceNote;
import com.geraud.audiorecorder.R;
import com.geraud.audiorecorder.Util.TimeAgo;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AudioListAdapter extends RecyclerView.Adapter<AudioListAdapter.AudioViewHolder> implements Filterable {

    private List<VoiceNote> voiceNoteList = new ArrayList<>();
    private List<VoiceNote> voiceNoteListFiltered = new ArrayList<>();
    private TimeAgo timeAgo;

    private onItemListClick onItemListClick;

    public AudioListAdapter(onItemListClick onItemListClick) {
        this.onItemListClick = onItemListClick;
    }

    @NonNull
    @Override
    public AudioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_list_item, parent, false);
        timeAgo = new TimeAgo();
        return new AudioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AudioViewHolder holder, int position) {

        VoiceNote voiceNote = voiceNoteListFiltered.get(position);
        File audioFile = null;

        //convert String to File
        try {
            Uri myUri = Uri.parse(voiceNote.getPath());
            audioFile = new File(myUri.getPath());
        } catch (Exception e) {
            Log.d("AUDIO-ADAPTER", "couldnt convert string to file");
        }


        holder.list_title.setText(voiceNote.getTitle());
        holder.list_date.setText(timeAgo.getTimeAgo(audioFile.lastModified()));

        holder.list_desc.setText(voiceNote.getDescription());
    }

    @Override
    public int getItemCount() {
        return voiceNoteListFiltered.size();
    }

    public void setNotes(List<VoiceNote> notes) {
        this.voiceNoteList = notes;
        this.voiceNoteListFiltered = notes;
        notifyDataSetChanged();
    }

    public class AudioViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView delete_note;
        private TextView list_title;
        private TextView list_date;
        private TextView list_desc;

        public AudioViewHolder(@NonNull View itemView) {
            super(itemView);

            delete_note = itemView.findViewById(R.id.delete_note);
            list_title = itemView.findViewById(R.id.list_title);
            list_date = itemView.findViewById(R.id.list_date);
            list_desc = itemView.findViewById(R.id.list_desc);

            itemView.setOnClickListener(this);

            delete_note.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //delete note
                    onItemListClick.deleteNote(voiceNoteListFiltered.get(getAdapterPosition()), getAdapterPosition());
                }
            });

        }

        @Override
        public void onClick(View v) {
            onItemListClick.onClickListener(voiceNoteListFiltered.get(getAdapterPosition()), getAdapterPosition());
        }
    }

    public VoiceNote getNoteAt(int position) {
        return voiceNoteListFiltered.get(position);
    }

    public void removeItem(int position) {
        voiceNoteListFiltered.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, voiceNoteListFiltered.size());
    }

    public interface onItemListClick {
        void onClickListener(VoiceNote voiceNote, int position);
        void deleteNote(VoiceNote voiceNote, int position);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    voiceNoteListFiltered = voiceNoteList;
                } else {
                    List<VoiceNote> filteredList = new ArrayList<>();
                    for (VoiceNote row : voiceNoteList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getTitle().toLowerCase().contains(charString.toLowerCase()) || row.getDescription().contains(charSequence)) {
                            filteredList.add(row);
                        }
                    }

                    voiceNoteListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = voiceNoteListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                voiceNoteListFiltered = (ArrayList<VoiceNote>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

}
