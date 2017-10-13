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

    private final String ERRO_AO_CONECTAR = "Erro ao conectar ao servidor. -> ";
    private final String AUT_SUCESSO = "Usuário autenticado com sucesso! -> ";
    private final String ERRO_AUTENTICACAO = "Login ou senha inválidas. -> ";
    private Usuario usuario = new Usuario();
    private String resposta = new String();
    private Object list[] = new Object[2];

    /**
     * Autentica no servidor e monta o array de resposta
     *
     * @param email
     * @param senha
     * @return [0] usuario, [1] resposta, podendo ser ERRO_AO_CONECTAR, AUT_SUCESSO ou ERRO_AUTENTICACAO
     */
    public Object[] autenticar(String email, String senha) {

        RetrofitService service = ServiceGenerator.createService(RetrofitService.class);
        Call<Usuario> call = service.loginCrip(email, senha);
        call.enqueue(new Callback<Usuario>() {

            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (!response.isSuccessful()) {
                    resposta = response.code() + " - " + response.message();
                    list[1] = resposta;
                } else {
                    if (response.body() != null) {
                        usuario = response.body();
                        resposta = AUT_SUCESSO + usuario.getId();
                        list[0] = usuario;
                        list[1] = resposta;

                    } else {
                        resposta = ERRO_AUTENTICACAO + usuario.getId();
                        list[1] = resposta;
                    }
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                resposta = ERRO_AO_CONECTAR + t.getMessage();
                list[1] = resposta;
            }
        });

        return list;
    }


    public String getErroAoConectar() {
        return ERRO_AO_CONECTAR;
    }

    public String getAutSucesso() {
        return AUT_SUCESSO;
    }

    public String getErroAutenticacao() {
        return ERRO_AUTENTICACAO;
    }
}
