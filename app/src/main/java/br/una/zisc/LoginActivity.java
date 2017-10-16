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
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import br.una.projetoaplicado.marcosbenevides.zisc.R;
import br.una.zisc.entidades.Usuario;
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
    private Object listaAutentica[];
    private Network network;
    private Thread threadOne, threadTwo;


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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog = ProgressDialog.show(LoginActivity.this,
                                "Por favor, aguarde!", "Carregando dados do servidor...");
                    }
                });

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        /*
                        * Criação do servico de conexão com o webservice
                        **/
                        RetrofitService service = ServiceGenerator.createService(RetrofitService.class);
                        /*
                        * chamada de método de autenticação no server.
                        * */
                        Call<Usuario> call = service.loginCrip(email, senha);

                        /*
                        * Método assincrono, augarda resposta do server.
                        * */
                        call.enqueue(new Callback<Usuario>() {

                            /*
                            * A obter uma resposta do servidor esse método irá tratar as proximas ações
                            * dependendo da resposta. Podendo ser elas:
                            * -> 200 e corpo da resposta não nulo = usuario autenticado com sucesso!
                            * -> 200 e corpo da resposta nulo = senha ou login de usuario errado!
                            * -> 500, 404 ou outra resposta = erro de conexão.
                            * */
                            @Override
                            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                                if (response.isSuccessful()) {
                                    if (response.body() != null) {
                                        //Salva as preferências do usuário
                                        if (checkLogin.isChecked()) {
                                            preferences = getSharedPreferences("LOGIN", Context.MODE_PRIVATE);
                                            SharedPreferences.Editor editor = preferences.edit();
                                            editor.putString("email", emailEditor.getText().toString());
                                            editor.putString("senha", senhaEditor.getText().toString());
                                            editor.putBoolean("checkLogin", checkLogin.isChecked());
                                            editor.apply();
                                        }

                                        usuario = response.body();
                                        it = new Intent(LoginActivity.this, MapsActivity.class);
                                        it.putExtra("EMAIL", usuario.getEmail());
                                        it.putExtra("ID", usuario.getId());

                                        status_error.setVisibility(View.INVISIBLE);

                                        Intent intent = new Intent(LoginActivity.this, MapsActivity.class);
                                        startActivity(intent);

                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(LoginActivity.this, "Login realizado com sucesso!", Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    } else {
                                        if (dialog != null) {
                                            dialog.dismiss();
                                        }
                                        status_error.setVisibility(View.VISIBLE);
                                    }
                                } else {
                                    callDialog(1, response.message() + " - " + response.code());
                                }
                            }

                            @Override
                            public void onFailure(Call<Usuario> call, Throwable t) {
                                callDialog(1, t.getMessage() + " - " + t.getCause());
                            }
                        });

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
    }

    public void cadastrar(View arg0) {
        Intent intent = new Intent(LoginActivity.this, CadastroActivity.class);
        startActivity(intent);

        //finish();
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

    /**
     * Chama o dialogMessage com o tipo especificado nos parametros
     *
     * @param tipoErro
     * @param response
     */
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
                } else {
                    title = getResources().getString(R.string.ERRO_AO_CONECTAR);
                    msg = response;
                }
                if (dialog != null) {
                    dialog.dismiss();
                }
                alertDialog = new AlertDialog.Builder(LoginActivity.this)
                        .setTitle(title)
                        .setMessage(msg)
                        .setCancelable(true)
                        .setPositiveButton("OK", null);
                alertDialog.create();
                alertDialog.show();
            }
        });
    }

}
