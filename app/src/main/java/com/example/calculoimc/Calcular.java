package com.example.calculoimc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;

public class Calcular extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calcular);

        TextInputEditText idade = (TextInputEditText) findViewById(R.id.tIdade);
        TextInputEditText peso = (TextInputEditText) findViewById(R.id.tPeso);
        TextInputEditText altura = (TextInputEditText) findViewById(R.id.tAltura);

        Intent a = new Intent (this, Resultados.class);
        Button calcular = (Button) findViewById(R.id.btCalcular);

        calcular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer iIdade = Integer.parseInt(idade.getText().toString());
                Double dPeso = Double.parseDouble(peso.getText().toString());
                Double dAltura = Double.parseDouble(altura.getText().toString());
                a.putExtra("idade", iIdade);
                a.putExtra("peso", dPeso);
                a.putExtra("altura", dAltura);

                startActivity(a);
                finish();
            }
        });


    }
}