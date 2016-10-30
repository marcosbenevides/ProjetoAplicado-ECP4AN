package br.una.projetoaplicado.marcosbenevides.vizinhancasegura.requisicoesWS;

import java.util.Objects;

import br.una.projetoaplicado.marcosbenevides.vizinhancasegura.classes.Usuario;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Marcos Benevides on 22/10/2016.
 */

public interface RetrofitService {

    @GET("evalido")
    Call<Object> buscaUsuario();

    @GET("login/{email}/{senha}")
    Call<Usuario> login(@Path("email") String email, @Path("senha") String senha);


}
