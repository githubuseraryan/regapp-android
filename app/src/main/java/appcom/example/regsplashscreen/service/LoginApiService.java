package appcom.example.regsplashscreen.service;

import appcom.example.regsplashscreen.model.request.LoginApiRequestModel;
import appcom.example.regsplashscreen.model.response.LoginApiResponseModel;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface LoginApiService {

    @POST("/regapp/login")
    Call<LoginApiResponseModel> getLoginResponse(@Body LoginApiRequestModel loginApiRequestModel);
}
