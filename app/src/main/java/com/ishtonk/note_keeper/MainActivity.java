package com.ishtonk.note_keeper;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ishtonk.note_keeper.Adapters.NotesListAdapter;
import com.ishtonk.note_keeper.Models.Notes;
import com.ishtonk.note_keeper.database.RoomDB;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener{
    RecyclerView recyclerView;
    NotesListAdapter notesListAdapter;
    List<Notes> notesList = new ArrayList<>();
    RoomDB database;
    FloatingActionButton fab_add;
    SearchView home_search_view;
    Notes selected_note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.home_recycler_view);
        fab_add = findViewById(R.id.fab_add_note);
        home_search_view = findViewById(R.id.home_search_view);

        database = RoomDB.getInstance(this); // get the database instance
        notesList = database.mainDAO().getAllNotes(); // get all the notes from the database

        updateRecycler(notesList); // update the recycler view

        fab_add.setOnClickListener(v -> {
            // when the floating action button is clicked
            Intent intent = new Intent(MainActivity.this, TakeNoteActivity.class);
            startActivityForResult(intent, 101); // start the take note activity
        });

        home_search_view.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // when the search view is changed
                filterNotes(newText);
                return true;
            }
        });
    }

    private void filterNotes(String newText) {
        List<Notes> filteredNotes = new ArrayList<>();
        for (Notes note : notesList) {
            if (note.getTitle().toLowerCase().contains(newText.toLowerCase()) // if the title contains the search text
                    || note.getNotes().toLowerCase().contains(newText.toLowerCase())) { // if the notes contains the search text
                filteredNotes.add(note); // add the note to the filtered notes list
            }
        }
        updateRecycler(filteredNotes); // update the recycler view with the filtered notes

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101 && resultCode == RESULT_OK) {
            // if the request code is 101 and the result code is ok
            Notes notes = (Notes) data.getSerializableExtra("notes"); // get the notes from the intent
            database.mainDAO().insert(notes); // insert the notes into the database
            notesList.add(notes); // add the notes to the list
            updateRecycler(notesList); // update the recycler view
            notesListAdapter.notifyDataSetChanged(); // notify the adapter that the data set has changed
        } else if (requestCode == 102 && resultCode == RESULT_OK) {
            // if the request code is 102 and the result code is ok
            Notes new_notes = (Notes) data.getSerializableExtra("notes"); // get the notes from the intent
            database.mainDAO().update(new_notes.getID(), new_notes.getTitle(), new_notes.getNotes()); // update the notes in the database
            notesList.clear();
            notesList.addAll(database.mainDAO().getAllNotes()); // get all the notes from the database
            updateRecycler(notesList); // update the recycler view
            notesListAdapter.notifyDataSetChanged(); // notify the adapter that the data set has changed

        }
    }

    private void updateRecycler(List<Notes> notesList) {
        recyclerView.setHasFixedSize(true); // set the recycler view to have fixed size
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)); // set the layout manager to be a staggered grid layout manager with 2 columns and vertical orientation
        notesListAdapter = new NotesListAdapter(this, notesList, notesClickListener); // create a new adapter for the recycler view
        recyclerView.setAdapter(notesListAdapter); // set the adapter for the recycler view
    }

    private final NotesClickListener notesClickListener = new NotesClickListener() {
        @Override
        public void onClick(Notes notes) {
            // when the user clicks on a note
            Intent intent = new Intent(MainActivity.this, TakeNoteActivity.class);
            intent.putExtra("oldNote", notes); // put the notes in the intent
            startActivityForResult(intent, 102); // start the take note activity

        }

        @Override
        public void onLongClick(Notes notes, CardView cardView) {
            // when the user long clicks on a note
            selected_note = new Notes(); // create a new note
            selected_note = notes; // set the selected note to the note that was long clicked
            cardView.setCardBackgroundColor(getResources().getColor(R.color.white)); // set the card background color to the primary color
            cardView.setCardElevation(5); // set the card elevation to 5dp
            showPopupMenu(cardView); // show the popup menu
        }
    };

    private void showPopupMenu(CardView cardView) {
        PopupMenu popupMenu = new PopupMenu(this, cardView); // create a new popup menu
        popupMenu.setOnMenuItemClickListener(this); // set the on menu item click listener
        popupMenu.inflate(R.menu.popup_menu); // inflate the popup menu
        popupMenu.show(); // show the popup menu
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        // when the user clicks on a menu item
        switch (item.getItemId()) {
            case R.id.pin:
                // if the user clicks on the pin menu item
                if (selected_note.isPinned()) {
                    // if the note is pinned
                    database.mainDAO().updatePinned(selected_note.getID(), false); // update the note in the database
                    selected_note.setPinned(false); // set the note to be unpinned
                    Toast.makeText(this, "Note Unpinned", Toast.LENGTH_SHORT).show(); // show a toast message
                } else {
                    // if the note is not pinned
                    database.mainDAO().updatePinned(selected_note.getID(), true); // update the note in the database
                    selected_note.setPinned(true); // set the note to be pinned
                    Toast.makeText(this, "Note Pinned", Toast.LENGTH_SHORT).show(); // show a toast message
                }
                notesList.clear(); // clear the notes list
                notesList.addAll(database.mainDAO().getAllNotes()); // get all the notes from the database
                updateRecycler(notesList); // update the recycler view
                return true;

            case R.id.delete:
                // if the user clicks on the delete menu item
                database.mainDAO().delete(selected_note); // delete the note from the database
                notesList.remove(selected_note); // remove the note from the notes list
                updateRecycler(notesList); // update the recycler view
                Toast.makeText(this, "Note Deleted", Toast.LENGTH_SHORT).show(); // show a toast message
                return true;

            default:
                return false;
        }
    }
}