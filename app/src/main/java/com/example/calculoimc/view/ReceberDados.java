package com.example.calculoimc.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.calculoimc.R;
import com.example.calculoimc.model.MetodosGerais;
import com.google.android.material.textfield.TextInputEditText;

public class ReceberDados extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.receber_dados);

        TextInputEditText idade = (TextInputEditText) findViewById(R.id.tIdade);
        TextInputEditText peso = (TextInputEditText) findViewById(R.id.tPeso);
        TextInputEditText altura = (TextInputEditText) findViewById(R.id.tAltura);

        peso.setRawInputType(Configuration.KEYBOARD_12KEY);
        altura.setRawInputType(Configuration.KEYBOARD_12KEY);
        idade.setRawInputType(Configuration.KEYBOARD_12KEY);

        Intent a = new Intent (this, Resultados.class);
        Button calcular = (Button) findViewById(R.id.btCalcular);

        calcular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String sAltura = altura.getText().toString();
                String sPeso = peso.getText().toString();

                MetodosGerais mtg = new MetodosGerais();

                Integer iIdade = Integer.parseInt(idade.getText().toString().trim());
                Double dPeso = mtg.ConverStringToDouble(sPeso);
                Double dAltura = mtg.ConverStringToDouble(sAltura);

                a.putExtra("idade", iIdade);
                a.putExtra("peso", dPeso);
                a.putExtra("altura", dAltura);

                startActivity(a);
                finish();
            }
        });


    }
}