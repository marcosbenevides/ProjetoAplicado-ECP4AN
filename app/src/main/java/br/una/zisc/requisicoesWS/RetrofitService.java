package br.una.zisc.requisicoesWS;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import br.una.zisc.classes.Alerta;
import br.una.zisc.classes.Usuario;
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

    @FormUrlEncoded
    @POST("login/")
    Call<Usuario> loginCrip(@Field("email") String email,
                           @Field("password") String password);

    /*@GET("consultaalerta/{bairro}/{cidade}/{estado}")
    Call<List<Alerta>> consultaAlerta(@Path("bairro") String bairro,
                                      @Path("cidade") String cidade,
                                      @Path("estado") String estado);*/

    @GET("ConsultaAlerta/{latitude}/{longitude}")
    Call<List<Alerta>> consultaAlerta(@Path("latitude") String longitude,
                                      @Path("longitude") String latitude);

    @FormUrlEncoded
    @POST("CadastrarAlerta")
    Call<String> cadastraralerta(@Field("email") String email,
                                 @Field("logHora") Timestamp data,
                                 @Field("latitude") String latitude,
                                 @Field("longitude") String longitude,
                                 @Field("bairro") String bairro,
                                 @Field("cidade") String cidade,
                                 @Field("estado") String estado,
                                 @Field("obs") String obs,
                                 @Field("tipo") String tipo,
                                 @Field("ePositivo") Boolean ePositivo);
}
