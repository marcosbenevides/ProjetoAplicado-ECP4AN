package br.una.zisc;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.nfc.Tag;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import br.una.projetoaplicado.marcosbenevides.zisc.Manifest;
import br.una.projetoaplicado.marcosbenevides.zisc.R;
import br.una.zisc.classes.Usuario;
import br.una.zisc.requisicoesWS.RetrofitCall;
import br.una.zisc.requisicoesWS.RetrofitService;
import br.una.zisc.requisicoesWS.ServiceGenerator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends Activity {

    private static final int MY_PERMISSION_LOCATION = 0;
    private String senha, email, ativaGPS = "GPS desativado, deseja ativa-lo?", ativaInternet = "Não existe nenhum tipo de conexão, deseja ativar o WIFI?";
    private Button cadastrar, confirmar;
    private EditText emailEditor, senhaEditor, remotoEditor, localEditor;
    private TextView status_error, easterEgg;
    private ProgressDialog dialog;
    private AlertDialog.Builder alertDialog, easterEggConfig;
    private AlertDialog alerta;
    public static final String TAG = "MARCOS: ";
    private Intent it;
    private SharedPreferences preferences;
    private CheckBox checkLogin;
    private Usuario usuario = new Usuario();
    private LocationManager locationManager;
    private ConnectivityManager connectivityManager;
    private ToggleButton toggleButton;
    private Base64 base64;
    private RetrofitCall call = new RetrofitCall();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        confirmar = (Button) findViewById(R.id.buttonLogin);
        cadastrar = (Button) findViewById(R.id.buttonCreateLogin);
        emailEditor = (EditText) findViewById(R.id.emailEditor);
        senhaEditor = (EditText) findViewById(R.id.senhaEditor);
        easterEgg = (TextView) findViewById(R.id.textView);
        status_error = (TextView) findViewById(R.id.status_login);
        checkLogin = (CheckBox) findViewById(R.id.checkLogin);

        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        emailEditor.setText(sharedPreferences.getString("emailUsuario", ""));
        senhaEditor.setText(sharedPreferences.getString("senhaUsuario", ""));
        checkLogin.setChecked(sharedPreferences.getBoolean("checkLogin", checkLogin.isChecked()));


        int internetPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.INTERNET);
        int locationPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        if (internetPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.INTERNET}, internetPermission);
        }
        if (locationPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, locationPermission);
        }

        gpsLigado();

        easterEgg.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                easterEggConfig = new AlertDialog.Builder(LoginActivity.this)
                        .setTitle("EDITAR PREFERÊNCIAS")
                        .setPositiveButton("SALVAR", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ServiceGenerator.API_URL_REMOTO = remotoEditor.getText().toString();
                                ServiceGenerator.API_URL_LOCAL = localEditor.getText().toString();
                            }
                        });

                LayoutInflater layoutInflater = LoginActivity.this.getLayoutInflater();
                View dialogView = layoutInflater.inflate(R.layout.easter_egg_config, null);
                easterEggConfig.setView(dialogView);

                toggleButton = (ToggleButton) dialogView.findViewById(R.id.toggleButton);
                remotoEditor = (EditText) dialogView.findViewById(R.id.editorRemoto);
                localEditor = (EditText) dialogView.findViewById(R.id.editorLocal);

                toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            remotoEditor.setEnabled(false);
                            localEditor.setEnabled(true);
                            ServiceGenerator.eLocal = true;
                        } else {
                            remotoEditor.setEnabled(true);
                            localEditor.setEnabled(false);
                            ServiceGenerator.eLocal = false;
                        }
                    }
                });

                remotoEditor.setEnabled(true);
                localEditor.setEnabled(false);
                remotoEditor.setText(ServiceGenerator.API_URL_REMOTO);
                localEditor.setText(ServiceGenerator.API_URL_LOCAL);

                alerta = easterEggConfig.create();
                alerta.show();

                return true;
            }
        });

        //Ação para autenticar usuário.
        confirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                * converte email e senha para base64
                * */
                email = Base64.encodeToString(emailEditor.getText().toString().getBytes(), Base64.NO_WRAP);
                senha = Base64.encodeToString(String.valueOf(senhaEditor.getText()).getBytes(), Base64.NO_WRAP);

                /*
                * Abre um dialogo de carregamento na tela.
                * */
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dialog = ProgressDialog.show(LoginActivity.this,
                                        "Por favor, aguarde!", "Carregando dados do servidor...");
                            }
                        });
                        //Autentica no webservice
                        final List<Object> listaAutentica = call.autenticar(email, senha);

                        /*
                        * Testa o retorno da função autentica com 3 condicoes de acordo com o retorno da funcao.
                        * retorno AUT_SUCESSO, deve fazer login do usuario, criar a sessão, e chamar o activity do mapa
                        * retorno ERRO_AUTENTICACAO, remover o dialogo, e informar ao usuario que a senha está errada.
                        * qualquer outro retorno, mostra a mensagem em um dialogo.
                        * */
                        if (listaAutentica.get(1).toString().contains(call.AUT_SUCESSO)) {

                            if (checkLogin.isChecked()) {
                                preferences = getSharedPreferences("LOGIN", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString("email", emailEditor.getText().toString());
                                editor.putString("senha", senhaEditor.getText().toString());
                                editor.putBoolean("checkLogin", checkLogin.isChecked());
                                editor.apply();
                            }

                            usuario = (Usuario) listaAutentica.get(0);

                            it = new Intent(LoginActivity.this, MapsActivity.class);
                            it.putExtra("EMAIL", usuario.getEmail());

                            status_error.setVisibility(View.INVISIBLE);

                            Intent intent = new Intent(LoginActivity.this, MapsActivity.class);
                            startActivity(intent);
                            it.putExtra("ID", usuario.getId());

                            Toast.makeText(LoginActivity.this, "Login realizado com sucesso!", Toast.LENGTH_LONG).show();

                        } else if (listaAutentica.get(1).toString().contains(call.ERRO_AUTENTICACAO)) {

                            if(dialog!=null){
                                dialog.dismiss();
                            }
                            status_error.setVisibility(View.VISIBLE);

                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(dialog!=null){
                                        dialog.dismiss();
                                    }
                                    alertDialog = new AlertDialog.Builder(LoginActivity.this)
                                            .setMessage(listaAutentica.get(1).toString())
                                            .setCancelable(true)
                                            .setPositiveButton("OK", null);
                                    alertDialog.create();
                                    alertDialog.show();
                                }
                            });
                        }
                    }
                }).start();
            }
        });

        preferences = getSharedPreferences("LOGIN", MODE_PRIVATE);
        String emailPrefs = preferences.getString("email", null);
        String senhaPrefs = preferences.getString("senha", null);
        Boolean lembrarPrefs = preferences.getBoolean("checkLogin", true);

        if (emailPrefs != null) {
            emailEditor.setText(emailPrefs);
            senhaEditor.setText(senhaPrefs);
            checkLogin.setChecked(lembrarPrefs);
        }

        //Marcador m = new Marcador();
        //m.distancia2Pontos("-20.064247", "-44.282156", "-20.066588", "-44.281439");

    }

    public void cadastrar(View arg0) {
        Intent intent = new Intent(LoginActivity.this, CadastroActivity.class);
        startActivity(intent);

        //finish();
    }

/*
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
                Log.e(TAG, email + " = " + senha);
                call = service.loginCrip(email, senha);
                call.enqueue(new Callback<Usuario>() {
                    @Override
                    public void onResponse(final Call<Usuario> call, final Response<Usuario> response) {
                        if (!response.isSuccessful()) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dialog.dismiss();
                                    alertDialog = new AlertDialog.Builder(LoginActivity.this)
                                            .setMessage("Impossível conectar ao servidor!\n" + response.message())
                                            .setCancelable(true)
                                            .setPositiveButton("OK", null);
                                    alertDialog.create();
                                    alertDialog.show();
                                    Log.e(TAG, " " + response.message());

                                }
                            });
                        } else {
                            dialog.dismiss();
                            if (checkLogin.isChecked()) {
                                preferences = getSharedPreferences("LOGIN", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString("email", emailEditor.getText().toString());
                                editor.putString("senha", senhaEditor.getText().toString());
                                editor.putBoolean("checkLogin", checkLogin.isChecked());
                                editor.commit();
                            }

                            if (response.body() != null) {
                                usuario = response.body();
                                it = new Intent(LoginActivity.this, MapsActivity.class);
                                if (it != null) {
                                    it.putExtra("EMAIL", usuario.getEmail());
                                    Log.e("PUT1", usuario.getEmail());
                                }
                                status_error.setVisibility(View.INVISIBLE);
                                Intent intent = new Intent(LoginActivity.this, MapsActivity.class);
                                startActivity(intent);
                                if (intent != null) {
                                    it.putExtra("EMAIL", usuario.getEmail());
                                    Log.e("PUT2", usuario.getEmail());
                                }
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
                                .setMessage("Impossível conectar ao servidor!\n" + t.getMessage())
                                .setCancelable(true)
                                .setPositiveButton("OK", null);
                        alertDialog.create();
                        alertDialog.show();
                        Log.e(TAG, "Falha: " + t.getMessage());
                        */

    /**
     * Retirar até startActivity(it); qndo tudo der certo
     *//*

//                        Intent it = new Intent(LoginActivity.this, MapsActivity.class);
//                        if (it != null) {
//                            it.putExtra("EMAIL", "errrrrrrrrrrrrrrrrou");
//                        }
//                        startActivity(it);
                    }
                });
            }
        }
        ).start();


    }
*/
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

    /**
     * Verifica se o GPS está ligado, se não chama um dialogo perguntando se quer ligar
     */
    public void gpsLigado() {
        locationManager = (LocationManager) getSystemService(this.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            alertAtiva(ativaGPS);
            return;
        }
        temConexao();
    }

    /**
     * Verifica se tem internet, se não chama um dialogo perguntando se quer conectar no WIFI
     */
    public void temConexao() {
        connectivityManager = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
        for (NetworkInfo n : networkInfo) {
            if (n.getTypeName().equalsIgnoreCase("WIFI"))
                if (n.isConnected())
                    return;
            if (n.getTypeName().equalsIgnoreCase("MOBILE"))
                if (n.isConnected())
                    return;
        }
        alertAtiva(ativaInternet);
    }

    /**
     * Pergunta se quer ativar o que precisa, se aceitar vai para devida tela
     * se não fecha o app
     */
    public void alertAtiva(final String nome) {
        final AlertDialog.Builder alertAtivaDialog = new AlertDialog.Builder(this);
        alertAtivaDialog.setMessage(nome)
                .setTitle("Algo está desativado!")
                .setCancelable(false)
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        if (nome.equals(ativaGPS))
                            startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
                        else
                            startActivityForResult(new Intent(Settings.ACTION_WIFI_SETTINGS), 0);
                    }
                })
                .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                        finish();
                    }
                });
        final AlertDialog alert = alertAtivaDialog.create();
        alert.show();
    }

    /**
     * Se o ususario for pra tela de ativar o que devia e não ativar
     * pergunta novamente
     */
    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent data) {
        gpsLigado();
    }

}
