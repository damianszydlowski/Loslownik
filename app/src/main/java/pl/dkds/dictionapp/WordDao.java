package pl.dkds.dictionapp;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface WordDao {
    @Query("Select * FROM word ORDER BY last_date DESC")
    List<Word> getAll();

    @Query("Select * FROM word ORDER BY last_date DESC LIMIT 5")
    List<Word> getrecent();

    @Query("Select Count(*) FROM word WHERE word = :word")
    int Exists(String word);

    @Query("Select * FROM word WHERE word = :word LIMIT 1")
    Word Get(String word);

    @Insert
    void insert(Word word);

    @Delete
    void delete(Word word);

    @Update
    void update(Word word);
}