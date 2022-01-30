package appcom.example.regsplashscreen.config;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiServerConfig {

    private static ApiServerConfig apiServerInstance;
    private final String API_URL = "http://10.0.2.2:8080/";

    private ApiServerConfig() { }

    public static ApiServerConfig getInstance() {
        if(apiServerInstance == null) {
            apiServerInstance = new ApiServerConfig();
        }
        return apiServerInstance;
    }

    public Retrofit getConfig() {
        return new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

}
