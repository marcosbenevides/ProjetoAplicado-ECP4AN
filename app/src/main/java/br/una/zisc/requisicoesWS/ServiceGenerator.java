package br.una.zisc.requisicoesWS;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Marcos Benevides on 22/10/2016.
 */

public class ServiceGenerator {
    //URL de acesso a API
    public static String API_URL_REMOTO = "http://ec2-54-187-15-178.us-west-2.compute.amazonaws.com/ZISC/res/";
    public static String API_URL_LOCAL = "http://192.168.1.110:8080/ZISC/res/";
    public static String API_URL = API_URL_LOCAL;
    public static Boolean eLocal = true;

    public static <S> S createService(Class<S> serviceClass) {

        if (eLocal) {
            API_URL = API_URL_LOCAL;
        } else {
            API_URL = API_URL_REMOTO;
        }

        //Interceptador das requisições
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        Gson gson = new GsonBuilder()
                .setLenient()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                .create();

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
                .readTimeout(15, TimeUnit.SECONDS);

        httpClient.addInterceptor(loggingInterceptor);


        //Instancia do retrofit, o interpretador.
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        return retrofit.create(serviceClass);
    }

}
