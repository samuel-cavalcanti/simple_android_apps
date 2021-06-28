package edu.harvard.cs50.notes;

import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

@Dao
public interface NoteDao {
    @Query("INSERT INTO notes (content,selected) VALUES ('New note',0)")
    void create();

    @Query("SELECT * FROM notes")
    List<Note> getAll();

    @Query("UPDATE notes SET content = :content WHERE id = :id")
    void saveContent(String content, int id);

    @Query("SELECT * FROM notes WHERE selected = 1")
    List<Note> getSelected();

    @Query("UPDATE notes SET selected = 1  WHERE id in (:ids)")
    void saveSelectedItems(List<Integer> ids);

    @Query("DELETE  FROM notes WHERE id IN (:ids)")
    void delete(List<Integer> ids);

    @Query("UPDATE notes SET selected = 0 WHERE selected = 1")
    void resetSelectedItems();
}
