package br.una.zisc;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.List;

import br.una.projetoaplicado.marcosbenevides.zisc.R;
import br.una.zisc.entidades.Alerta;
import br.una.zisc.widget.AdapterViewHolder;

/**
 * Created by marcos.benevides on 10/11/2017.
 */

public class AlertasAdapter extends RecyclerView.Adapter {

    private List<Alerta> alertas;
    private Context context;
    SimpleDateFormat date = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    AlertasAdapter(List<Alerta> alertas, Context context) {
        this.alertas = alertas;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.card_view_row, parent, false);

        return new AdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        AdapterViewHolder viewHolder = (AdapterViewHolder) holder;
        Alerta alerta = alertas.get(position);

        viewHolder.tipoAlerta.setText(alerta.getTipo());
        String localidade = alerta.getCidade() +
                ", " + alerta.getBairro() +
                " - " + alerta.getEstado();

        viewHolder.localidade.setText(localidade);
        viewHolder.logHora.setText(date.format(alerta.getLogHora()));
        viewHolder.observacao.setText(alerta.getObservacao());


    }

    @Override
    public int getItemCount() {
        return alertas.size();
    }
}
