package com.blueberryinc.foreignlearner;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION=1;


    public static final String DATABASE_NAME="DATABASE_WORDS";
    public static final String TABLE_WORD_JAPANESE="TABLE_WORD_JAPANESE";
    public static final String TABLE_WORD_ENGLISH="TABLE_WORD_ENGLISH";



    public static final String KEY_ID="_id";
    public static final String KEY_PROGRESS="KEY_PROGRESS";
    public static final String KEY_TAGS="KEY_TAGS";
    public static final String KEY_WORD="KEY_WORD";
    public static final String KEY_TRANSCRIPTION="KEY_TRANSCRIPTION";
    public static final String KEY_TRANSLATION="KEY_TRANSLATION";

    public DBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table "+ TABLE_WORD_JAPANESE+"("+ KEY_ID
                            +" integer primary key,"
                            + KEY_WORD+" text,"
                            +KEY_TRANSCRIPTION+" text,"
                            +KEY_TRANSLATION+" text,"
                            +KEY_TAGS+" text,"
                            +KEY_PROGRESS+" ,text)");
        sqLiteDatabase.execSQL("create table "+ TABLE_WORD_ENGLISH+"("+ KEY_ID
                +" integer primary key,"
                + KEY_WORD+" text,"
                +KEY_TRANSCRIPTION+" text,"
                +KEY_TRANSLATION+" text,"
                +KEY_TAGS+" text,"
                +KEY_PROGRESS+" ,text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
