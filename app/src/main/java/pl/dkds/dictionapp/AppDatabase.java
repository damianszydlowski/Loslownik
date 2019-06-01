package pl.dkds.dictionapp;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {Word.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract WordDao wordDao();
}