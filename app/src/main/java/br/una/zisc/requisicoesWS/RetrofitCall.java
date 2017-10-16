package br.una.zisc.requisicoesWS;

import java.util.List;

import br.una.zisc.entidades.Alerta;
import br.una.zisc.entidades.Usuario;
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
    private final String SEM_ALERTAS = "Não foi encontrado alertas \npara esta região!";
    private final String ENCONTRADO_ALERTAS = "Foram encontrados -> ";

    private Usuario usuario = new Usuario();
    private String resposta = new String();
    private Object list[] = new Object[2];

    /**
     * Autentica no servidor e monta o array de resposta
     *
     * @param emailCript
     * @param senhaCript
     * @return [0] usuario, [1] resposta, podendo ser ERRO_AO_CONECTAR, AUT_SUCESSO ou ERRO_AUTENTICACAO
     */
    public Object[] autenticar(String emailCript, String senhaCript) {
        final String email = emailCript;
        final String senha = senhaCript;

         new Thread(new Runnable() {
            @Override
            public void run() {
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
            }
        });

        return list;
    }

    public Object[] buscaAlerta(final String latitude, final String longitude) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                RetrofitService service = ServiceGenerator.createService(RetrofitService.class);
                Call<List<Alerta>> alertas = service.consultaAlerta(latitude, longitude);

                alertas.enqueue(new Callback<List<Alerta>>() {
                    @Override
                    public void onResponse(Call<List<Alerta>> call, Response<List<Alerta>> response) {

                        if (!response.isSuccessful()) {
                            resposta = ERRO_AO_CONECTAR + "\n" + response.code() + " - " + response.message();
                            list[1] = resposta;
                        } else if (response.body() != null) {
                            List<Alerta> alertas = response.body();
                            resposta = ENCONTRADO_ALERTAS + alertas.size();
                            list[0] = alertas;
                            list[1] = resposta;
                        } else {
                            resposta = SEM_ALERTAS;
                            list[1] = resposta;
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Alerta>> call, Throwable t) {

                        resposta = ERRO_AO_CONECTAR + t.getMessage();
                        list[1] = resposta;
                    }
                });
            }
        }).start();

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

    public String getSEM_ALERTAS() {
        return SEM_ALERTAS;
    }

    public String getENCONTRADO_ALERTAS() {
        return ENCONTRADO_ALERTAS;
    }
}
