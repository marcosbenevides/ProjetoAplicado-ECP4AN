package br.una.zisc;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import br.una.projetoaplicado.marcosbenevides.zisc.R;

public class ActTelaParaMarcar extends AppCompatActivity implements View.OnClickListener {

    String classificacao = "vazio";
    private Button btnOk, btnCancelar;
    private TextView teste;
    Intent it;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_tela_para_marcar);

        btnOk = (Button)findViewById(R.id.btnOk);
        btnCancelar = (Button)findViewById(R.id.btnCancelar);

        btnOk.setOnClickListener(this);
        btnCancelar.setOnClickListener(this);

        teste = (TextView) findViewById(R.id.teste);


    }

    public void onClick(View v){
        it = new Intent(this, MenuLateral.class);
        switch (v.getId()){
            case R.id.btnOk:
                if(it!=null && !classificacao.equals("vazio")) {
                    it.putExtra("Classificacao", classificacao);

                    if(classificacao.equals("Bom"))
                        setResult(1);
                    else
                        setResult(2);
                    finish();
                }
                break;
            case R.id.btnCancelar:
                if(it!=null) {
                    setResult(3);
                    finish();
                }
                break;
        }

    }

    public void tipoSelecionado(View view){
        Bundle bundle = getIntent().getExtras();        //trecho para testar passagem de parametros

        if(bundle != null) {                            //trecho para testar passagem de parametros
            String tipo = bundle.getString("TESTE");    //trecho para testar passagem de parametros
            teste.setText(tipo);                        //trecho para testar passagem de parametros
        }
        switch (view.getId()){
            case R.id.bom:
                classificacao = "Bom";
                break;
            case R.id.ruim:
                classificacao = "Ruim";
                break;
        }
    }

}
