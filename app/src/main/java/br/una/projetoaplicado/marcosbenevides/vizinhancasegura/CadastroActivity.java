package br.una.projetoaplicado.marcosbenevides.vizinhancasegura;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class CadastroActivity extends Activity {

    String name,email,cep,password;
    Button cadastrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        cadastrar = (Button) findViewById(R.id.buttonRegister);
    }

    public void cadastroConcluido(View arg0){
        Intent intent = new Intent(CadastroActivity.this,LoginActivity.class);
        startActivity(intent);

        finish();
    }
}
