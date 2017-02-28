package com.haoche51.bee.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import com.haoche51.bee.GlobalData;
import com.haoche51.bee.R;
import com.haoche51.bee.util.BeeStatistics;
import com.haoche51.bee.util.BeeUtils;
import com.haoche51.bee.util.SpUtils;
import java.util.List;

public class VehicleDetailActivity extends BeeActivity {

  @BindView(R.id.web_browser) WebView mWebView;
  @BindView(R.id.linear_net_refresh) View mNetErrLinear;

  private String mLoadURL;
  private static final String INTENT_KEY_ID = "keyForId";
  private int mVehicleSourceId;

  @OnClick(R.id.linear_net_refresh) public void refresh() {
    if (!BeeUtils.isNetAvailable()) {
      mNetErrLinear.setVisibility(View.VISIBLE);
      BeeUtils.toastNetError();
    } else {
      mNetErrLinear.setVisibility(View.GONE);
      mWebView.loadUrl(mLoadURL);
    }
  }

  @OnClick(R.id.iv_back_web_browser) public void back() {
    if (mWebView.canGoBack()) {
      mWebView.goBack();
    } else {
      finish();
    }
  }

  @Override void initViews() {

    Intent intent = getIntent();
    if (intent.hasExtra(INTENT_KEY_ID)) {
      mVehicleSourceId = intent.getIntExtra(INTENT_KEY_ID, -1);
    }
    mLoadURL = BeeUtils.getCarDetailURL(mVehicleSourceId);

    initWebView();

    if (!BeeUtils.isNetAvailable()) {
      mNetErrLinear.setVisibility(View.VISIBLE);
      return;
    }
    mWebView.loadUrl(mLoadURL);
  }

  @SuppressLint("setJavaScriptEnabled") private void initWebView() {
    mWebView.getSettings().setDomStorageEnabled(true);
    mWebView.getSettings().setJavaScriptEnabled(true);
    mWebView.addJavascriptInterface(new JavaScriptInterface(), "BeeJs");
    mWebView.setWebViewClient(new WebViewClient() {
      public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (url.startsWith("tel:")) {
          Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
          startActivity(intent);
        } else {
          mLoadURL = url;
          //当前页面跳转时先判断状态。
          if (!BeeUtils.isNetAvailable()) {
            mNetErrLinear.setVisibility(View.VISIBLE);
          } else {
            mNetErrLinear.setVisibility(View.GONE);
            view.loadUrl(url);
          }
        }
        return true;
      }
    });
  }

  public static void idToThis(Context context, String curId) {
    if (TextUtils.isEmpty(curId) || !TextUtils.isDigitsOnly(curId)) return;
    Intent mIntent = new Intent(GlobalData.mContext, VehicleDetailActivity.class);
    mIntent.putExtra(INTENT_KEY_ID, Integer.parseInt(curId));
    if (context instanceof Activity) {
      context.startActivity(mIntent);
    } else {
      mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      context.startActivity(mIntent);
    }
  }

  @Override void initTitleBar(TextView backTv, TextView titleTv, TextView rightTv) {
    hideTitleBar();
  }

  @Override int getContentViewResourceId() {
    return R.layout.activity_vehicle_detail;
  }

  @Override protected void onResume() {
    super.onResume();
    if (mWebView != null) {
      mWebView.onResume();
    }
    BeeStatistics.onPageStart(getClass().getSimpleName());
  }

  @Override protected void onPause() {
    super.onPause();
    if (mWebView != null) {
      mWebView.onPause();
    }
    BeeStatistics.onPageEnd(getClass().getSimpleName());
  }

  class JavaScriptInterface {
    JavaScriptInterface() {
    }

    /** 存储资讯过的车的所有id,上限100 */
    @SuppressWarnings("unused") @JavascriptInterface public void saveThisVehicle(String id) {
      if (TextUtils.isEmpty(id) || !TextUtils.isDigitsOnly(id)) return;
      List<String> list = SpUtils.getAskVehicleIds();
      if (list.contains(id)) {
        list.remove(id);
      }
      list.add(0, id);
      if (list.size() > 100) {
        list.remove(100);
      }
      SpUtils.setAskVehicleIds(list);
    }
  }
}

