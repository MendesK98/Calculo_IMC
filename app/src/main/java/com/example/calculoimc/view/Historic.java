package com.example.calculoimc.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import com.example.calculoimc.R;
import com.example.calculoimc.database.DataBase;
import com.example.calculoimc.model.UserAtributos;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.YAxis;
import java.util.ArrayList;
import java.util.List;

public class Historic extends AppCompatActivity {

    private HistoricAdapter adapter; // Variável global para facilitar o refresh

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historic);

        RecyclerView recyclerView = findViewById(R.id.recyclerViewHistorico);
        DataBase db = new DataBase(this);
        List<UserAtributos> listaImc = db.getAllUsersWithIMC();

        gerarGrafico(listaImc);

        // Configuração do RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new HistoricAdapter(listaImc, position -> {
            confirmarExclusao(position, listaImc, db);
        });

        recyclerView.setAdapter(adapter);
    }

    private void confirmarExclusao(int position, List<UserAtributos> listaImc, DataBase db) {
        UserAtributos selecionado = listaImc.get(position);

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Excluir Registro")
                .setMessage("Deseja apagar este registro de IMC?")
                .setPositiveButton("Sim", (dialog, which) -> {
                    // 1. Banco
                    db.deletarRegistro(selecionado.getId());

                    // 2. Lista e Adapter
                    listaImc.remove(position);
                    adapter.notifyItemRemoved(position);
                    adapter.notifyItemRangeChanged(position, listaImc.size());

                    // 3. Gráfico
                    gerarGrafico(listaImc);

                    android.widget.Toast.makeText(this, "Excluído!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancelar", null)
                .show();
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

        // 1. Verificação de segurança: se não há dados, não gera gráfico
        if (listaImc == null || listaImc.isEmpty()) {
            chart.setNoDataText("Nenhum dado para exibir no gráfico.");
            chart.invalidate();
            return;
        }

        // Configurações do Eixo Y (Zona Verde)
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.removeAllLimitLines(); // Limpa linhas antigas para não duplicar se der refresh

        LimitLine ll1 = new LimitLine(18.6f, "Mínimo Ideal");
        ll1.setLineWidth(1f);
        ll1.setLineColor(Color.parseColor("#4CAF50")); // Verde mais nítido
        ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);

        LimitLine ll2 = new LimitLine(25.0f, "Máximo Ideal");
        ll2.setLineWidth(1f);
        ll2.setLineColor(Color.parseColor("#4CAF50")); // Verde mais nítido superior
        ll2.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);

        LimitLine ll3 = new LimitLine(40.0f, "Limite Alerta");
        ll3.setLineWidth(1f);
        ll3.setLineColor(Color.parseColor("#F44336")); // Vermelho para o alerta superior
        ll3.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);

        leftAxis.addLimitLine(ll1);
        leftAxis.addLimitLine(ll2);
        leftAxis.addLimitLine(ll3);
        leftAxis.setDrawLimitLinesBehindData(true);

        // Deixa o gráfico respirar: define limites fixos para o eixo Y
        leftAxis.setAxisMinimum(10f);
        leftAxis.setAxisMaximum(45f);



        // Eixo Direita e X: Limpeza visual
        chart.getAxisRight().setEnabled(false);
        chart.getXAxis().setDrawGridLines(false);
        chart.getXAxis().setGranularity(1f); // Evita números repetidos no eixo X

        // 2. Preenchimento dos dados
        List<Entry> entradas = new ArrayList<>();
        for (int i = 0; i < listaImc.size(); i++) {
            float imcValor = (float) listaImc.get(i).getImc().getIndice();
            entradas.add(new Entry(i, imcValor));
        }

        // 3. Configuração visual da linha
        LineDataSet dataSet = new LineDataSet(entradas, "Evolução do IMC");
        dataSet.setColor(Color.parseColor("#2196F3"));
        dataSet.setCircleColor(Color.parseColor("#1976D2"));
        dataSet.setLineWidth(3f);
        dataSet.setCircleRadius(5f);
        dataSet.setDrawCircleHole(true);
        dataSet.setValueTextSize(10f);

        // Estilo da área preenchida
        dataSet.setDrawFilled(true);
        dataSet.setFillAlpha(40);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);

        // Perfumaria final
        chart.getDescription().setEnabled(false);
        chart.setTouchEnabled(true);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setPinchZoom(true);
        chart.animateXY(1000, 1000);
        chart.invalidate();
    }

}