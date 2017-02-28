package com.haoche51.bee.net;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.haoche51.bee.BuildConfig;
import com.haoche51.bee.util.BeeUtils;
import com.haoche51.bee.util.BeeLog;
import java.util.HashMap;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class API {

  private static final String APP_TOKEN = "haoche51@572";
  private static final String ACTION_KEY = "action";
  private static final String PARAMS_KEY = "params";
  private static final String MSG_KEY = "msg";
  private static final String TOKEN_KEY = "token";
  private static final String OTHER_KEY = "other";

  private static Retrofit mRetrofit =
      new Retrofit.Builder().addConverterFactory(ScalarsConverterFactory.create())
          .baseUrl(HttpAddress.APP_SERVER)
          .build();

  private static HttpService mService;

  private static HttpService getHttpService() {
    if (mService == null) {
      synchronized (HttpService.class) {
        if (mService == null) {
          mService = mRetrofit.create(HttpService.class);
        }
      }
    }
    return mService;
  }

  private static String getParams(Map<String, Object> params) {
    if (!params.containsKey(ACTION_KEY)) {
      throw new RuntimeException("action not find ");
    }
    //取出action
    String actionValue = (String) params.remove(ACTION_KEY);

    //action params 层
    Map<String, Object> first = new HashMap<>();
    first.put(ACTION_KEY, actionValue);
    first.put(PARAMS_KEY, params);

    //第二层
    Map<String, Object> second = new HashMap<>();
    second.put(MSG_KEY, first);
    second.put(TOKEN_KEY, APP_TOKEN);

    //统计相关
    Map<String, Object> other = new HashMap<>();
    other.put("a_v", BeeUtils.getAppVersionName());
    other.put("s_v", BeeUtils.getOSVersion());
    other.put("r_w", BeeUtils.getScreenWidthInPixels());
    other.put("r_h", BeeUtils.getScreenHeightPixels());

    if (!BuildConfig.DEBUG) {
      second.put(OTHER_KEY, other);
    }

    GsonBuilder gb = new GsonBuilder().disableHtmlEscaping();
    Gson gson = gb.create();
    return gson.toJson(second);
  }

  public static void post(final Map<String, Object> params, final HttpCallBack callBack) {
    final String req = getParams(params);
    try {
      getHttpService().postData(req).enqueue(new Callback<String>() {
        @Override public void onResponse(Call<String> call, Response<String> response) {
          BeeLog.net(HttpAddress.APP_SERVER + "?req=" + req);
          callBack.onFinish(response.body());
        }

        @Override public void onFailure(Call<String> call, Throwable t) {
          BeeLog.net("onFailure ... " + HttpAddress.APP_SERVER + "?req=" + req);
          t.printStackTrace();
          callBack.onFinish("");
        }
      });
    } catch (Exception e) {
      e.printStackTrace();
      callBack.onFinish("");
    }
  }
}
