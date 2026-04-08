package com.example.calculoimc.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.calculoimc.R;
import com.example.calculoimc.model.DecimalDigitsInputFilter;
import com.example.calculoimc.model.MetodosGerais;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.textfield.TextInputEditText;
import android.content.res.ColorStateList;
import android.graphics.Color;

public class ReceberDados extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.receber_dados);

        // Ativa a setinha de voltar na barra superior
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Referências dos campos
        TextInputEditText peso = findViewById(R.id.tPeso);
        TextInputEditText altura = findViewById(R.id.tAltura);
        MaterialButtonToggleGroup toggleGroup = findViewById(R.id.toggleGroup);
        Button calcular = findViewById(R.id.btCalcular);

        // Aplicando os filtros de decimais
        // Peso: até 3 dígitos antes do ponto (ex: 150.55)
        peso.setFilters(new InputFilter[] { new DecimalDigitsInputFilter(3, 2) });
        // Altura: até 1 dígito antes do ponto (ex: 1.75) - Ajuste para 3 se quiser usar cm no imperial
        altura.setFilters(new InputFilter[] { new DecimalDigitsInputFilter(1, 2) });

        calcular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sAltura = altura.getText().toString().trim();
                String sPeso = peso.getText().toString().trim();

                // Validação de campos vazios
                if (sAltura.isEmpty() || sPeso.isEmpty()) {
                    if(sAltura.isEmpty()) altura.setError("Informe a altura");
                    if(sPeso.isEmpty()) peso.setError("Informe o peso");
                    return;
                }

                // Normalização para Double (troca vírgula por ponto)
                sAltura = sAltura.replace(',', '.');
                sPeso = sPeso.replace(',', '.');

                MetodosGerais mtg = new MetodosGerais();
                Double dPeso = mtg.ConverStringToDouble(sPeso);
                Double dAltura = mtg.ConverStringToDouble(sAltura);

                // LÓGICA DA OPÇÃO A: Conversão Global
                // Se o botão selecionado for o Imperial, convertemos para o padrão do banco (Métrico)
                if (toggleGroup.getCheckedButtonId() == R.id.btnImperial) {
                    dPeso = dPeso * 0.453592; // Libras para Kg
                    dAltura = dAltura * 0.0254; // Polegadas para Metros
                }

                // Navegação para a tela de resultados
                Intent intentResultados = new Intent(ReceberDados.this, Resultados.class);
                intentResultados.putExtra("peso", dPeso);
                intentResultados.putExtra("altura", dAltura);

                startActivity(intentResultados);
                // Não usamos finish() aqui para permitir que o usuário volte e corrija os dados
            }
        });

        toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            Button btn = findViewById(checkedId);
            if (btn != null) {
                atualizarEstiloBotao(btn, isChecked);
            }
        });
        Button btnMetrico = findViewById(R.id.btnMetrico);
        Button btnImperial = findViewById(R.id.btnImperial);

        atualizarEstiloBotao(btnMetrico, true);
        atualizarEstiloBotao(btnImperial, false);
    }

    // Gerencia o clique na setinha de voltar da barra de título
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Volta para a MainActivity limpando a pilha
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void atualizarEstiloBotao(Button btn, boolean selecionado) {
        if (selecionado) {
            // Cor quando ativo (ex: Azul)
            btn.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.BLUE));
            btn.setTextColor(android.graphics.Color.WHITE);
            btn.setAlpha(1.0f);
        } else {
            // Cor quando apagado (ex: Cinza)
            btn.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.LTGRAY));
            btn.setTextColor(android.graphics.Color.DKGRAY);
            btn.setAlpha(0.5f);
        }
    }
}