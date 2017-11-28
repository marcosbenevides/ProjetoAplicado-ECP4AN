package br.una.zisc;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.gson.Gson;

import java.util.List;

import br.una.projetoaplicado.marcosbenevides.zisc.R;
import br.una.zisc.entidades.Alerta;
import br.una.zisc.entidades.Usuario;
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
    private Toolbar actionBar;
    private Usuario usuario;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_view);

        recyclerView = findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        actionBar = findViewById(R.id.actionBar);
        setSupportActionBar(actionBar);

        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Meus Alertas");
        } catch (NullPointerException e) {
            callDialog(3, e.getMessage());
        }

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            usuario = new Gson().fromJson(bundle.getString("USUARIO"), Usuario.class);
        }

        buscaAlertas();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * This hook is called whenever an item in your options menu is selected.
     * The default implementation simply returns false to have the normal
     * processing happen (calling the item's Runnable or sending a message to
     * its Handler as appropriate).  You can use this method for any items
     * for which you would like to do processing without those other
     * facilities.
     * <p>
     * <p>Derived classes should call through to the base class for it to
     * perform the default menu handling.</p>
     *
     * @param item The menu item that was selected.
     * @return boolean Return false to allow normal menu processing to
     * proceed, true to consume it here.
     * @see #onCreateOptionsMenu
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home: {
                onBackPressed();
                finish();
            }
            break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void buscaAlertas() {

        callProgressDialog("Por favor aguarde", "Buscando seus alertas!");

        new Thread(new Runnable() {
            @Override
            public void run() {

                RetrofitService service = ServiceGenerator.createService(RetrofitService.class);
                Call<List<Alerta>> alertas = service.consultaAlertaUsuario(usuario.getId());

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
                            recyclerView.setAdapter(new AlertasAdapter(lista, CardViewActivity.this));

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
