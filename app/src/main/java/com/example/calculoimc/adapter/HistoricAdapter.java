package com.example.calculoimc.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.calculoimc.R;
import com.example.calculoimc.model.UserAtributos;
import java.util.List;

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
        UserAtributos user = lista.get(position);

        holder.txtDescricao.setText("IMC: " + user.getImc().getIndice() + " - Data: " + user.getImc().getData());

        holder.itemView.setOnLongClickListener(v -> {
            longClickListener.onItemLongClick(position);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtDescricao;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtDescricao = itemView.findViewById(R.id.txtDescricao);
        }
    }
}