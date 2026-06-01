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
                // 1. Busca o usuário que está logado na sessão atual
                UserAtributos usuarioLogado = com.example.calculoimc.model.SessaoUsuario.getInstance().getUsuarioLogado();

                if (usuarioLogado != null) {
                    UserAtributos registroParaSalvar = new UserAtributos();

                    registroParaSalvar.setNome(usuarioLogado.getNome());
                    registroParaSalvar.setIdade(usuarioLogado.getIdade());

                    registroParaSalvar.setImc(imc);

                    boolean sucesso = db.addIMC(registroParaSalvar);

                    if (sucesso) {
                        Toast.makeText(Resultados.this, "Resultado salvo para " + usuarioLogado.getNome(), Toast.LENGTH_SHORT).show();
                        save.setEnabled(false);
                    } else {
                        Toast.makeText(Resultados.this, "Erro ao salvar no banco de dados.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Resultados.this, "Erro: Nenhum usuário selecionado!", Toast.LENGTH_LONG).show();
                }
            }
        });



    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            android.content.Intent intent = new android.content.Intent(this, com.example.calculoimc.view.ReceberDados.class);

            intent.addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP | android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP);

            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}