package br.una.projetoaplicado.marcosbenevides.vizinhancasegura.requisicoesWS;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

import br.una.projetoaplicado.marcosbenevides.vizinhancasegura.classes.Usuario;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Marcos Benevides on 22/10/2016.
 */

public class ServiceGenerator {
    //URL de acesso a API
    public static final String API_URL = "https://192.168.1.110/ZISC2/res/";

    public static <S> S createService(Class<S> serviceClass){

        //Interceptador das requisições
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
                .readTimeout(15, TimeUnit.SECONDS);

        httpClient.addInterceptor(loggingInterceptor);



        //Instancia do retrofit, o interpretador.
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        return retrofit.create(serviceClass);
    }

}
