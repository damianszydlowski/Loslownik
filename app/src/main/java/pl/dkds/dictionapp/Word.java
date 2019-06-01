package pl.dkds.dictionapp;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class Word implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public int word_id;

    @ColumnInfo(name = "word")
    public String Word;

    @ColumnInfo(name = "translation")
    public String Translation;

    @ColumnInfo(name = "definition")
    public String Definition;

    @ColumnInfo(name = "example")
    public String Example;

    @ColumnInfo(name = "last_date")
    public long Date;

    public void setWord (String word){
        this.Word = word;
        this.Translation = "";
        this.Definition = "";
        this.Example = "";
        this.Date = 0;
    }

    public void setTranslation(String translation){
        this.Translation = translation;
    }
    public void setDefinition(String definition){
        this.Definition = definition;
    }
    public void setExample(String example){
        this.Example = example;
    }
    public void setDate(long date) { this.Date = date; }

    public String getTranslation(){
        return this.Translation;
    }
    public String getDefinition(){
        return this.Definition;
    }
    public String getExample(){
        return this.Example;
    }
    public String getWord() { return this.Word; }
}