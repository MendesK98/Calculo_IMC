package com.example.calculoimc.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.calculoimc.model.UserAtributos;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DataBase extends SQLiteOpenHelper {

    public static final String DB_NAME = "historico.db"; // Adicione o .db
    public static final int DB_VERSION = 2;
    public static final String TABELA_HISTORICO = "historico";
    public static final String COL_ID = "id";
    public static final String COL_NOME = "nome";
    public static final String COL_IDADE = "idade";
    public static final String COL_PESO = "peso";
    public static final String COL_ALTURA = "altura";
    public static final String COL_IMC = "imc";
    public static final String COL_CIRCUNFERENCIA = "circunferencia";
    public static final String DATA_HORA = "data_hora";

    public DataBase(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Use String.format ou concatenação limpa
        String criarTabela = "CREATE TABLE " + TABELA_HISTORICO + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NOME + " TEXT, " +
                COL_IDADE + " INTEGER, " +
                COL_PESO + " REAL, " +
                COL_ALTURA + " REAL, " +
                COL_IMC + " REAL, " +
                COL_CIRCUNFERENCIA + " REAL," +
                DATA_HORA + " DATETIME DEFAULT CURRENT_TIMESTAMP" +
                ");";

        db.execSQL(criarTabela);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABELA_HISTORICO);
        onCreate(db);
    }

    public boolean addIMC(UserAtributos userAtributos) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(COL_NOME, userAtributos.getNome());
        values.put(COL_IDADE, userAtributos.getIdade());

        values.put(COL_PESO, userAtributos.getImc().getPeso());
        values.put(COL_ALTURA, userAtributos.getImc().getAltura());

        values.put(COL_IMC, userAtributos.getImc().getIndice());
        values.put(COL_CIRCUNFERENCIA, userAtributos.getImc().getCircunferencia());

        long resultado = db.insert(TABELA_HISTORICO, null, values);

        db.close();

        return resultado != -1;
    }

    public List<UserAtributos> getAllUsersWithIMC() {
        List<UserAtributos> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Consulta todos os dados
        String query = "SELECT * FROM " + TABELA_HISTORICO + " ORDER BY " + DATA_HORA + " DESC";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                // Criamos o objeto principal
                UserAtributos user = new UserAtributos();
                user.setNome(cursor.getString(cursor.getColumnIndexOrThrow(COL_NOME)));
                user.setIdade(cursor.getInt(cursor.getColumnIndexOrThrow(COL_IDADE)));
                user.getImc().setPeso(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_PESO)));
                user.getImc().setAltura(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_ALTURA)));
                user.getImc().setIndice(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_IMC)));
                user.getImc().setCircunferencia(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_CIRCUNFERENCIA)));

                String dataDoBanco = cursor.getString(cursor.getColumnIndexOrThrow(DATA_HORA));

                try {
                    SimpleDateFormat formatoBanco = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    Date dataConvertida = formatoBanco.parse(dataDoBanco);

                    user.getImc().setData(dataConvertida);
                } catch (Exception e) {
                    Log.e("DB_ERROR", "Erro ao converter data: " + e.getMessage());
                }

                lista.add(user);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return lista;
    }
}