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

        //TODO criar botao salvar que salvará os IMCs de um determinado usuário em sua própria lista.


    }
}