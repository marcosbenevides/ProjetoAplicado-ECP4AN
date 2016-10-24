package br.una.projetoaplicado.marcosbenevides.vizinhancasegura.requisicoesWS;

import br.una.projetoaplicado.marcosbenevides.vizinhancasegura.classes.Usuario;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

/**
 * Created by Marcos Benevides on 22/10/2016.
 */

public interface RetrofitService {

    @GET("usuario")
//    Call<String> buscarUsuario();

    Call<Boolean> login();
/*    Call<Usuario> buscarUsuario(@Path("idusuario") String idusuario,
                                   @Path("nome") String nome,
                                   @Path("email") String email,
                                   @Path("enderecos") String enderecos,
                                   @Path("segurancas") String segurancas,
                                   @Path("alertas") String alertas);*/



}
