package com.ishtonk.note_keeper;

import androidx.cardview.widget.CardView;

import com.ishtonk.note_keeper.Models.Notes;

public interface NotesClickListener {
    void onClick(Notes notes);
    void onLongClick(Notes notes, CardView cardView);
}
