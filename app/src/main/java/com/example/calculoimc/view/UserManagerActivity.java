package com.example.calculoimc.view;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.calculoimc.R;
import com.example.calculoimc.adapter.UserAdapter;
import com.example.calculoimc.database.UserDAO;
import com.example.calculoimc.model.SessaoUsuario;
import com.example.calculoimc.model.UserAtributos;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class UserManagerActivity extends AppCompatActivity implements UserAdapter.OnUserClickListener {

    private EditText editNome, editIdade, editAltura, editMetaPeso;
    private TextView textMetaIMC;
    private Button btnSalvar, btnExcluir;
    private LinearLayout containerFormulario;
    private FloatingActionButton fabAdd;
    private RecyclerView rvUsuarios;

    private UserAdapter userAdapter;
    private UserDAO userDAO;
    private UserAtributos usuarioEmEdicao = null; // Controle de estado

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_manager);

        inicializarComponentes();
        configurarRecyclerView();

        userDAO = new UserDAO(this);
        atualizarListaUsuarios();

        // Botão flutuante para abrir/fechar form
        fabAdd.setOnClickListener(v -> alternarFormulario());

        // Botão Salvar ou Atualizar
        btnSalvar.setOnClickListener(v -> salvarOuAtualizar());

        // Botão Excluir (Só aparece na edição)
        btnExcluir.setOnClickListener(v -> confirmarExclusao());

        configurarMonitoramentoDeDados();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Isso força o Android a chamar o onPrepareOptionsMenu novamente
        invalidateOptionsMenu();
    }

    private void inicializarComponentes() {
        editNome = findViewById(R.id.editNomeUsuario);
        editIdade = findViewById(R.id.editIdadeUsuario);
        editAltura = findViewById(R.id.editAlturaUsuario);
        editMetaPeso = findViewById(R.id.editMetaPeso);
        textMetaIMC = findViewById(R.id.textMetaIMC);
        btnSalvar = findViewById(R.id.btnSalvarUsuario);
        btnExcluir = findViewById(R.id.btnExcluirUsuario);
        containerFormulario = findViewById(R.id.containerFormulario);
        fabAdd = findViewById(R.id.fabAddUsuario);
        rvUsuarios = findViewById(R.id.rvUsuarios);
    }

    private void configurarRecyclerView() {
        rvUsuarios.setLayoutManager(new LinearLayoutManager(this));
        rvUsuarios.setHasFixedSize(true);
    }

    private void atualizarListaUsuarios() {
        List<UserAtributos> lista = userDAO.listarTodos();
        if (userAdapter == null) {
            userAdapter = new UserAdapter(lista, this);
            rvUsuarios.setAdapter(userAdapter);
        } else {
            userAdapter.setUsuarios(lista);
            userAdapter.notifyDataSetChanged();
        }

        // Mantém o verde no usuário logado
        checarUsuarioLogado();
    }

    private void configurarMonitoramentoDeDados() {
        android.text.TextWatcher watcher = new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                atualizarIMCAlvo();
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        };

        editAltura.addTextChangedListener(watcher);
        editMetaPeso.addTextChangedListener(watcher);
    }

    private void checarUsuarioLogado() {
        if (SessaoUsuario.getInstance().estaLogado()) {
            int idLogado = SessaoUsuario.getInstance().getUsuarioLogado().getId();
            userAdapter.marcarUsuarioComoSelecionado(idLogado);
        }
    }

    private void salvarOuAtualizar() {
        String nome = editNome.getText().toString();
        String idadeStr = editIdade.getText().toString();
        String alturaStr = editAltura.getText().toString();
        String metaStr = editMetaPeso.getText().toString();

        if (nome.isEmpty() || idadeStr.isEmpty() || alturaStr.isEmpty() || metaStr.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (usuarioEmEdicao == null) {
            // MODO CADASTRO
            UserAtributos novo = new UserAtributos();
            preencherDados(novo, nome, idadeStr, alturaStr, metaStr);
            if (userDAO.salvar(novo)) {
                Toast.makeText(this, "Usuário cadastrado!", Toast.LENGTH_SHORT).show();
            }
        } else {
            // MODO EDIÇÃO
            preencherDados(usuarioEmEdicao, nome, idadeStr, alturaStr, metaStr);
            if (userDAO.atualizar(usuarioEmEdicao)) {
                Toast.makeText(this, "Dados atualizados!", Toast.LENGTH_SHORT).show();
            }
        }

        limparCamposEResetar();
        atualizarListaUsuarios();
        fecharFormulario();
    }

    private void preencherDados(UserAtributos u, String n, String i, String a, String m) {
        u.setNome(n);
        u.setIdade(Integer.parseInt(i));
        u.setAltura(Double.parseDouble(a.replace(",", ".")));
        u.setMetaPeso(Double.parseDouble(m.replace(",", ".")));
        atualizarIMCAlvo();
    }

    private void atualizarIMCAlvo() {
        String alturaStr = editAltura.getText().toString().replace(",", ".");
        String metaPesoStr = editMetaPeso.getText().toString().replace(",", ".");

        if (!alturaStr.isEmpty() && !metaPesoStr.isEmpty()) {
            try {
                double altura = Double.parseDouble(alturaStr);
                double metaPeso = Double.parseDouble(metaPesoStr);

                if (altura > 0) {
                    double imcAlvo = metaPeso / (altura * altura);
                    // Atualiza o TextView formatando com 2 casas decimais
                    textMetaIMC.setText(String.format("IMC Alvo: %.2f", imcAlvo));
                } else {
                    textMetaIMC.setText("IMC Alvo: 0.00");
                }
            } catch (NumberFormatException e) {
                textMetaIMC.setText("IMC Alvo: --");
            }
        } else {
            textMetaIMC.setText("IMC Alvo: --");
        }
    }

    private void confirmarExclusao() {
        new AlertDialog.Builder(this)
                .setTitle("Excluir Usuário")
                .setMessage("Deseja realmente excluir " + usuarioEmEdicao.getNome() + "?")
                .setPositiveButton("Sim", (dialog, i) -> {
                    if (userDAO.excluir(usuarioEmEdicao.getId())) {
                        Toast.makeText(this, "Excluído!", Toast.LENGTH_SHORT).show();
                        limparCamposEResetar();
                        atualizarListaUsuarios();
                        fecharFormulario();
                    }
                })
                .setNegativeButton("Não", null)
                .show();
    }

    private void alternarFormulario() {
        if (containerFormulario.getVisibility() == View.GONE) {
            containerFormulario.setVisibility(View.VISIBLE);
            fabAdd.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
        } else {
            fecharFormulario();
        }
    }

    private void fecharFormulario() {
        containerFormulario.setVisibility(View.GONE);
        fabAdd.setImageResource(android.R.drawable.ic_input_add);
        limparCamposEResetar();
    }

    private void limparCamposEResetar() {
        editNome.setText("");
        editIdade.setText("");
        editAltura.setText("");
        editMetaPeso.setText("");
        textMetaIMC.setText("IMC Alvo: --");
        usuarioEmEdicao = null;
        btnSalvar.setText("Salvar Usuário");
        btnExcluir.setVisibility(View.GONE);
    }

    @Override
    public void onUserClick(UserAtributos usuario) {
        SessaoUsuario.getInstance().setUsuarioLogado(usuario);
        finish();
    }

    // CLIQUE LONGO: Entra em modo edição
    @Override
    public void onUserLongClick(UserAtributos usuario) {
        usuarioEmEdicao = usuario;

        editNome.setText(usuario.getNome());
        editIdade.setText(String.valueOf(usuario.getIdade()));
        editAltura.setText(String.valueOf(usuario.getAltura()));
        editMetaPeso.setText(String.valueOf(usuario.getMetaPeso()));

        btnSalvar.setText("Atualizar Dados");
        btnExcluir.setVisibility(View.VISIBLE);

        if (containerFormulario.getVisibility() == View.GONE) {
            alternarFormulario();
            atualizarIMCAlvo();
        }
    }
}