package com.cuneyt.mynotes;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.cuneyt.mynotes.adapters.NoteAdapter;
import com.cuneyt.mynotes.entities.NoteModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference referenceNote;
    private ArrayList<NoteModel> noteModels = new ArrayList<>();
    private NoteAdapter noteAdapter;

    private FloatingActionButton fabNoteAdd;
    private RecyclerView rvNoteList;

    private void visualObjects(){
        fabNoteAdd = findViewById(R.id.fabNoteAdd);
        rvNoteList = findViewById(R.id.rvNoteList);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            visualObjects();

            referenceNote = FirebaseDatabase.getInstance().getReference(getResources().getString(R.string.db_note));

            fabNoteAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intentNote = new Intent(MainActivity.this, NoteActivity.class);
                    startActivity(intentNote);
                }
            });

            show();

            fabButtonHiddenRecyclerView();
            return insets;
        });
    }

    private void show(){
        rvNoteList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, true); // VERTICAL, true: RecyclerView'e eklenen veri en alttan üste doğru eklenir.
        linearLayoutManager.setStackFromEnd(true); // RecyclerView'e eklenen veri sayfayı otomatik kaydırır.
        rvNoteList.setItemAnimator(new DefaultItemAnimator());
        rvNoteList.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewInflater = inflater.inflate(R.layout.design_row_note, null); // Tasarım bağlandı.

        noteAdapter = new NoteAdapter(this, noteModels);
        rvNoteList.setAdapter(noteAdapter);

        referenceNote.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                noteModels.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    NoteModel note = dataSnapshot.getValue(NoteModel.class);

                    noteModels.add(note);
                }

                Collections.sort(noteModels, new Comparator<NoteModel>() { //RecyclerView A->Z sıralama
                    @Override
                    public int compare(NoteModel nModel, NoteModel t1) {
                        return nModel.getDate().compareToIgnoreCase(t1.getDate());
                        // return Integer.compare(t1.getNumber(), ggModel.getNumber());
                    }
                });

                noteAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void fabButtonHiddenRecyclerView() {

        rvNoteList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy < 0) {
                    fabNoteAdd.show();
                } else if (dy > 0) {
                    fabNoteAdd.hide();
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }
}