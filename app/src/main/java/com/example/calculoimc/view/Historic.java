package com.example.calculoimc.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
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
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Historic extends AppCompatActivity {

    private HistoricAdapter adapter;
    private RecyclerView recyclerView;
    private int filtroAtual = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historic);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Meu Histórico");
        }

        recyclerView = findViewById(R.id.recyclerViewHistorico);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Lógica dos Chips (Filtro visível)
        ChipGroup chipGroup = findViewById(R.id.chipGroupFiltro);
        chipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.chip10) {
                atualizarListaComFiltro(10);
            } else if (checkedId == R.id.chip50) {
                atualizarListaComFiltro(50);
            } else if (checkedId == R.id.chipTudo) {
                atualizarListaComFiltro(100);
            }
        });

        atualizarListaComFiltro(filtroAtual);
    }

    private void atualizarListaComFiltro(int limite) {
        this.filtroAtual = limite;
        DataBase db = new DataBase(this);
        if (!SessaoUsuario.getInstance().estaLogado()) return;

        String nome = SessaoUsuario.getInstance().getUsuarioLogado().getNome();
        // Busca todos e filtramos aqui para garantir a ordem do gráfico
        List<UserAtributos> listaCompleta = db.getRegistriesByUser(nome, 999);

        List<UserAtributos> listaFiltrada;
        if (listaCompleta.size() > limite) {
            listaFiltrada = new ArrayList<>(listaCompleta.subList(listaCompleta.size() - limite, listaCompleta.size()));
        } else {
            listaFiltrada = new ArrayList<>(listaCompleta);
        }

        // Gráfico (Ordem cronológica)
        gerarGrafico(listaFiltrada);

        // RecyclerView (Mais novo primeiro)
        List<UserAtributos> listaRecycler = new ArrayList<>(listaFiltrada);
        Collections.reverse(listaRecycler);

        adapter = new HistoricAdapter(listaRecycler, position -> {
            confirmarExclusao(position, listaRecycler, db);
        });
        recyclerView.setAdapter(adapter);
    }

    private void gerarGrafico(List<UserAtributos> listaImc) {
        LineChart chart = findViewById(R.id.chartHistorico);
        if (listaImc == null || listaImc.isEmpty()) {
            chart.setNoDataText("Nenhum dado.");
            chart.clear();
            return;
        }

        List<Entry> entradas = new ArrayList<>();
        for (int i = 0; i < listaImc.size(); i++) {
            entradas.add(new Entry(i, (float) listaImc.get(i).getImc().getIndice()));
        }

        LineDataSet dataSet = new LineDataSet(entradas, "IMC");
        dataSet.setColor(Color.parseColor("#1976D2"));
        dataSet.setCircleColor(Color.parseColor("#1976D2"));
        dataSet.setLineWidth(3f);
        dataSet.setDrawValues(false);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        LineData lineData = new LineData(dataSet);

        // Meta (Ponto Verde à Direita)
        UserAtributos user = SessaoUsuario.getInstance().getUsuarioLogado();
        if (user != null && user.getMetaIMC() > 0) {
            float imcMeta = (float) user.getMetaIMC();
            float maxX = Math.max(listaImc.size() + 1, 8);

            List<Entry> metaEntry = new ArrayList<>();
            metaEntry.add(new Entry(maxX - 0.5f, imcMeta));

            LineDataSet metaSet = new LineDataSet(metaEntry, "Objetivo");
            metaSet.setCircleColor(Color.parseColor("#4CAF50"));
            metaSet.setCircleRadius(8f);
            metaSet.setDrawCircleHole(true);
            metaSet.setCircleHoleColor(Color.WHITE);
            metaSet.setLineWidth(0f);
            metaSet.setValueTextColor(Color.parseColor("#4CAF50"));

            lineData.addDataSet(metaSet);
            chart.getXAxis().setAxisMaximum(maxX);
        }

        chart.setData(lineData);
        configurarEixos(chart);
        chart.invalidate();
    }

    private void configurarEixos(LineChart chart) {
        chart.getXAxis().setEnabled(false);
        chart.getAxisRight().setEnabled(false);
        chart.getDescription().setEnabled(false);

        YAxis left = chart.getAxisLeft();
        left.removeAllLimitLines();
        left.setAxisMinimum(10f);
        left.setAxisMaximum(45f);

        addLimit(left, 18.6f, "Mínimo", "#90A4AE");
        addLimit(left, 25.0f, "Máximo", "#90A4AE");
        addLimit(left, 40.0f, "Alerta", "#EF5350");
    }

    private void addLimit(YAxis axis, float val, String label, String color) {
        LimitLine ll = new LimitLine(val, label);
        ll.setLineColor(Color.parseColor(color));
        ll.setTextColor(Color.parseColor(color));
        ll.setLineWidth(1f);
        axis.addLimitLine(ll);
    }

    private void confirmarExclusao(int pos, List<UserAtributos> lista, DataBase db) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Excluir")
                .setMessage("Deseja apagar?")
                .setPositiveButton("Sim", (d, w) -> {
                    db.deletarRegistro(lista.get(pos).getId());
                    atualizarListaComFiltro(filtroAtual);
                })
                .setNegativeButton("Não", null).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) { finish(); return true; }
        return super.onOptionsItemSelected(item);
    }
}