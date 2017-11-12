package br.una.zisc;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.List;

import br.una.projetoaplicado.marcosbenevides.zisc.R;
import br.una.zisc.entidades.Alerta;
import br.una.zisc.requisicoesWS.RetrofitService;
import br.una.zisc.requisicoesWS.ServiceGenerator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CardViewActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Integer idUsuario, contFalha = 0;
    private ProgressDialog dialog;
    private AlertDialog.Builder alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_view);

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            idUsuario = bundle.getInt("ID");
        }
        buscaAlertas();

    }

    public void buscaAlertas() {

        callProgressDialog("Por favor aguarde", "Buscando seus alertas!");

        new Thread(new Runnable() {
            @Override
            public void run() {

                RetrofitService service = ServiceGenerator.createService(RetrofitService.class);
                Call<List<Alerta>> alertas = service.consultaAlertaUsuario(idUsuario);

                alertas.enqueue(new Callback<List<Alerta>>() {
                    @Override
                    public void onResponse(Call<List<Alerta>> call, Response<List<Alerta>> response) {
                        if (!response.isSuccessful()) {
                            contFalha++;
                            if (contFalha >= 3) {

                                callDialog(1, response.code() + " - " + response.message());
                            } else {
                                buscaAlertas();
                            }
                        } else if (response.body() != null) {
                            contFalha = 0;

                            if (dialog != null) {
                                dialog.dismiss();
                            }
                            List<Alerta> lista = response.body();
                            recyclerView.setAdapter(new AlertasAdapter(lista,CardViewActivity.this));

                        } else {
                            callDialog(2, "");
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Alerta>> call, Throwable t) {
                        callDialog(1, t.getMessage() + " - " + t.getCause());
                    }
                });

            }
        }).start();
    }

    public void callDialog(final Integer tipoErro, final String response) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String title;
                String msg;
                if (tipoErro == 1) {
                    title = getResources().getString(R.string.ERRO_AO_CONECTAR);
                    msg = response;
                } else if (tipoErro == 2) {
                    title = "OPS!";
                    msg = getResources().getString(R.string.SEM_ALERTAS);
                } else if (tipoErro == 3) {
                    title = "OPS!";
                    msg = response;
                } else {
                    title = getResources().getString(R.string.ERRO_AO_CONECTAR);
                    msg = response;
                }
                if (dialog != null) {
                    dialog.dismiss();
                }
                alertDialog = new AlertDialog.Builder(CardViewActivity.this)
                        .setTitle(title)
                        .setMessage(msg)
                        .setCancelable(true)
                        .setPositiveButton("OK", null);
                alertDialog.create();
                alertDialog.show();
            }
        });
    }


    public void callProgressDialog(final String title, final String message) {
        if (dialog != null) {
            dialog.dismiss();
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                CardViewActivity.this.dialog = ProgressDialog.show(CardViewActivity.this, title, message);
            }
        });
    }
}
