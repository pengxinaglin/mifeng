package com.haoche51.bee.activity;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import com.haoche51.bee.R;
import com.haoche51.bee.adapter.AutoCompleteAdapter;
import com.haoche51.bee.adapter.SearchAdapter;
import com.haoche51.bee.custom.BeeGridView;
import com.haoche51.bee.entity.HotSearchEntity;
import com.haoche51.bee.entity.SuggestionEntity;
import com.haoche51.bee.net.API;
import com.haoche51.bee.net.GsonParse;
import com.haoche51.bee.net.HttpCallBack;
import com.haoche51.bee.net.ParamsUtil;
import com.haoche51.bee.util.BeeStatistics;
import com.haoche51.bee.util.BeeUtils;
import com.haoche51.bee.util.BeeConstants;
import com.haoche51.bee.util.FilterUtils;
import com.haoche51.bee.util.SpUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SearchActivity extends BeeActivity {

  @BindView(R.id.ac_input) AutoCompleteTextView mAutoTv;
  @BindView(R.id.gv_search_history) BeeGridView mHistoryGv;
  @BindView(R.id.linear_search_history) View mHistoryLayout;
  @BindView(R.id.gv_search_hot) BeeGridView mHotGv;

  private AutoCompleteAdapter mAutoAdapter;
  private SearchAdapter hotAdapter;

  private List<String> mAutoData = new ArrayList<>();

  private List<String> mHistoryData = SpUtils.getSearchHistory();

  private List<String> mHotSearchData = new ArrayList<>();

  private View.OnClickListener listener = new View.OnClickListener() {
    @Override public void onClick(View v) {
      String key = (String) v.getTag();
      BeeStatistics.searchClick("热门" + key);
      doResult(key);
    }
  };

  @Override void initViews() {
    initAdapters();
    initAutoListener();
    requestHotSearchData();
  }

  private void initAdapters() {
    if (BeeUtils.isListEmpty(mHistoryData)) {
      mHistoryLayout.setVisibility(View.GONE);
    } else {
      mHistoryLayout.setVisibility(View.VISIBLE);
    }
    int layoutId = R.layout.gvitem_search;
    SearchAdapter historyAdapter = new SearchAdapter(this, mHistoryData, layoutId, listener);
    mHistoryGv.setAdapter(historyAdapter);

    hotAdapter = new SearchAdapter(this, mHotSearchData, layoutId, listener);
    mHotGv.setAdapter(hotAdapter);

    mAutoAdapter = new AutoCompleteAdapter(this, mAutoData, R.layout.lvitem_search);
    mAutoTv.setAdapter(mAutoAdapter);
    int height = BeeUtils.getScreenHeightPixels() - BeeUtils.getDimenPixels(R.dimen.px_45dp);
    mAutoTv.setDropDownWidth(BeeUtils.getScreenWidthInPixels());
    mAutoTv.setDropDownHeight(height);
    mAutoTv.setDropDownBackgroundResource(R.drawable.bg_search_white);
    mAutoTv.setDropDownHorizontalOffset(BeeUtils.getDimenPixels(R.dimen.px_15dp));
    mAutoTv.setDropDownVerticalOffset(BeeUtils.getDimenPixels(R.dimen.px_7dp));
    mAutoTv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position < mAutoData.size()) {
          String data = mAutoData.get(position);
          BeeStatistics.searchClick("列表" + data);
          doResult(data);
        }
      }
    });

    mAutoTv.postDelayed(new Runnable() {
      @Override public void run() {
        InputMethodManager imm =
            (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
      }
    }, 200);
  }

  private void initAutoListener() {

    mAutoTv.addTextChangedListener(new TextWatcher() {
      @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {
      }

      @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
      }

      @Override public void afterTextChanged(Editable s) {
        if (s != null && mAutoTv != null) {
          String input = s.toString();
          input = input.trim();
          int len = input.length();
          if (len > 0) {
            requestSuggestion(input);
          }
        }
      }
    });
  }

  private void requestSuggestion(String keyword) {
    if (TextUtils.isEmpty(keyword)) return;

    Map<String, Object> params = ParamsUtil.getSuggestion(keyword);
    API.post(params, new HttpCallBack() {
      @Override public void onFinish(String response) {
        if (!TextUtils.isEmpty(response)) {
          handleSuggestion(response);
        }
      }
    });
  }

  private void handleSuggestion(String response) {
    SuggestionEntity suggestion = GsonParse.parseSuggestion(response);
    if (mAutoTv == null) return;
    mAutoData.clear();
    List<String> data = suggestion.getData();
    if (!BeeUtils.isListEmpty(data)) {
      mAutoData.addAll(data);
      mAutoAdapter.notifyDataSetChanged();
    }
  }

  private void requestHotSearchData() {

    Map<String, Object> params = ParamsUtil.getHotSearch();
    API.post(params, new HttpCallBack() {
      @Override public void onFinish(String response) {
        handleHotSearchResp(response);
      }
    });
  }

  private void handleHotSearchResp(String resp) {
    if (TextUtils.isEmpty(resp)) return;
    if (mAutoTv == null) return;

    HotSearchEntity en = GsonParse.parseHotSearch(resp);
    List<String> data = en.getData();
    if (data != null && !data.isEmpty()) {
      mHotSearchData.clear();
      mHotSearchData.addAll(data);
      hotAdapter.notifyDataSetChanged();
    }
  }

  private void doResult(String result) {
    SpUtils.saveHistory(result);
    Intent intent = new Intent();
    intent.putExtra(BeeConstants.KEY_FOR_SEARCH_KEY, result);
    setResult(BeeConstants.REQUEST_CODE_FOR_SEARCH, intent);
    doFinish();
  }

  @Override protected void onPause() {
    super.onPause();
    BeeUtils.hideKeyboard(mAutoTv);
    BeeStatistics.onPageEnd(getClass().getSimpleName());
  }

  private void doFinish() {
    FilterUtils.resetFilterTerm(FilterUtils.ALL);
    finish();
  }

  @Override public boolean dispatchKeyEvent(KeyEvent event) {
    if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
      //点击搜索按钮
      String input = mAutoTv.getText().toString();
      if (!TextUtils.isEmpty(input)) {
        BeeStatistics.searchClick("文本框" + input);
        doResult(input);
      }
      return true;
    }

    return super.dispatchKeyEvent(event);
  }

  @Override void initTitleBar(TextView backTv, TextView titleTv, TextView rightTv) {
    hideTitleBar();
  }

  @Override int getContentViewResourceId() {
    return R.layout.activity_search;
  }

  @OnClick(R.id.tv_search_cancel) public void cancelClick() {
    BeeStatistics.closeClick("搜索");
    finish();
  }

  @Override protected void onResume() {
    super.onResume();
    BeeStatistics.onPageStart(getClass().getSimpleName());
  }
}
