package br.una.zisc;

import android.*;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import br.una.zisc.classes.Usuario;
import br.una.zisc.requisicoesWS.RetrofitService;
import br.una.zisc.requisicoesWS.ServiceGenerator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends Activity {

    private static final int MY_PERMISSION_LOCATION = 0;
    private String senha, email;
    private Button cadastrar, confirmar;
    private EditText emailEditor, senhaEditor;
    private TextView status_error;
    private ProgressDialog dialog;
    Usuario usuario = new Usuario();
    AlertDialog.Builder alertDialog;
    public static final String TAG = "MARCOS: ";
    private Intent it;
    private CheckBox checkSenha;
    private SharedPreferences preferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        confirmar = (Button) findViewById(R.id.buttonLogin);
        cadastrar = (Button) findViewById(R.id.buttonCreateLogin);
        emailEditor = (EditText) findViewById(R.id.emailEditor);
        senhaEditor = (EditText) findViewById(R.id.senhaEditor);
        status_error = (TextView) findViewById(R.id.status_login);
        checkSenha = (CheckBox) findViewById(R.id.checkSenha);

        ActivityCompat.requestPermissions(this,
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                MY_PERMISSION_LOCATION);

    }

    public void cadastrar(View arg0) {
        Intent intent = new Intent(LoginActivity.this, CadastroActivity.class);
        startActivity(intent);

        //finish();
    }

    public void botaoLogin(View arg0){
        email = emailEditor.getText().toString();
        //senha = md5(String.valueOf(senhaEditor.getText()));
        senha = String.valueOf(senhaEditor.getText());

        try {
            consultaWS();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public void consultaWS() throws UnsupportedEncodingException, NoSuchAlgorithmException {

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
                Call<Usuario> call = null;
                    Log.e(TAG,email  + " = " + senha);
                    call = service.login(email, senha);
                call.enqueue(new Callback<Usuario>() {
                    @Override
                    public void onResponse(final Call<Usuario> call, final Response<Usuario> response) {
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
                            usuario = response.body();
                            if(checkSenha.isChecked()){
                                preferences = getPreferences(Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString("emailUser", emailEditor.getText().toString());
                                editor.putString("senhaUser", senhaEditor.getText().toString());
                                editor.commit();
                            }
                            it = new Intent(LoginActivity.this, MapsActivity.class);
                            if(it != null) {
                                it.putExtra("EMAIL", usuario.getEmail());
                            }
                            usuario.getEmail();
                            if (usuario.getEmail().equalsIgnoreCase(email)) {
                                status_error.setVisibility(View.INVISIBLE);
                                Intent intent = new Intent(LoginActivity.this, MapsActivity.class);
                                startActivity(intent);
                                Toast.makeText(LoginActivity.this, "Login realizado com sucesso!", Toast.LENGTH_LONG).show();
                            } else {
                                senhaEditor.setText("");
                                status_error.setVisibility(View.VISIBLE);
                                Toast.makeText(LoginActivity.this, "Algo está errado!", Toast.LENGTH_LONG).show();
                            }
                            Log.e(TAG, usuario.toString());
                        }
                    }

                    @Override
                    public void onFailure(Call<Usuario> call, Throwable t) {
                        dialog.dismiss();

                        alertDialog = new AlertDialog.Builder(LoginActivity.this)
                                .setMessage("Impossível conectar ao servidor!")
                                .setCancelable(true)
                                .setPositiveButton("OK", null);
                        alertDialog.create();
                        alertDialog.show();
                        Log.e(TAG, "Falha: " + t.getMessage());
                        Intent it = new Intent(LoginActivity.this, MapsActivity.class);
                        if(it != null) {
                            it.putExtra("EMAIL", "errrrrrrrrrrrrrrrrou");
                        }
                        startActivity(it);
                    }
                });
            }
        }
        ).start();


    }


    private String md5(String senha) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        byte b[] = messageDigest.digest(senha.getBytes("UTF-8"));
        StringBuilder stringBuilder = new StringBuilder();
        for (byte b1 : b) {
            stringBuilder.append(String.format("%02X", 0xFF & b1));
        }
        final String MD5_CRYPT = stringBuilder.toString();

        return MD5_CRYPT;
    }

}