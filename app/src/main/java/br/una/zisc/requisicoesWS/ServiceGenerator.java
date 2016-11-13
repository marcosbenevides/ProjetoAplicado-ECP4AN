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
    public static String remoto = "ec2-54-187-15-178.us-west-2.compute.amazonaws.com";
    public static String local = "192.168.1.110";
    public static String API_URL = "http://" + remoto + "/ZISC/res/";
    public static String API_URL_LOCAL = "http://" + local + ":8080/ZISC/res/";

    public static <S> S createService(Class<S> serviceClass){

        //Interceptador das requisições
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        Gson gson = new GsonBuilder()
                .setLenient()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
                .readTimeout(15, TimeUnit.SECONDS);

        httpClient.addInterceptor(loggingInterceptor);



        //Instancia do retrofit, o interpretador.
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL_LOCAL)
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        return retrofit.create(serviceClass);
    }

}
