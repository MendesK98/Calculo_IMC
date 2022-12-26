package com.example.calculoimc.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.calculoimc.R;
import com.example.calculoimc.model.IMC;
import com.example.calculoimc.model.User;
import com.example.calculoimc.model.Usuarios;

import java.text.DecimalFormat;

public class Resultados extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultados);

        TextView show = (TextView) findViewById(R.id.tResultado);
        TextView show2 = (TextView) findViewById(R.id.tMensagem);
        Button save = (Button) findViewById(R.id.save);
        Button historico = (Button) findViewById(R.id.historico);

        //Criando IMC
        IMC imc = new IMC();
        String imcDefinicao ="Isso significa que ";

        //Recebendo dados via Intent
        Intent b = getIntent();
        imc.setAltura(b.getDoubleExtra("altura", 0));
        imc.setPeso(b.getDoubleExtra("peso", 0));

        imc.setIndice(imc.calcIMC());

        //Criando formatador de decimal para apresentar resultados
        DecimalFormat formatador = new DecimalFormat("0.00");

        //Apresentando resultados
        String resultado = "Seu Peso: " + imc.getPeso() + "\nSua Altura: " + imc.getAltura()
                + "\n\nSeu índice de massa corporal eh: " + formatador.format(imc.getIndice())
                + "\nO Indíce recomendado eh entre 18,5 e 25";

        imcDefinicao += imc.imcMessage();

        show2.setText(imcDefinicao);
        show.setText(resultado);

        Intent his = new Intent(this, Historic.class);
        historico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(his);
                finish();
            }
        });

        //Parei aqui, criar método para salvar o imc na lista de imcs de cada usuario, precisa criar loginc com usuarios e modo de entrada anonimo
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User lucas = new User("Lucas");
                User ana = new User("Ana");
                User maria = new User("Maria");

                IMC x = new IMC();
                x.setIndice(25.3);
                x.setAltura(1.7);
                x.setPeso(60.7);
                IMC x2 = new IMC();
                x2.setIndice(25.3);
                x2.setAltura(1.7);
                x2.setPeso(60.7);
                IMC x3 = new IMC();
                x3.setIndice(25.3);
                x3.setAltura(1.7);
                x3.setPeso(60.7);

                lucas.setImc(x);
                ana.setImc(x2);
                maria.setImc(x3);

                Usuarios.addUser(lucas);
                Usuarios.addUser(ana);
                Usuarios.addUser(maria);

            }
        });

    }
}