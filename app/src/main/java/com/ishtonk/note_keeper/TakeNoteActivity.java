package com.ishtonk.note_keeper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.ishtonk.note_keeper.Models.Notes;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TakeNoteActivity extends AppCompatActivity {
    EditText note_title_edit, note_content_edit;
    ImageView save_note_image;
    Notes notes;
    boolean isOldNote = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_note);

        save_note_image = findViewById(R.id.save_note_image); // get the save note image view
        note_title_edit = findViewById(R.id.note_title_edit); // get the note title edit text view
        note_content_edit = findViewById(R.id.note_content_edit); // get the note content edit text view

        notes = new Notes(); // create a new notes object
        try {
            notes = (Notes) getIntent().getSerializableExtra("oldNote"); // get the old note from the intent
            note_title_edit.setText(notes.getTitle()); // set the note title edit text view to the old note title
            note_content_edit.setText(notes.getNotes()); // set the note content edit text view to the old note content
            isOldNote = true; // set the isOldNote boolean to true
        } catch (Exception e) {
            e.printStackTrace();
        }


        save_note_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // when the save note image is clicked
                String title = note_title_edit.getText().toString(); // get the title from the note title edit text view
                String content = note_content_edit.getText().toString(); // get the content from the note content edit text view

                // if the content is empty
                if (content.isEmpty()) {
                    Toast.makeText(TakeNoteActivity.this, "Please enter some content", Toast.LENGTH_SHORT).show(); // show a toast if the content is empty
                    return;
                }

                SimpleDateFormat dateFormatter = new SimpleDateFormat("EEE, d mmm yyyy HH:mm a") ; // create a new date formatter
                Date date = new Date(); // create a new date

                if (!isOldNote) {
                    notes = new Notes(); // create a new note
                }

                notes.setTitle(title); // set the title of the note
                notes.setNotes(content); // set the content of the note
                notes.setDate(dateFormatter.format(date)); // set the date of the note

                Intent intent = new Intent(); // create a new intent
                intent.putExtra("notes", notes); // put the note in the intent
                setResult(RESULT_OK, intent); // set the result of the intent to be ok
                finish(); // finish the activity
            }
        });
    }
}