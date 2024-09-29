package com.cuneyt.mynotes;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.cuneyt.mynotes.assistantclass.DateTime;
import com.cuneyt.mynotes.assistantclass.RandomId;
import com.cuneyt.mynotes.entities.NoteModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

public class NoteActivity extends AppCompatActivity {

    private DatabaseReference referenceNote;
    private NoteModel noteModel;

    private DateTime dateTime = new DateTime();
    private RandomId random = new RandomId();

    private TextInputEditText inputNoteTitle;
    private TextInputEditText inputNoteWrite;
    private FloatingActionButton fabSave;

    private void visualObjects() {
        inputNoteTitle = findViewById(R.id.inputNoteTitle);
        inputNoteWrite = findViewById(R.id.inputNoteWrite);
        fabSave = findViewById(R.id.fabSave);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_note);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            visualObjects();

            referenceNote = FirebaseDatabase.getInstance().getReference(getResources().getString(R.string.db_note));

            fabSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    add();
                    Intent intentList = new Intent(NoteActivity.this, MainActivity.class);
                    startActivity(intentList);
                }
            });

            incomingUpdateInfo();

            return insets;
        });
    }

    private void add() {

        String title = inputNoteTitle.getText().toString();
        String note = inputNoteWrite.getText().toString();
        String date = dateTime.currentlyDateTime("dd.MM.yyyy");
        String id = title + "-" + random.randomUUID().toString();

        referenceNote.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                noteModel = new NoteModel(id, title, note, date);

                referenceNote.child(id).setValue(noteModel);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void incomingUpdateInfo(){
        String incomingId = getIntent().getStringExtra("id");
        String incomingTitle = getIntent().getStringExtra("title");
        String incomingNote = getIntent().getStringExtra("note");
        String incomingDate = getIntent().getStringExtra("date");

        inputNoteTitle.setText(incomingTitle);
        inputNoteWrite.setText(incomingNote);
    }
}