package br.una.zisc;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import br.una.projetoaplicado.marcosbenevides.zisc.R;
import br.una.zisc.entidades.Usuario;

public class ProfileActivity extends AppCompatActivity {

    private TextInputLayout layout_nome, layout_email, layout_cpf, layout_celular;
    private EditText input_nome, input_email, input_cpf, input_celular;
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        layout_nome = findViewById(R.id.layout_nome);
        layout_email = findViewById(R.id.layout_email);
        layout_cpf = findViewById(R.id.layout_cpf);
        layout_celular = findViewById(R.id.layout_celular);

        input_nome = findViewById(R.id.input_nome);
        input_email = findViewById(R.id.input_email);
        input_cpf = findViewById(R.id.input_cpf);
        input_celular = findViewById(R.id.input_celular);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            usuario = new Gson().fromJson(bundle.getString("USUARIO"), Usuario.class);
        }

        try{
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(usuario.getNome());
        }catch(NullPointerException ex){
            Toast.makeText(this,ex.getMessage(),Toast.LENGTH_LONG).show();
        }

        input_nome.setText(usuario.getNome());
        input_email.setText(usuario.getEmail());
        input_cpf.setText(usuario.getCpf());
        input_celular.setText(usuario.getCelular());

        input_cpf.setEnabled(false);

    }
}
