package com.example.calculoimc.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
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
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Historic extends AppCompatActivity {

    private HistoricAdapter adapter;
    private RecyclerView recyclerView;
    private LineChart chart;

    // Variáveis de controle de estado
    private int quantidadeSelecionada = 10;
    private int mesesSelecionados = 3;
    private boolean modoPeriodoAtivo = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historic);

        // 1. Lógica para gerar dados de teste (Apenas uma vez)
        if (SessaoUsuario.getInstance().estaLogado()) {
            SharedPreferences prefs = getSharedPreferences("config_teste", MODE_PRIVATE);
            boolean jaGerou = prefs.getBoolean("dados_gerados", false);

            if (!jaGerou) {
                DataBase db = new DataBase(this);
                db.gerarDadosTeste(SessaoUsuario.getInstance().getUsuarioLogado().getNome());

                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("dados_gerados", true);
                editor.apply();

                Toast.makeText(this, "Dados de teste gerados pela primeira vez!", Toast.LENGTH_SHORT).show();
            }
        }

        // 2. Configuração da Toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Meu Histórico");
        }

        // 3. Inicialização dos componentes de UI
        chart = findViewById(R.id.chartHistorico);
        recyclerView = findViewById(R.id.recyclerViewHistorico);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        MaterialButtonToggleGroup toggleGroupFiltro = findViewById(R.id.toggleGroupFiltro);
        LinearLayout containerQuantidade = findViewById(R.id.containerQuantidade);
        LinearLayout containerPeriodo = findViewById(R.id.containerPeriodo);

        ChipGroup chipGroupQtd = findViewById(R.id.chipGroupQtd);
        ChipGroup chipGroupPeriodo = findViewById(R.id.chipGroupPeriodo);

        // 4. Lógica do Toggle Group (Alternar entre Abas de Quantidade e Período)
        toggleGroupFiltro.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.btnFiltroQtd) {
                    modoPeriodoAtivo = false;
                    containerQuantidade.setVisibility(View.VISIBLE);
                    containerPeriodo.setVisibility(View.GONE);
                    atualizarListaPorQuantidade(quantidadeSelecionada);
                } else if (checkedId == R.id.btnFiltroPeriodo) {
                    modoPeriodoAtivo = true;
                    containerQuantidade.setVisibility(View.GONE);
                    containerPeriodo.setVisibility(View.VISIBLE);
                    atualizarListaPorPeriodo(mesesSelecionados);
                }
            }
        });

        // 5. Lógica dos Chips de Quantidade
        chipGroupQtd.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == View.NO_ID) return; // Evita ação caso o usuário desmarque o chip ativo

            if (checkedId == R.id.chip10) {
                quantidadeSelecionada = 10;
            } else if (checkedId == R.id.chip50) {
                quantidadeSelecionada = 50;
            } else if (checkedId == R.id.chip100) {
                quantidadeSelecionada = 100;
            }

            if (!modoPeriodoAtivo) {
                atualizarListaPorQuantidade(quantidadeSelecionada);
            }
        });

        // 6. Lógica dos Chips de Período (Novo fluxo reativo sem botão manual)
        chipGroupPeriodo.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == View.NO_ID) return;

            if (checkedId == R.id.chip3Meses) {
                mesesSelecionados = 3;
            } else if (checkedId == R.id.chip6Meses) {
                mesesSelecionados = 6;
            } else if (checkedId == R.id.chip1Ano) {
                mesesSelecionados = 12; // 1 ano = 12 meses
            }

            if (modoPeriodoAtivo) {
                atualizarListaPorPeriodo(mesesSelecionados);
            }
        });

        // 7. Carga Inicial da Tela (Padrão: 10 últimos registros)
        atualizarListaPorQuantidade(quantidadeSelecionada);
    }

    private void atualizarListaPorQuantidade(int limite) {
        if (!SessaoUsuario.getInstance().estaLogado()) return;

        DataBase db = new DataBase(this);
        String nome = SessaoUsuario.getInstance().getUsuarioLogado().getNome();

        List<UserAtributos> listaTotal = db.getRegistriesByUser(nome, 999);
        List<UserAtributos> filtrada;

        if (listaTotal.size() > limite) {
            filtrada = new ArrayList<>(listaTotal.subList(listaTotal.size() - limite, listaTotal.size()));
        } else {
            filtrada = new ArrayList<>(listaTotal);
        }

        configurarTelaComLista(filtrada);
    }

    private void atualizarListaPorPeriodo(int meses) {
        if (!SessaoUsuario.getInstance().estaLogado()) return;

        DataBase db = new DataBase(this);
        String nome = SessaoUsuario.getInstance().getUsuarioLogado().getNome();

        List<UserAtributos> lista = db.getRegistriesByPeriod(nome, meses);
        configurarTelaComLista(lista);
    }

    private void configurarTelaComLista(List<UserAtributos> lista) {
        gerarGrafico(lista);

        List<UserAtributos> listaParaRecycler = new ArrayList<>(lista);
        Collections.reverse(listaParaRecycler);

        adapter = new HistoricAdapter(listaParaRecycler, position -> {
            confirmarExclusao(position, listaParaRecycler);
        });
        recyclerView.setAdapter(adapter);
    }

    private void gerarGrafico(List<UserAtributos> listaImc) {
        if (listaImc == null || listaImc.isEmpty()) {
            chart.setNoDataText("Nenhum dado para este filtro.");
            chart.clear();
            return;
        }

        List<Entry> entradas = new ArrayList<>();
        for (int i = 0; i < listaImc.size(); i++) {
            entradas.add(new Entry(i, (float) listaImc.get(i).getImc().getIndice()));
        }

        LineDataSet dataSet = new LineDataSet(entradas, "Evolução do IMC");
        dataSet.setColor(Color.parseColor("#1976D2"));
        dataSet.setCircleColor(Color.parseColor("#1976D2"));
        dataSet.setLineWidth(3f);
        dataSet.setDrawValues(false);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        LineData lineData = new LineData(dataSet);

        // Linha de Chegada (Meta)
        UserAtributos user = SessaoUsuario.getInstance().getUsuarioLogado();
        if (user != null && user.getMetaIMC() > 0) {
            float imcMeta = (float) user.getMetaIMC();
            float maxX = Math.max(listaImc.size() + 1, 10);

            List<Entry> metaEntry = new ArrayList<>();
            metaEntry.add(new Entry(maxX - 0.5f, imcMeta));

            LineDataSet metaSet = new LineDataSet(metaEntry, "Minha Meta");
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

        addLimit(left, 18.6f, "Mínimo Ideal", "#90A4AE");
        addLimit(left, 25.0f, "Máximo Ideal", "#90A4AE");
        addLimit(left, 40.0f, "Alerta", "#EF5350");
    }

    private void addLimit(YAxis axis, float val, String label, String color) {
        LimitLine ll = new LimitLine(val, label);
        ll.setLineColor(Color.parseColor(color));
        ll.setTextColor(Color.parseColor(color));
        ll.setLineWidth(1f);
        axis.addLimitLine(ll);
    }

    private void confirmarExclusao(int pos, List<UserAtributos> listaExibida) {
        DataBase db = new DataBase(this);
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Excluir")
                .setMessage("Deseja apagar este registro?")
                .setPositiveButton("Sim", (d, w) -> {
                    db.deletarRegistro(listaExibida.get(pos).getId());

                    // Recarrega mantendo de forma limpa o estado atual do filtro
                    if (modoPeriodoAtivo) {
                        atualizarListaPorPeriodo(mesesSelecionados);
                    } else {
                        atualizarListaPorQuantidade(quantidadeSelecionada);
                    }
                })
                .setNegativeButton("Não", null).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}