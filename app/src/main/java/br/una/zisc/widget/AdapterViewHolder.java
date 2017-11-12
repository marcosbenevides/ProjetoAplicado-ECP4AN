package br.una.zisc.widget;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import br.una.projetoaplicado.marcosbenevides.zisc.R;

/**
 * Created by Marcos Benevides on 12/11/2017.
 */

public class AdapterViewHolder extends RecyclerView.ViewHolder {

    final public TextView tipoAlerta, localidade, logHora, observacao;

    public AdapterViewHolder(View itemView) {
        super(itemView);

        tipoAlerta = (TextView) itemView.findViewById(R.id.tipoAlertaCard);
        localidade = (TextView) itemView.findViewById(R.id.localidadeCard);
        logHora = (TextView) itemView.findViewById(R.id.loghoraCard);
        observacao = (TextView) itemView.findViewById(R.id.obsAlertaCard);

    }
}
