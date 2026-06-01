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
import android.widget.TextView;
import android.widget.Toast;

import com.example.calculoimc.R;
import com.example.calculoimc.database.DataBase;
import com.example.calculoimc.model.SessaoUsuario;

public class MainActivity extends AppCompatActivity {

    String TAG = "erro";

    private Button bCalcular;
    private Button btnAbrirUsuarios;
    private TextView textViewBoasVindas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //DB
        DataBase dataBase = new DataBase(this);

        Button btnAbrirUsuarios = findViewById(R.id.btnAbrirUsuarios);

        btnAbrirUsuarios.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, UserManagerActivity.class);
            startActivity(intent);
        });

        Button calcular = (Button) findViewById(R.id.bCalcular);
        calcular.setOnClickListener(v -> {
            Intent intent = new Intent(this, ReceberDados.class);
            startActivity(intent);
        });

        bCalcular = findViewById(R.id.bCalcular);
        btnAbrirUsuarios = findViewById(R.id.btnAbrirUsuarios);
        textViewBoasVindas = findViewById(R.id.textView2);

        btnAbrirUsuarios.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, UserManagerActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // 1. Localiza o item do histórico pelo ID que está no seu XML
        MenuItem itemHistorico = menu.findItem(R.id.menu_historico);

        // 2. Verifica se existe um usuário logado
        boolean estaLogado = SessaoUsuario.getInstance().estaLogado();

        // 3. Define se o botão está habilitado e qual a sua opacidade visual
        if (itemHistorico != null) {
            itemHistorico.setEnabled(estaLogado);

            // Opcional: Deixa o ícone cinza/apagado quando desativado
            if (itemHistorico.getIcon() != null) {
                itemHistorico.getIcon().setAlpha(estaLogado ? 255 : 100);
            }
        }

        return super.onPrepareOptionsMenu(menu);
    }

    // Mantenha o onCreateOptionsMenu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_principal, menu);
        return true;
    }

    // DELETE THIS BLOCK
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_historico) {
            if (SessaoUsuario.getInstance().estaLogado()) {
                Intent intent = new Intent(this, Historic.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Selecione um usuário primeiro!", Toast.LENGTH_SHORT).show();
            }
            return true;
        }

        if (id == R.id.menu_usuarios) {
            // CHAMA A ATIVIDADE DE GERENCIAMENTO DE USUÁRIOS
            Intent intent = new Intent(this, UserManagerActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onResume() {
        super.onResume();

        // Força o Android a rodar o 'onPrepareOptionsMenu' e checar se o usuário logou
        invalidateOptionsMenu();

        if (SessaoUsuario.getInstance().estaLogado()) {
            String nome = SessaoUsuario.getInstance().getUsuarioLogado().getNome();
            setTitle("Olá, " + nome);
        } else {
            setTitle("Calculadora IMC (Sem usuário)");
        }
        verificarUsuarioESessao();
    }

    private void verificarUsuarioESessao() {
        if (SessaoUsuario.getInstance().estaLogado()) {
            // Se houver usuário selecionado:
            bCalcular.setEnabled(true); // Habilita o botão
            bCalcular.setAlpha(1.0f);   // Opacidade total

            String nome = SessaoUsuario.getInstance().getUsuarioLogado().getNome();
            textViewBoasVindas.setText("Olá, " + nome + "!");
        } else {
            // Se NÃO houver usuário:
            bCalcular.setEnabled(false); // Desabilita o botão
            bCalcular.setAlpha(0.5f);   // Deixa ele "apagadinho" visualmente

            textViewBoasVindas.setText("Selecione um usuário para começar");
        }
    }
}