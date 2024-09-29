package com.cuneyt.mynotes.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.cuneyt.mynotes.NoteActivity;
import com.cuneyt.mynotes.R;
import com.cuneyt.mynotes.entities.NoteModel;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.MyViewHolder>{

    private DatabaseReference referenceNote ;
    private Context mContext;
    private ArrayList<NoteModel> noteModelArrayList;

    public NoteAdapter(Context mContext, ArrayList<NoteModel> noteModelArrayList) {
        this.mContext = mContext;
        this.noteModelArrayList = noteModelArrayList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView txtRowTitle, txtRowNote, txtRowDate;
        ConstraintLayout constRow;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txtRowTitle = itemView.findViewById(R.id.txtRowTitle);
            txtRowNote = itemView.findViewById(R.id.txtRowNote);
            txtRowDate = itemView.findViewById(R.id.txtRowDate);
            constRow = itemView.findViewById(R.id.constRow);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.design_row_note, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteAdapter.MyViewHolder holder, int position) {
        String id = noteModelArrayList.get(position).getId();
        String title = noteModelArrayList.get(position).getNoteTitle();
        String note = noteModelArrayList.get(position).getNote();
        String date = noteModelArrayList.get(position).getDate();

        holder.txtRowTitle.setText(title);
        holder.txtRowNote.setText(note);
        holder.txtRowDate.setText(date);

        holder.constRow.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                Snackbar snackbar = Snackbar.make(view, "Not siliniyor...", Snackbar.LENGTH_LONG)
                        .setAction("Sil", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                delete(id);
                                Snackbar.make(view, "Silindi", Snackbar.LENGTH_SHORT).show();
                            }
                        });
                snackbar.setActionTextColor(mContext.getResources().getColor(R.color.text_cl));
                snackbar.setBackgroundTint(mContext.getResources().getColor(R.color.fab_cl));
                snackbar.setTextColor(mContext.getResources().getColor(R.color.text_cl));
                snackbar.show();

                return true;
            }
        });

        holder.constRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentUpdate = new Intent(mContext, NoteActivity.class);
                intentUpdate.putExtra("id", id);
                intentUpdate.putExtra("title", title);
                intentUpdate.putExtra("note", note);
                intentUpdate.putExtra("date", date);
                mContext.startActivity(intentUpdate);
            }
        });
    }

    @Override
    public int getItemCount() {
        return noteModelArrayList.size();
    }

    private void delete(String id){

        referenceNote = FirebaseDatabase.getInstance().getReference(mContext.getResources().getString(R.string.db_note)).child(id);

        referenceNote.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                referenceNote.removeValue();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void update(){

    }
}
