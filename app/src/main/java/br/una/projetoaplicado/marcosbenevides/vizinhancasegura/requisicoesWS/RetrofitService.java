package br.una.projetoaplicado.marcosbenevides.vizinhancasegura.requisicoesWS;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import br.una.projetoaplicado.marcosbenevides.vizinhancasegura.classes.Alerta;
import br.una.projetoaplicado.marcosbenevides.vizinhancasegura.classes.Usuario;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by Marcos Benevides on 22/10/2016.
 */

public interface RetrofitService {

    @GET("evalido")
    Call<Object> buscaUsuario();

    @GET("login/{email}/{senha}")
    Call<Usuario> login(@Path("email") String email, @Path("senha") String senha);

    @GET("consultaalerta/{bairro}/{cidade}/{estado}")
    Call<List<Alerta>> consultaAlerta(@Path("bairro") String bairro,
                                      @Path("cidade") String cidade,
                                      @Path("estado") String estado);

    @GET("consutaalerta/{longitude}/{latitude}")
    Call<List<Alerta>> consultaAlerta(@Path("longitude") String longitude,
                                      @Path("latitude") String latitude);

    @FormUrlEncoded
    @POST("cadastrarAlerta")
    Call<String> cadastrarAlerta(@Field("email") String email,
                                 @Field("logHora") Date data,
                                 @Field("latitude") String latitude,
                                 @Field("longitude") String longitude,
                                 @Field("bairro") String bairro,
                                 @Field("cidade") String cidade,
                                 @Field("estado") String estado,
                                 @Field("obs") String obs,
                                 @Field("tipo") String tipo,
                                 @Field("ePositivo") Boolean ePositivo);
}
