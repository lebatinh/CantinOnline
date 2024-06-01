package com.example.startopenapp.retrofit;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {
    @FormUrlEncoded
    @POST("{path}")
    Call<String> sendDataToServer(@Path("path") String phpFilePath, @FieldMap Map<String, String> data);
}
