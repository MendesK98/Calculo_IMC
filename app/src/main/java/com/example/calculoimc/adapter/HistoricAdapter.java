package com.example.calculoimc.adapter;

import android.graphics.Color; // Importe o Color correto
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.calculoimc.R;
import com.example.calculoimc.model.UserAtributos;
import java.util.List;

// Alterado para usar HistoricAdapter.ViewHolder
public class HistoricAdapter extends RecyclerView.Adapter<HistoricAdapter.ViewHolder> {

    private List<UserAtributos> lista;
    private OnItemLongClickListener longClickListener;

    public interface OnItemLongClickListener {
        void onItemLongClick(int position);
    }

    public HistoricAdapter(List<UserAtributos> lista, OnItemLongClickListener listener) {
        this.lista = lista;
        this.longClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_historico, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserAtributos atual = lista.get(position);

        // 1. Configura os textos básicos
        holder.txtDescricao.setText("IMC: " + String.format("%.1f", atual.getImc().getIndice()) + " (" + atual.getClassificacao() + ")");
        holder.txtData.setText("Registrado em: " + atual.getImc().getData());

        // 2. Lógica da Variação de Peso
        if (position < lista.size() - 1) {
            UserAtributos anterior = lista.get(position + 1);
            double diff = atual.getImc().getPeso() - anterior.getImc().getPeso();

            if (diff > 0) {
                // Ganhou peso (Vermelho)
                holder.txtVariacao.setText(String.format("+%.1f kg", diff));
                holder.txtVariacao.setTextColor(Color.parseColor("#EF5350"));
                holder.txtVariacao.setBackgroundColor(Color.parseColor("#15EF5350"));
            } else if (diff < 0) {
                // Perdeu peso (Verde)
                holder.txtVariacao.setText(String.format("%.1f kg", diff));
                holder.txtVariacao.setTextColor(Color.parseColor("#4CAF50"));
                holder.txtVariacao.setBackgroundColor(Color.parseColor("#154CAF50"));
            } else {
                // Peso igual (Cinza)
                holder.txtVariacao.setText("-");
                holder.txtVariacao.setTextColor(Color.GRAY);
                holder.txtVariacao.setBackgroundColor(Color.parseColor("#15808080"));
            }
            holder.txtVariacao.setVisibility(View.VISIBLE);
        } else {
            // Primeiro registro não tem comparação
            holder.txtVariacao.setVisibility(View.GONE);
        }

        // 3. Lógica da Cor Lateral
        int corStatus = getColorByImc(atual.getImc().getIndice());
        holder.viewStatusIndicator.setBackgroundColor(corStatus);

        // 4. Clique longo para deletar
        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onItemLongClick(position);
            }
            return true;
        });
    }

    private int getColorByImc(double imc) {
        if (imc < 18.5) return Color.parseColor("#FFB74D");
        if (imc < 25) return Color.parseColor("#4CAF50");
        if (imc < 30) return Color.parseColor("#FFD54F");
        return Color.parseColor("#EF5350");
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    // ViewHolder atualizado com todos os IDs do seu item_historico.xml
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtDescricao, txtData, txtVariacao;
        View viewStatusIndicator;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtDescricao = itemView.findViewById(R.id.txtDescricao);
            txtData = itemView.findViewById(R.id.txtData);
            txtVariacao = itemView.findViewById(R.id.txtVariacao);
            viewStatusIndicator = itemView.findViewById(R.id.viewStatusIndicator);
        }
    }
}