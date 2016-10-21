package br.una.projetoaplicado.marcosbenevides.vizinhancasegura;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import br.una.projetoaplicado.marcosbenevides.vizinhancasegura.classes.Usuario;
import br.una.projetoaplicado.marcosbenevides.vizinhancasegura.operacoes.OperacoesGerais;
import br.una.projetoaplicado.marcosbenevides.vizinhancasegura.operacoes.OperacoesWS;

public class LoginActivity extends Activity {

    private String senha, email, teste1,teste2;
    private Button cadastrar, confirmar;
    private EditText emailEditor, senhaEditor;
    private ProgressDialog dialog;
    private LoginRequest loginRequest;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        confirmar = (Button) findViewById(R.id.buttonLogin);
        cadastrar = (Button) findViewById(R.id.buttonCreateLogin);
        emailEditor = (EditText) findViewById(R.id.emailEditor);
        senhaEditor = (EditText) findViewById(R.id.senhaEditor);
    }

    public void cadastrar(View arg0) {
        Intent intent = new Intent(LoginActivity.this, CadastroActivity.class);
        startActivity(intent);

        //finish();
    }

    public void consultaWS(View arg0){

        loginRequest = new LoginRequest();
        loginRequest.execute();

    }
    public void validationUser() {
        email = emailEditor.getText().toString();
        senha = senhaEditor.getText().toString();
    }


    private class LoginRequest extends AsyncTask<Void, Void, Usuario> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(LoginActivity.this, "Por favor, aguarde...", "Carregando dados do servidor...", true, true);
        }

        @Override
        protected void onPostExecute(Usuario pessoa) {
            teste1 = pessoa.getNome().substring(0,1).toUpperCase()+pessoa.getNome().substring(1);
            teste2 = pessoa.getEmail();
            String texto = pessoa.toString();
            Toast toast = Toast.makeText(LoginActivity.this,texto,Toast.LENGTH_LONG);
            toast.show();
            dialog.dismiss();
        }

        @Override
        protected Usuario doInBackground(Void... params) {
            OperacoesGerais op = new OperacoesGerais();
            return op.getInformacao("https://randomuser.me/api/");
        }
    }
}
