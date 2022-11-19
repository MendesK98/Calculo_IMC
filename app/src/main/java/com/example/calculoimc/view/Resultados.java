package com.example.calculoimc.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.calculoimc.R;
import com.example.calculoimc.model.User;

import java.text.DecimalFormat;

public class Resultados extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultados);

        TextView show = (TextView) findViewById(R.id.tResultado);

        //Criando usuario;
        User user = new User();
        String imcDefinicao ;

        //Recebendo dados via Intent
        Intent b = getIntent();
        user.setIdade(b.getIntExtra("idade", 0));
        user.setAltura(b.getDoubleExtra("altura", 0));
        user.setPeso(b.getDoubleExtra("peso", 0));

        user.calcIMC(user.getAltura(), user.getPeso());

        //Criando formatador de decimal para apresentar resultados
        DecimalFormat formatador = new DecimalFormat("0.00");

        //Apresentando resultados
        String resultado = "Sua Idade: " + user.getIdade() + "\nSeu Peso: " + user.getPeso() + "\nSua Altura: " + user.getAltura()
                + "\n\nSeu índice de massa corporal eh: " + formatador.format(user.getImc()) + " e isso significa que ";

        resultado += user.imcMessage();


        show.setText(resultado);
    }
}