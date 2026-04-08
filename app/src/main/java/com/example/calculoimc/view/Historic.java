package com.example.calculoimc.view;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.example.calculoimc.R;
import com.example.calculoimc.database.DataBase;
import com.example.calculoimc.model.UserAtributos;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import java.util.ArrayList;

import java.util.List;

public class Historic extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historic);

        ListView listView = findViewById(R.id.listViewHistorico); // ID do seu ListView no XML
        DataBase db = new DataBase(this);

        // Busca a lista de IMCs do banco
        List<UserAtributos> listaImc = db.getAllUsersWithIMC();


        gerarGrafico(listaImc);

        // Cria um adaptador simples (usa o toString() da sua classe IMC)
        ArrayAdapter<UserAtributos> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                listaImc
        );

        listView.setAdapter(adapter);
        }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            android.content.Intent intent = new android.content.Intent(this, com.example.calculoimc.view.MainActivity.class);

            intent.addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP | android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP);

            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void gerarGrafico(List<UserAtributos> listaImc) {
        LineChart chart = findViewById(R.id.chartHistorico);
        List<Entry> entradas = new ArrayList<>();

// Preenchemos os dados do gráfico
        for (int i = 0; i < listaImc.size(); i++) {
            float imcValor = (float) listaImc.get(i).getImc().getIndice();
            entradas.add(new Entry(i, imcValor));
        }

// Configuração visual do gráfico
        LineDataSet dataSet = new LineDataSet(entradas, "Evolução do IMC");
        dataSet.setColor(Color.BLUE);
        dataSet.setCircleColor(Color.DKGRAY);
        dataSet.setLineWidth(2f);
        dataSet.setValueTextSize(10f);

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.getDescription().setText("Seu progresso");
        chart.animateX(1000); // Animação de entrada
        chart.invalidate(); // Refresh
    }

}