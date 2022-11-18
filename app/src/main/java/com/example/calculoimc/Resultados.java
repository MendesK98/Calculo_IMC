package com.example.calculoimc;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import java.text.DecimalFormat;

public class Resultados extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultados);

        TextView show = (TextView) findViewById(R.id.tResultado);

        Intent b = getIntent();
        Integer idade = b.getIntExtra("idade", 0);
        Double altura = b.getDoubleExtra("altura", 0);
        Double peso = b.getDoubleExtra("peso", 0);

        double imc = peso / (altura * altura);
        DecimalFormat formatador = new DecimalFormat("0.00");


        String resultado = "Sua Idade: " + idade.toString() + "\nSeu Peso: " + peso.toString() + "\nSua Altura: " + altura.toString()
                + "\n\nSeu índice de massa corporal eh: " + formatador.format(imc) + " e isso significa que";
        String imcDefinicao = " ";


        if (imc > 0 && imc <= 18.5) {
            imcDefinicao += "seu estado atual é de magreza, sinta-se à vontade para comer mais alimentos saudáveis e gostosos.";
        } else if (imc > 18.5 && imc < 25) {
            imcDefinicao += "seu estado atual é satisfatório, continue praticando exercícios e se alimentando corretamente";
        } else if (imc >= 25 && imc < 30) {
            imcDefinicao += "seu estado atual é de sobrepeso, recomenda-se a procura de um profissional de saúde/nutrição";
        } else if (imc >= 30 && imc < 35) {
            imcDefinicao += "seu estado atual é de obesidade grau I, recomenda-se procurar um profissional de saúde/nutrição";
        } else if (imc >= 35 && imc < 40) {
            imcDefinicao += "seu estado atual é de obesidade grau II, recomenda-se procurar um profissional de saúde/nutrição";
        } else if (imc > 40 && imc < 80) {
            imcDefinicao += "seu estado atual é obesidade grau III, recomenda-se procurar um profissional de saúde/nutrição";
        } else {
            imcDefinicao += "Valores NÃO inseridos corretamente, reinicie o app e tente novamente!!!";
            resultado = null;
        }

        resultado += imcDefinicao;

        show.setText(resultado);
    }
}