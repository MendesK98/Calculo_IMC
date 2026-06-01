package com.example.calculoimc.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.calculoimc.model.UserAtributos;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    private SQLiteDatabase db;
    private DataBase dbHelper;

    public UserDAO(Context context) {
        dbHelper = new DataBase(context);
        this.db = dbHelper.getWritableDatabase();
    }

    public void open() {
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public boolean salvar(UserAtributos user) {
        ContentValues values = new ContentValues();
        values.put("nome", user.getNome());
        values.put("idade", user.getIdade());
        values.put("altura", user.getAltura());
        values.put("meta_peso", user.getMetaPeso());
        values.put("meta_imc", user.getMetaIMC());

        if (user.getId() > 0) {
            return db.update("usuarios", values, "id = ?", new String[]{String.valueOf(user.getId())}) > 0;
        } else {
            return db.insert("usuarios", null, values) != -1;
        }
    }

    public boolean atualizar(UserAtributos user) {
        ContentValues values = new ContentValues();
        values.put("nome", user.getNome());
        values.put("idade", user.getIdade());
        values.put("altura", user.getAltura());
        values.put("meta_peso", user.getMetaPeso());

        String[] args = {String.valueOf(user.getId())};
        return db.update("usuarios", values, "id = ?", args) > 0;
    }
    public boolean excluir(int id) {
        return db.delete("usuarios", "id = ?", new String[]{String.valueOf(id)}) > 0;
    }


    public List<UserAtributos> listarTodos() {
        List<UserAtributos> lista = new ArrayList<>();
        Cursor cursor = db.query("usuarios", null, null, null, null, null, "nome ASC");

        if (cursor.moveToFirst()) {
            do {
                UserAtributos u = new UserAtributos();
                u.setId(cursor.getInt(0));
                u.setNome(cursor.getString(1));
                u.setIdade(cursor.getInt(2));
                u.setAltura(cursor.getDouble(3));     // Novo campo
                u.setMetaPeso(cursor.getDouble(4));   // Novo campo
                // meta_imc está no índice 5, mas ele é calculado no objeto
                lista.add(u);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return lista;
    }

    // DELETE (CRUD)
    public void deletar(int id) {
        db.delete("usuarios", "id = ?", new String[]{String.valueOf(id)});
    }
}