package com.example.calculoimc.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.calculoimc.R;
import com.example.calculoimc.database.DataBase;
import com.example.calculoimc.model.IMC;
import com.example.calculoimc.model.UserAtributos;

import java.util.List;

public class Historic extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historic);

        ListView listView = findViewById(R.id.listViewHistorico); // ID do seu ListView no XML
        DataBase db = new DataBase(this);

        // Busca a lista de IMCs do banco
        List<UserAtributos> listaImc = db.getAllUsersWithIMC();

        // Cria um adaptador simples (usa o toString() da sua classe IMC)
        ArrayAdapter<UserAtributos> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                listaImc
        );

        listView.setAdapter(adapter);
        }

}