package com.example.calculoimc.adapter;

import android.graphics.Color; // CORREÇÃO: Import correto para cores
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.calculoimc.R;
import com.example.calculoimc.model.UserAtributos;

import java.util.List;
import java.util.Locale;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<UserAtributos> listaUsuarios;
    private int posicaoSelecionada = -1;
    private OnUserClickListener listener;

    // Interface para cliques
    public interface OnUserClickListener {
        void onUserClick(UserAtributos usuario);
        void onUserLongClick(UserAtributos usuario);
    }

    // Construtor principal
    public UserAdapter(List<UserAtributos> lista, OnUserClickListener listener) {
        this.listaUsuarios = lista;
        this.listener = listener;
    }

    public void setUsuarios(List<UserAtributos> lista) {
        this.listaUsuarios = lista;
    }

    public void marcarUsuarioComoSelecionado(int usuarioId) {
        if (listaUsuarios == null) return;

        for (int i = 0; i < listaUsuarios.size(); i++) {
            if (listaUsuarios.get(i).getId() == usuarioId) {
                this.posicaoSelecionada = i;
                notifyDataSetChanged();
                break;
            }
        }
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_usuario, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        UserAtributos user = listaUsuarios.get(position);

        // 1. Definir textos
        holder.txtNome.setText(user.getNome());

        String detalhes = String.format(Locale.getDefault(),
                "Idade: %d | Altura: %.2fm | Meta: %.1fkg (IMC: %.1f)",
                user.getIdade(), user.getAltura(), user.getMetaPeso(), user.getMetaIMC());
        holder.txtDetalhes.setText(detalhes);

        // 2. Lógica da Cor de Seleção (Verde se selecionado, Branco se não)
        if (posicaoSelecionada == position) {
            holder.layoutInterno.setBackgroundColor(Color.parseColor("#C8E6C9")); // Verde claro
        } else {
            holder.layoutInterno.setBackgroundColor(Color.WHITE);
        }

        // 3. Clique Simples
        holder.itemView.setOnClickListener(v -> {
            posicaoSelecionada = holder.getAdapterPosition();
            notifyDataSetChanged(); // Atualiza a lista para pintar o selecionado
            if (listener != null) {
                listener.onUserClick(user);
            }
        });

        // 4. Clique Longo
        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onUserLongClick(user);
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return listaUsuarios != null ? listaUsuarios.size() : 0;
    }

    // ViewHolder Único e Corrigido
    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView txtNome, txtDetalhes;
        LinearLayout layoutInterno;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNome = itemView.findViewById(R.id.txtNomeItem);
            txtDetalhes = itemView.findViewById(R.id.txtDetalhesItem);
            layoutInterno = itemView.findViewById(R.id.layoutItem);
        }
    }
}