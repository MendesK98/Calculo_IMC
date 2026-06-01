package com.example.calculoimc.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import com.example.calculoimc.R;
import com.example.calculoimc.adapter.HistoricAdapter;
import com.example.calculoimc.database.DataBase;
import com.example.calculoimc.model.SessaoUsuario;
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

        String nomeLogado = SessaoUsuario.getInstance().getUsuarioLogado().getNome();
        List<UserAtributos> listaImc = db.getRegistriesByUser(nomeLogado);

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

        if (listaImc == null || listaImc.isEmpty()) {
            chart.setNoDataText("Nenhum dado para exibir.");
            chart.invalidate();
            return;
        }

        UserAtributos usuarioLogado = SessaoUsuario.getInstance().getUsuarioLogado();

        // 1. DATASET DO HISTÓRICO
        List<Entry> entradasHistorico = new ArrayList<>();
        for (int i = 0; i < listaImc.size(); i++) {
            float imcValor = (float) listaImc.get(i).getImc().getIndice();
            entradasHistorico.add(new Entry(i, imcValor));
        }

        LineDataSet dataSetHistorico = new LineDataSet(entradasHistorico, "Meu Progresso");
        dataSetHistorico.setColor(Color.parseColor("#1976D2"));
        dataSetHistorico.setCircleColor(Color.parseColor("#1976D2"));
        dataSetHistorico.setLineWidth(3f);
        dataSetHistorico.setCircleRadius(4f);
        dataSetHistorico.setDrawCircleHole(false);
        dataSetHistorico.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        // 2. DATASET DA META (DESTINO FIXO À DIREITA)
        List<Entry> entradaMeta = new ArrayList<>();
        float imcMeta = (float) (usuarioLogado != null ? usuarioLogado.getMetaIMC() : 0);

        LineData lineData = new LineData(dataSetHistorico);

        if (imcMeta > 0) {
            // FIXANDO A DIREITA:
            // Definimos um valor fixo de "espaço" no gráfico.
            // Se o histórico for pequeno, a meta fica longe.
            // Se o histórico crescer, a meta continua na borda direita.
            float limiteMaximoX = Math.max(listaImc.size() + 2, 10); // Garante um mínimo de 10 espaços
            float posicaoMeta = limiteMaximoX - 0.5f; // Quase no fim do eixo

            entradaMeta.add(new Entry(posicaoMeta, imcMeta));

            LineDataSet dataSetMeta = new LineDataSet(entradaMeta, "Linha de Chegada");
            dataSetMeta.setCircleColor(Color.parseColor("#4CAF50"));
            dataSetMeta.setCircleRadius(9f); // Ponto grande para destaque
            dataSetMeta.setCircleHoleRadius(5f);
            dataSetMeta.setCircleHoleColor(Color.WHITE);
            dataSetMeta.setDrawCircleHole(true);
            dataSetMeta.setLineWidth(0f);

            // Texto indicando a meta
            dataSetMeta.setDrawValues(true);
            dataSetMeta.setValueTextSize(11f);
            dataSetMeta.setValueTextColor(Color.parseColor("#4CAF50"));

            lineData.addDataSet(dataSetMeta);

            // Ajuste do Eixo X para fixar a borda
            chart.getXAxis().setAxisMaximum(limiteMaximoX);
        }

        chart.setData(lineData);

        // --- CONFIGURAÇÕES DE EIXO ---
        chart.getXAxis().setAxisMinimum(-0.5f);
        chart.getXAxis().setDrawGridLines(false);
        chart.getXAxis().setDrawLabels(false);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setAxisMinimum(10f);
        leftAxis.setAxisMaximum(45f);
        leftAxis.setDrawGridLines(true);
        leftAxis.setGridColor(Color.LTGRAY);

        chart.getAxisRight().setEnabled(false);
        chart.getDescription().setEnabled(false);

        // Aumenta o recuo à direita para o ponto não ser cortado
        chart.setExtraRightOffset(50f);

        chart.animateX(1000);
        chart.invalidate();
    }

}