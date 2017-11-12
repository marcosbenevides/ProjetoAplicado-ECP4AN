package br.una.zisc.requisicoesWS;

import java.util.Date;
import java.util.List;

import br.una.zisc.entidades.Alerta;
import br.una.zisc.entidades.CallHandler;
import br.una.zisc.entidades.DptoPolicia;
import br.una.zisc.entidades.Usuario;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by Marcos Benevides on 22/10/2016.
 */

public interface RetrofitService {

    /* Usuario */
    @FormUrlEncoded
    @POST("login/")
    Call<Usuario> loginCrip(@Field("email") String email,
                            @Field("password") String password);

    @FormUrlEncoded
    @POST("cadastrousuario/")
    Call<Usuario> cadastraUsuario(@Field("nome") String nome,
                                  @Field("email") String email,
                                  @Field("cpf") String cpf,
                                  @Field("celular") String celular,
                                  @Field("senha") String senha);

    /* Alertas */
    @FormUrlEncoded
    @POST("consultaalerta/")
    Call<List<Alerta>> consultaAlerta(@Field("latitude") String latitude,
                                      @Field("longitude") String longitude);

    @FormUrlEncoded
    @POST("cadastroalerta/")
    Call<Alerta> cadastraralerta(@Field("id") int idUsuario,
                                 @Field("longitude") String longitude,
                                 @Field("latitude") String latitude,
                                 @Field("bairro") String bairro,
                                 @Field("cidade") String cidade,
                                 @Field("estado") String estado,
                                 @Field("observacao") String obs,
                                 @Field("tipo") String tipo,
                                 @Field("ePositivo") Boolean ePositivo);
    @GET("consultaAlertaUsuario/{id}")
    Call<List<Alerta>> consultaAlertaUsuario(@Path("id") int idUsuario);

    /* CallHandler */
    @FormUrlEncoded
    @POST("callhandler/")
    Call<CallHandler> setCallHandler(@Field("id_usuario") int id_usuario,
                                     @Field("latitude") String latitude,
                                     @Field("longitude") String longitude,
                                     @Field("cidade") String cidade,
                                     @Field("bairro") String bairro,
                                     @Field("estado") String estado);

    @FormUrlEncoded
    @PUT("callhandler/")
    Call<CallHandler> cancelCallHandler(@Field("id") int id_call);

    /* Departamento de Policia */
    @GET("consultadpto/")
    Call<List<DptoPolicia>> buscaDpto();
}
