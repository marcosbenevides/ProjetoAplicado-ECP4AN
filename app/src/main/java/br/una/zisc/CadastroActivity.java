package br.una.zisc;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import br.una.projetoaplicado.marcosbenevides.zisc.R;
import br.una.zisc.entidades.Usuario;
import br.una.zisc.requisicoesWS.RetrofitService;
import br.una.zisc.requisicoesWS.ServiceGenerator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CadastroActivity extends Activity {

    String nome, email, senha, re_senha, cpf, celular;
    Button cadastrar;
    EditText nome_usuario, email_usuario, senha_usuario, re_senha_usuario, cpf_usuario, cel_usuario;
    AlertDialog.Builder alertDialog;
    ProgressDialog dialog;
    Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        nome_usuario = (EditText) findViewById(R.id.textNomeUser);
        email_usuario = (EditText) findViewById(R.id.textEmailUser);
        senha_usuario = (EditText) findViewById(R.id.textPasswd);
        re_senha_usuario = (EditText) findViewById(R.id.textRePasswd);
        cpf_usuario = (EditText) findViewById(R.id.textCpfUser);
        cel_usuario = (EditText) findViewById(R.id.textCelUser);

        cadastrar = (Button) findViewById(R.id.buttonRegister);
        cadastrar.setOnClickListener(new View.OnClickListener() {
            /**
             * Called when a view has been clicked.
             *
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {

                nome = nome_usuario.getText().toString();
                email = email_usuario.getText().toString();
                senha = senha_usuario.getText().toString();
                re_senha = re_senha_usuario.getText().toString();
                cpf = cpf_usuario.getText().toString();
                celular = cel_usuario.getText().toString();

                if (!senha.equals(re_senha)) {
                    Toast.makeText(CadastroActivity.this, "As senhas digitadas não batem!", Toast.LENGTH_LONG).show();
                } else {
                    callProgressDialog("Aguarde um momento.", "Efetuando seu cadastro!");
                    cadastrar();
                }
            }
        });
    }

    public void cadastrar() {
        RetrofitService service = ServiceGenerator.createService(RetrofitService.class);
        Call<Usuario> call = service.cadastraUsuario(nome, email, cpf, celular, senha);
        call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (!response.isSuccessful()) {
                    callDialog(1, response.code() + " - " + response.message());
                } else if (response.body() == null) {
                    callDialog(2, "");
                } else {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    usuario = response.body();
                    Toast.makeText(CadastroActivity.this, "Cadastro efetuado com sucesso!", Toast.LENGTH_SHORT).show();
                    cadastroConcluido();
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                callDialog(3, t.getCause() + " - " + t.getMessage());
            }
        });
    }

    public void cadastroConcluido() {
        Intent intent = new Intent(CadastroActivity.this, MenuLateral.class);
        intent.putExtra("EMAIL", usuario.getNome());
        intent.putExtra("ID", usuario.getId());
        startActivity(intent);

        finish();
    }

    public void callDialog(final Integer tipoErro, final String response) {

        runOnUiThread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void run() {
                String title;
                String msg;
                if (tipoErro == 1) {
                    title = getResources().getString(R.string.ERRO_AO_CONECTAR);
                    msg = response;
                } else if (tipoErro == 2) {
                    title = "OPS!";
                    msg = "Email já cadastrado!";
                } else {
                    title = getResources().getString(R.string.ERRO_AO_CONECTAR);
                    msg = response;
                }
                if (dialog != null) {
                    dialog.dismiss();
                }
                alertDialog = new AlertDialog.Builder(CadastroActivity.this)
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
                CadastroActivity.this.dialog = ProgressDialog.show(CadastroActivity.this, title, message);
            }
        });
    }

}
