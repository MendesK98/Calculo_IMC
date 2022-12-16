package com.example.calculoimc.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
        TextView show2 = (TextView) findViewById(R.id.tMensagem);
        Button save = (Button) findViewById(R.id.save);
        Button historico = (Button) findViewById(R.id.historico);

        //Criando usuario;
        User user = new User();
        String imcDefinicao ="Isso significa que ";

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
                + "\n\nSeu índice de massa corporal eh: " + formatador.format(user.getImc()) + "\nO Indíce recomendado eh entre 18,5 e 25";

        imcDefinicao += user.imcMessage();

        show2.setText(imcDefinicao);
        show.setText(resultado);

        //Evento botao para adicionar IMC na lista de IMCs
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user.adicionarIMC();
            }
        });
        Intent hist = new Intent(this, Historic.class);
        //Evento botao para visualizar a lista de IMCs

        //FALTA: Iniciar atividade Historico e exibir lista de IMCs calculados
        historico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(hist);
                finish();
            }
        });


    }
}