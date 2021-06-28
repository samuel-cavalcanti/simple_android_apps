package edu.harvard.cs50.notes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {
    private List<Note> notes = new ArrayList<>();
    private List<Integer> selectedNotes = new ArrayList<>();

    public class NoteViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout containerView;
        public TextView nameTextView;
        public int position;

        public NoteViewHolder(View view) {
            super(view);
            this.containerView = view.findViewById(R.id.note_row);
            this.nameTextView = view.findViewById(R.id.note_row_name);
            this.position = -1;
            this.containerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Note note = (Note) containerView.getTag();
                    Intent intent = new Intent(v.getContext(), NoteActivity.class);
                    intent.putExtra("id", note.id);
                    intent.putExtra("content", note.content);


                    context.startActivity(intent);
                }


            });

            this.containerView.setOnLongClickListener(new View.OnLongClickListener() {
                @SuppressLint("ResourceAsColor")
                @Override
                public boolean onLongClick(View v) {

                    if (position == -1)
                        return false;

                    Note current = notes.get(position);

                    current.selected = !current.selected;


                    notifyItemChanged(position);
                    if (current.selected)
                        selectedNotes.add(current.id);
                    else
                        selectedNotes.remove(current.id);

                    return false;
                }
            });


        }
    }


    @Override
    public NoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.note_row, parent, false);

        return new NoteViewHolder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(NoteViewHolder holder, int position) {
        Note current = notes.get(position);
        holder.containerView.setTag(current);
        holder.nameTextView.setText(current.content);
        holder.position = position;

        if (current.selected)
            holder.containerView.setBackgroundColor(holder.containerView.getResources().getColor(R.color.colorAccent));
        else
            holder.containerView.setBackgroundColor(holder.containerView.getResources().getColor(R.color.transparent));

    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public void reload() {
        MainActivity.database.noteDao().resetSelectedItems();
        notes = MainActivity.database.noteDao().getAll();
        notifyDataSetChanged();
    }


    public List<Integer> getSelectedNotes() {
        return selectedNotes;
    }
}