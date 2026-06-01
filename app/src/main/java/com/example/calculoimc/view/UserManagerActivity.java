package com.example.calculoimc.view;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.example.calculoimc.database.UserDAO;
import com.example.calculoimc.model.SessaoUsuario;
import com.example.calculoimc.model.UserAtributos;
import com.example.calculoimc.adapter.UserAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.Locale;

public class UserManagerActivity extends AppCompatActivity implements UserAdapter.OnUserClickListener {

    private EditText editNome, editIdade, editAltura, editMetaPeso;
    private TextView textMetaIMC;
    private Button btnSalvar;
    private UserDAO userDAO;
    private RecyclerView rvUsuarios;
    private UserAdapter userAdapter;
    private LinearLayout containerFormulario;
    private FloatingActionButton fabAddUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_manager);

        editNome = findViewById(R.id.editNomeUsuario);
        editIdade = findViewById(R.id.editIdadeUsuario);
        editAltura = findViewById(R.id.editAlturaUsuario);
        editMetaPeso = findViewById(R.id.editMetaPeso);
        textMetaIMC = findViewById(R.id.textMetaIMC);
        btnSalvar = findViewById(R.id.btnSalvarUsuario);

        rvUsuarios = findViewById(R.id.rvUsuarios);
        rvUsuarios.setLayoutManager(new LinearLayoutManager(this));

        userDAO = new UserDAO(this);
        userDAO.open();

        configurarCalculoAutomaticoMeta();
        atualizarListaUsuarios();

        btnSalvar.setOnClickListener(v -> salvarUsuario());

        containerFormulario = findViewById(R.id.containerFormulario);
        fabAddUsuario = findViewById(R.id.fabAddUsuario);

        fabAddUsuario.setOnClickListener(v -> {
            alternarFormulario();
        });
    }

    private void salvarUsuario() {
        try {
            String nome = editNome.getText().toString();
            String sIdade = editIdade.getText().toString();
            String sAltura = editAltura.getText().toString().replace(",", ".");
            String sMetaPeso = editMetaPeso.getText().toString().replace(",", ".");

            if (nome.isEmpty() || sIdade.isEmpty() || sAltura.isEmpty() || sMetaPeso.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                return;
            }

            UserAtributos novoUser = new UserAtributos();
            novoUser.setNome(nome);
            novoUser.setIdade(Integer.parseInt(sIdade));
            novoUser.setAltura(Double.parseDouble(sAltura));
            novoUser.setMetaPeso(Double.parseDouble(sMetaPeso));

            if (userDAO.salvar(novoUser)) {
                Toast.makeText(this, "Usuário salvo e selecionado!", Toast.LENGTH_SHORT).show();

                // Loga o usuário recém criado
                SessaoUsuario.getInstance().setUsuarioLogado(novoUser);

                limparCampos();
                atualizarListaUsuarios();
                alternarFormulario(); // Esconde o form automaticamente após salvar
            }

        } catch (Exception e) {
            Toast.makeText(this, "Erro ao salvar", Toast.LENGTH_SHORT).show();
        }
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

        checarUsuarioLogado();
    }

    @Override
    public void onUserClick(UserAtributos usuario) {
        SessaoUsuario.getInstance().setUsuarioLogado(usuario);
        Toast.makeText(this, "Usuário " + usuario.getNome() + " selecionado!", Toast.LENGTH_SHORT).show();
        userAdapter.notifyDataSetChanged(); // Para atualizar a cor verde na lista
    }

    @Override
    public void onUserLongClick(UserAtributos usuario) {
        new AlertDialog.Builder(this)
                .setTitle("Excluir Usuário")
                .setMessage("Deseja realmente apagar " + usuario.getNome() + "?")
                .setPositiveButton("Sim", (dialog, which) -> {
                    if (userDAO.excluir(usuario.getId())) {
                        Toast.makeText(this, "Apagado com sucesso", Toast.LENGTH_SHORT).show();

                        if (SessaoUsuario.getInstance().estaLogado() &&
                                SessaoUsuario.getInstance().getUsuarioLogado().getId() == usuario.getId()) {
                            SessaoUsuario.getInstance().encerrarSessao();
                        }
                        atualizarListaUsuarios();
                    }
                })
                .setNegativeButton("Não", null)
                .show();
    }

    private void configurarCalculoAutomaticoMeta() {
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    String sAltura = editAltura.getText().toString().replace(",", ".");
                    String sMeta = editMetaPeso.getText().toString().replace(",", ".");

                    if (!sAltura.isEmpty() && !sMeta.isEmpty()) {
                        double h = Double.parseDouble(sAltura);
                        double p = Double.parseDouble(sMeta);

                        if (h > 0 && p > 0) {
                            double imcMeta = p / (h * h);
                            textMetaIMC.setText(String.format(Locale.getDefault(), "IMC Alvo: %.2f", imcMeta));
                        }
                    } else {
                        textMetaIMC.setText("IMC Alvo: -");
                    }
                } catch (Exception e) {
                    textMetaIMC.setText("IMC Alvo: -");
                }
            }
        };

        editAltura.addTextChangedListener(watcher);
        editMetaPeso.addTextChangedListener(watcher);
    }

    private void checarUsuarioLogado() {
        // Verifica se existe alguém na sessão global
        if (SessaoUsuario.getInstance().estaLogado()) {
            int idLogado = SessaoUsuario.getInstance().getUsuarioLogado().getId();

            // Avisa o adapter para encontrar esse ID e pintar de verde
            if (userAdapter != null) {
                userAdapter.marcarUsuarioComoSelecionado(idLogado);
            }
        }
    }

    private void alternarFormulario() {
        if (containerFormulario.getVisibility() == View.GONE) {
            containerFormulario.setVisibility(View.VISIBLE);
            fabAddUsuario.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
        } else {
            containerFormulario.setVisibility(View.GONE);
            fabAddUsuario.setImageResource(android.R.drawable.ic_input_add);
            limparCampos();
        }
    }

    private void limparCampos() {
        editNome.setText("");
        editIdade.setText("");
        editAltura.setText("");
        editMetaPeso.setText("");
        textMetaIMC.setText("IMC Alvo: -");
        editNome.requestFocus();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (userDAO != null) {
            userDAO.close();
        }
    }
}