package br.una.projetoaplicado.marcosbenevides.vizinhancasegura;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LoginActivity extends Activity {

    String senha, email;
    Button cadastrar,confirmar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        confirmar = (Button)findViewById(R.id.buttonLogin);
        cadastrar = (Button)findViewById(R.id.buttonCreateLogin);

    }

    public void cadastrar(View arg0){
        Intent intent = new Intent(LoginActivity.this,CadastroActivity.class);
        startActivity(intent);

        //finish();
    }
}
