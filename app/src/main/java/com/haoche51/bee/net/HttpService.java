package com.haoche51.bee.net;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

interface HttpService {
  @FormUrlEncoded @POST("/") Call<String> postData(@Field("req") String req);
}
