package com.example.calculoimc.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.calculoimc.model.UserAtributos;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DataBase extends SQLiteOpenHelper {

    public static final String DB_NAME = "historico.db";
    public static final int DB_VERSION = 6;
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

        String criarTabelaUsuarios = "CREATE TABLE usuarios (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nome TEXT UNIQUE, " +
                "idade INTEGER, " +
                "altura REAL, " +
                "meta_peso REAL, " +
                "meta_imc REAL);";
        db.execSQL(criarTabelaUsuarios);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABELA_HISTORICO);
        db.execSQL("DROP TABLE IF EXISTS usuarios");
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

    // Alterando o nome e adicionando o parâmetro de filtro
    public List<UserAtributos> getRegistriesByUser(String nomeUsuario, int limite) {
        List<UserAtributos> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Adicionamos a cláusula WHERE para filtrar pelo nome
        // O ORDER BY continua garantindo que os mais recentes apareçam primeiro
        String query = "SELECT * FROM " + TABELA_HISTORICO +
                " WHERE " + COL_NOME + " = ?" +
                " ORDER BY id DESC LIMIT " + limite;;

        // Passamos o nomeUsuario como argumento para evitar SQL Injection
        Cursor cursor = db.rawQuery(query, new String[]{nomeUsuario});

        try {
            if (cursor.moveToFirst()) {
                do {
                    UserAtributos user = new UserAtributos();

                    user.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)));
                    user.setNome(cursor.getString(cursor.getColumnIndexOrThrow(COL_NOME)));
                    user.setIdade(cursor.getInt(cursor.getColumnIndexOrThrow(COL_IDADE)));

                    // Preenchendo os dados do IMC
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
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
        return lista;
    }

    public List<UserAtributos> getRegistriesByPeriod(String nomeUsuario, int meses) {
        List<UserAtributos> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // A consulta usa a função date() do SQLite para subtrair os meses da data atual
        String query = "SELECT * FROM " + TABELA_HISTORICO +
                " WHERE " + COL_NOME + " = ?" +
                " AND " + DATA_HORA + " >= date('now', '-" + meses + " months')" +
                " ORDER BY id DESC";

        Cursor cursor = db.rawQuery(query, new String[]{nomeUsuario});

        try {
            if (cursor.moveToFirst()) {
                do {
                    UserAtributos user = new UserAtributos();

                    user.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)));
                    user.setNome(cursor.getString(cursor.getColumnIndexOrThrow(COL_NOME)));
                    user.setIdade(cursor.getInt(cursor.getColumnIndexOrThrow(COL_IDADE)));

                    user.getImc().setPeso(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_PESO)));
                    user.getImc().setAltura(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_ALTURA)));
                    user.getImc().setIndice(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_IMC)));
                    user.getImc().setCircunferencia(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_CIRCUNFERENCIA)));

                    // --- CORREÇÃO AQUI ---
                    String dataDoBanco = cursor.getString(cursor.getColumnIndexOrThrow(DATA_HORA));
                    try {
                        // O SQLite salva como yyyy-MM-dd HH:mm:ss
                        SimpleDateFormat formatoBanco = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                        Date dataConvertida = formatoBanco.parse(dataDoBanco);
                        user.getImc().setData(dataConvertida);
                    } catch (Exception e) {
                        Log.e("DB_ERROR", "Erro ao converter data no periodo: " + e.getMessage());
                    }

                    lista.add(user);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
        return lista;
    }


    public void deletarRegistro(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.delete(TABELA_HISTORICO, COL_ID + " = ?", new String[]{String.valueOf(id)});
        } finally {
            db.close();
        }
    }

    public void gerarDadosTeste(String nomeUsuario) {
        SQLiteDatabase db = this.getWritableDatabase();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        db.beginTransaction(); // Usar transação deixa o processo muito mais rápido
        try {
            for (int mes = 0; mes < 12; mes++) {
                for (int i = 0; i < 20; i++) {
                    ContentValues values = new ContentValues();

                    // Calcula a data retroativa
                    Calendar cal = Calendar.getInstance();
                    cal.add(Calendar.MONTH, -mes);
                    cal.add(Calendar.DAY_OF_MONTH, -i); // Espalha os registros pelos dias
                    String dataRetroativa = sdf.format(cal.getTime());

                    // Gera valores de IMC aleatórios para o gráfico não ficar reto
                    double pesoFake = 70 + (Math.random() * 20); // Entre 70kg e 90kg
                    double imcFake = pesoFake / (1.75 * 1.75);

                    values.put(COL_NOME, nomeUsuario);
                    values.put(COL_IDADE, 30);
                    values.put(COL_PESO, pesoFake);
                    values.put(COL_ALTURA, 1.75);
                    values.put(COL_IMC, imcFake);
                    values.put(COL_CIRCUNFERENCIA, 85.0);
                    values.put(DATA_HORA, dataRetroativa); // Inserindo a data manual

                    db.insert(TABELA_HISTORICO, null, values);
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
    }
}