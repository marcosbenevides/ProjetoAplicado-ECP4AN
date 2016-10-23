package br.una.projetoaplicado.marcosbenevides.vizinhancasegura;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import br.una.projetoaplicado.marcosbenevides.vizinhancasegura.classes.Usuario;
import br.una.projetoaplicado.marcosbenevides.vizinhancasegura.requisicoesWS.RetrofitService;
import br.una.projetoaplicado.marcosbenevides.vizinhancasegura.requisicoesWS.ServiceGenerator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends Activity {

    private String senha, email;
    private Button cadastrar, confirmar;
    private EditText emailEditor, senhaEditor;
    private TextView status_error;
    private ProgressDialog dialog;
    Usuario usuario = new Usuario();
    Thread thread;
    public static final String TAG = "MARCOS: ";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        confirmar = (Button) findViewById(R.id.buttonLogin);
        cadastrar = (Button) findViewById(R.id.buttonCreateLogin);
        emailEditor = (EditText) findViewById(R.id.emailEditor);
        senhaEditor = (EditText) findViewById(R.id.senhaEditor);
        status_error = (TextView) findViewById(R.id.status_login);
    }

    public void cadastrar(View arg0) {
        Intent intent = new Intent(LoginActivity.this, CadastroActivity.class);
        startActivity(intent);

        //finish();
    }

    public void consultaWS(View arg0) {


        new Thread(new Runnable() {

            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog = ProgressDialog.show(LoginActivity.this, "Por favor, aguarde...", "Carregando dados do servidor...");
                    }
                });
                RetrofitService service = ServiceGenerator.createService(RetrofitService.class);
                Call<Boolean> call = service.login();
                call.enqueue(new Callback<Boolean>() {
                    @Override
                    public void onResponse(Call<Boolean> call, final Response<Boolean> response) {
                        if (!response.isSuccessful()) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dialog.dismiss();
                                    Log.e(TAG, " " + response.message());
                                }
                            });
                        } else {
                            dialog.dismiss();
                            if(response.body() == true){
                                status_error.setVisibility(View.INVISIBLE);
                                //Intent intent = new Intent(LoginActivity.this,MapaActivity.class);
                                Toast.makeText(LoginActivity.this,"Login realizado com sucesso!",Toast.LENGTH_LONG);
                            }else{
                                senhaEditor.setText("");
                                status_error.setVisibility(View.VISIBLE);
                                Toast.makeText(LoginActivity.this, "Algo está errado!", Toast.LENGTH_SHORT).show();
                            }
                            Log.e(TAG, usuario.toString());
                        }
                    }

                    @Override
                    public void onFailure(Call<Boolean> call, Throwable t) {
                        dialog.dismiss();
                        Log.e(TAG, "Falha: " + t.getMessage());
                    }
                });
            }
        }).start();


    }
}
