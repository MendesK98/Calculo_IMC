package com.example.calculoimc.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.calculoimc.R;
import com.example.calculoimc.model.IMC;

import java.text.DecimalFormat;
import com.example.calculoimc.database.DataBase;
import com.example.calculoimc.model.UserAtributos;

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


        DataBase db = new DataBase(this);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserAtributos user = new UserAtributos();
                user.setNome("Teste");
                user.setIdade(20);
                user.getImc().setPeso(imc.getPeso());
                user.getImc().setAltura(imc.getAltura());
                user.setImc(imc);

                boolean sucesso = db.addIMC(user);

                if (sucesso) {
                    Toast.makeText(Resultados.this, "Salvo com sucesso!", Toast.LENGTH_SHORT).show();
                    save.setEnabled(false);
                } else {
                    Toast.makeText(Resultados.this, "Erro ao salvar.", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
}