package com.example.calculoimc.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.calculoimc.R;
import com.example.calculoimc.database.DataBase;

public class MainActivity extends AppCompatActivity {

    String TAG = "erro";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //DB
        DataBase dataBase = new DataBase(this);


        Button calcular = (Button) findViewById(R.id.bCalcular);

        Intent i = new Intent(this, ReceberDados.class);
        calcular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(i);
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_historico) {
            // Navegar para a sua Activity de Histórico
            Intent intent = new Intent(this, Historic.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}