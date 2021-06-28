package edu.harvard.cs50.notes;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private NotesAdapter adapter;
    public static NotesDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = Room
                .databaseBuilder(getApplicationContext(), NotesDatabase.class, "notes")
                .allowMainThreadQueries()
                .build();

        recyclerView = findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(this);
        adapter = new NotesAdapter();

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        setClickListeners();

    }

    @Override
    protected void onResume() {
        super.onResume();

        adapter.reload();
    }

    private void setClickListeners() {
        FloatingActionButton fab;

        fab = findViewById(R.id.add_note_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                database.noteDao().create();
                adapter.reload();
            }
        });

        fab = findViewById(R.id.remove_note_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Integer> selectedNotes = adapter.getSelectedNotes();
                database.noteDao().delete(selectedNotes);
                adapter.reload();
            }
        });
    }
}
