package com.example.calculoimc.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
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

public class ReceberDados extends AppCompatActivity {

    private TextInputEditText peso, altura;
    private MaterialButtonToggleGroup toggleGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.receber_dados);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        peso = findViewById(R.id.tPeso);
        altura = findViewById(R.id.tAltura);
        toggleGroup = findViewById(R.id.toggleGroup);
        Button calcular = findViewById(R.id.btCalcular);

        configurarFiltros(false);
        inicializarToggle();

        calcular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                executarCalculo();
            }
        });
    }

    private void executarCalculo() {
        String sAltura = altura.getText().toString().trim();
        String sPeso = peso.getText().toString().trim();

        if (sAltura.isEmpty() || sPeso.isEmpty()) {
            if (sAltura.isEmpty()) altura.setError("Informe a altura");
            if (sPeso.isEmpty()) peso.setError("Informe o peso");
            return;
        }

        sAltura = sAltura.replace(',', '.');
        sPeso = sPeso.replace(',', '.');

        MetodosGerais mtg = new MetodosGerais();
        Double dPeso = mtg.ConverStringToDouble(sPeso);
        Double dAltura = mtg.ConverStringToDouble(sAltura);

        // CONVERSÃO: Só converte se o sistema Imperial estiver marcado
        if (toggleGroup.getCheckedButtonId() == R.id.btnImperial) {
            dPeso = dPeso * 0.453592; // lb -> kg
            dAltura = dAltura * 0.0254; // in -> m
        }

        Intent intentResultados = new Intent(ReceberDados.this, Resultados.class);
        intentResultados.putExtra("peso", dPeso);
        intentResultados.putExtra("altura", dAltura);
        startActivity(intentResultados);
    }

    private void inicializarToggle() {
        Button btnMetrico = findViewById(R.id.btnMetrico);
        Button btnImperial = findViewById(R.id.btnImperial);

        // Define cores iniciais
        atualizarEstiloBotao(btnMetrico, true);
        atualizarEstiloBotao(btnImperial, false);

        toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            Button btn = findViewById(checkedId);
            if (btn != null) {
                atualizarEstiloBotao(btn, isChecked);
                if (isChecked) {
                    configurarFiltros(checkedId == R.id.btnImperial);
                    // Limpa os campos ao trocar para evitar confusão de unidades
                    peso.setText("");
                    altura.setText("");
                }
            }
        });
    }

    private void configurarFiltros(boolean isImperial) {
        if (isImperial) {
            peso.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(3, 2)});
            altura.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(2, 2)});
            peso.setHint("Peso (libras)");
            altura.setHint("Altura (polegadas)");
        } else {
            peso.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(3, 2)});
            altura.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(1, 2)});
            peso.setHint("Peso (kg)");
            altura.setHint("Altura (ex: 1.75)");
        }
    }

    private void atualizarEstiloBotao(Button btn, boolean selecionado) {
        if (selecionado) {
            btn.setBackgroundTintList(ColorStateList.valueOf(Color.BLUE));
            btn.setTextColor(Color.WHITE);
            btn.setAlpha(1.0f);
        } else {
            btn.setBackgroundTintList(ColorStateList.valueOf(Color.LTGRAY));
            btn.setTextColor(Color.DKGRAY);
            btn.setAlpha(0.6f);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}