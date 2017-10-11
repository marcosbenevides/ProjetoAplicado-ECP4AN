package br.una.zisc.requisicoesWS;

import android.content.pm.LauncherApps;
import android.util.Log;

import java.util.List;

import br.una.zisc.classes.Usuario;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Avanti Premium on 11/10/2017.
 */

public class RetrofitCall {

    private static final String ERRO_AO_CONECTAR = "Erro ao conectar ao servidor. -> ";
    private static final String AUT_SUCESSO = "Usuário autenticado com sucesso! -> ";
    private static final String ERRO_AUTENTICACAO = "Login ou senha inválidas. -> ";

    Usuario usuario = new Usuario();
    List<Object> list;
    String resposta;


    public List<Object> autenticar(String email, String senha) {

        RetrofitService service = ServiceGenerator.createService(RetrofitService.class);
        Call<Usuario> call = service.loginCrip(email, senha);
        call.enqueue(new Callback<Usuario>() {

            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (!response.isSuccessful()) {
                    resposta = ERRO_AO_CONECTAR + response.message();
                } else {
                    if (response.body() != null) {
                        usuario = response.body();
                        resposta = AUT_SUCESSO + usuario.getId();
                    }else{
                        resposta = ERRO_AUTENTICACAO + usuario.getId();
                    }
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {

            }
        });

        list.add(0, usuario);
        list.add(1, resposta);
        return list;
    }

}
