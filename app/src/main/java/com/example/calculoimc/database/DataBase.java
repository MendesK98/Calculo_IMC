package com.example.calculoimc.database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class DataBase extends SQLiteOpenHelper {

    public static final String DB_NAME = "usuarios";
    public static final int DB_VERSION = 1;

    private String id = "ID";
    private String nome = "Nome";
    private String idade = "Idade";
    private String peso = "Peso";
    private String imc ="IMC";


    SQLiteDatabase db;

    public DataBase(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        db = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String tabelaUsuario = "CREATE TABLE usuarios (" +
                id + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                nome + " TEXT, " +
                idade + " INTEGER, " +
                peso + " REAL, " +
                imc + " REAL" +
                ");";

        try {
            db.execSQL(tabelaUsuario);
        } catch (SQLException e) {
            Log.e("DB_LOG", "onCreate: " + e.getLocalizedMessage());
        }
    }

   public void addValorDB () {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

    }
}
