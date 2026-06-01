package com.example.calculoimc.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.materialswitch.MaterialSwitch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Historic extends AppCompatActivity {

    private HistoricAdapter adapter;
    private RecyclerView recyclerView;

    // Variáveis de controle de estado
    private int quantidadeSelecionada = 10;
    private boolean modoPeriodoAtivo = false;
    private String tituloSwitch= "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Função utilizada para gerar dados para teste.
//        if (SessaoUsuario.getInstance().estaLogado()) {
//            DataBase db = new DataBase(this);
//            db.gerarDadosTeste(SessaoUsuario.getInstance().getUsuarioLogado().getNome());
//            Toast.makeText(this, "Dados de teste gerados!", Toast.LENGTH_SHORT).show();
//        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historic);

        // Configuração da Toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Meu Histórico");
        }

        // 1. Inicialização dos componentes de UI
        recyclerView = findViewById(R.id.recyclerViewHistorico);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        MaterialSwitch switchTipoFiltro = findViewById(R.id.switchTipoFiltro);
        LinearLayout containerQuantidade = findViewById(R.id.containerQuantidade);
        LinearLayout containerPeriodo = findViewById(R.id.containerPeriodo);

        ChipGroup chipGroup = findViewById(R.id.chipGroupFiltro);
        EditText editMeses = findViewById(R.id.editMeses);
        Button btnAplicarPeriodo = findViewById(R.id.btnAplicarPeriodo);

        // 2. Lógica do Interruptor (Alternar entre modo Quantidade e modo Período)
        switchTipoFiltro.setOnCheckedChangeListener((buttonView, isChecked) -> {
            modoPeriodoAtivo = isChecked;

            if (isChecked) {
                // Ativou modo PERÍODO
                containerQuantidade.setVisibility(View.GONE);
                containerPeriodo.setVisibility(View.VISIBLE);

                tituloSwitch = "Filtrar por Período";

                // Executa o filtro baseado no valor que já está no EditText
                String mesesStr = editMeses.getText().toString();
                processarFiltroPorPeriodo(mesesStr);
            } else {
                // Ativou modo QUANTIDADE
                containerQuantidade.setVisibility(View.VISIBLE);
                containerPeriodo.setVisibility(View.GONE);

                tituloSwitch = "Filtrar por Quantidade";

                // Retorna para a última quantidade selecionada nos chips
                atualizarListaPorQuantidade(quantidadeSelecionada);
            }
            buttonView.setText(tituloSwitch);
        });

        // 3. Lógica dos Chips (Selecionar 10, 50 ou 100)
        chipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.chip10) {
                quantidadeSelecionada = 10;
            } else if (checkedId == R.id.chip50) {
                quantidadeSelecionada = 50;
            } else if (checkedId == R.id.chip100) {
                quantidadeSelecionada = 100;
            }

            // Só atualiza se o modo período não estiver bloqueando a visão
            if (!modoPeriodoAtivo) {
                atualizarListaPorQuantidade(quantidadeSelecionada);
            }
        });

        // 4. Lógica do Botão Aplicar (Filtrar por meses digitados)
        btnAplicarPeriodo.setOnClickListener(v -> {
            processarFiltroPorPeriodo(editMeses.getText().toString());
        });

        // 5. Carga Inicial: Modo Quantidade (10 registros)
        atualizarListaPorQuantidade(quantidadeSelecionada);
    }

    private void processarFiltroPorPeriodo(String valor) {
        if (!valor.isEmpty()) {
            try {
                int meses = Integer.parseInt(valor);
                atualizarListaPorPeriodo(meses);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Digite um número válido", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void atualizarListaPorQuantidade(int limite) {
        if (!SessaoUsuario.getInstance().estaLogado()) return;

        DataBase db = new DataBase(this);
        String nome = SessaoUsuario.getInstance().getUsuarioLogado().getNome();

        // Busca registros (pegamos um número grande para garantir que o subList funcione)
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

        // Usa a consulta SQL por período (meses retroativos)
        List<UserAtributos> lista = db.getRegistriesByPeriod(nome, meses);

        configurarTelaComLista(lista);
    }

    private void configurarTelaComLista(List<UserAtributos> lista) {
        // Gráfico recebe a lista na ordem cronológica (ASC)
        gerarGrafico(lista);

        // RecyclerView recebe a lista invertida (Mais novos no topo)
        List<UserAtributos> listaParaRecycler = new ArrayList<>(lista);
        Collections.reverse(listaParaRecycler);

        adapter = new HistoricAdapter(listaParaRecycler, position -> {
            confirmarExclusao(position, listaParaRecycler);
        });
        recyclerView.setAdapter(adapter);
    }

    private void gerarGrafico(List<UserAtributos> listaImc) {
        LineChart chart = findViewById(R.id.chartHistorico);
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

        // Linha de Chegada (Ponto Verde fixo à direita)
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

        // Linhas de Referência
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

                    // Recarrega mantendo o modo que o usuário estava usando
                    if (modoPeriodoAtivo) {
                        EditText editMeses = findViewById(R.id.editMeses);
                        processarFiltroPorPeriodo(editMeses.getText().toString());
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