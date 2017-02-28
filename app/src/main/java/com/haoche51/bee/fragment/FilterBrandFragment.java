package com.haoche51.bee.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import com.haoche51.bee.R;
import com.haoche51.bee.adapter.FilterBrandAdapter;
import com.haoche51.bee.adapter.FilterBrandGridViewAdapter;
import com.haoche51.bee.custom.BeeGridView;
import com.haoche51.bee.custom.SideBar;
import com.haoche51.bee.dao.Brand;
import com.haoche51.bee.dao.BrandDAO;
import com.haoche51.bee.dao.FilterTerm;
import com.haoche51.bee.entity.BrandEntity;
import com.haoche51.bee.entity.EventBusEntity;
import com.haoche51.bee.util.BeeStatistics;
import com.haoche51.bee.util.BeeUtils;
import com.haoche51.bee.util.BeeEvent;
import com.haoche51.bee.util.FilterUtils;
import com.haoche51.bee.util.ComparatorBrand;
import com.haoche51.bee.util.SpUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class FilterBrandFragment extends BeeFragment implements View.OnClickListener {

  @BindView(R.id.lv_brand_main) ListView mBrandLv;
  @BindView(R.id.tv_toast_brand) TextView mTvToast;
  @BindView(R.id.side_bar_brand) SideBar mSideBar;
  @BindView(R.id.iv_filter_brand_close) View mCloseIv;
  @BindView(R.id.tv_filter_brand_top_title) TextView mTopTitle;
  @BindView(R.id.v_filter_brand_top_line) View mTopLine;

  private View mHeaderView;
  private TextView mAllBrandTv;
  private ImageView mAllBrandIv;
  private int titleHeight = BeeUtils.getDimenPixels(R.dimen.px_64dp);

  FilterBrandGridViewAdapter gvAdapter;
  FilterBrandAdapter mBrandAdapter;
  List<Brand> mBrandsData = new ArrayList<>();

  private int type;

  public void setType(int type) {
    this.type = type;
  }

  public static FilterBrandFragment newInstance() {
    return new FilterBrandFragment();
  }

  @Override boolean isNeedBindEventBus() {
    return true;
  }

  @Override int getFragmentContentViewResourceId() {
    return R.layout.fragment_filter_brand;
  }

  @Override void doInitViewOrData() {
    if (mSideBar == null) return;
    mCloseIv.setOnClickListener(this);
    mSideBar.setTextView(mTvToast);
    List<BrandEntity> brandEntityList = BrandDAO.getInstance().getAllBrands();
    if (BeeUtils.isListEmpty(brandEntityList)) return;
    List<BrandEntity> hotBrandEntitiesTemp = SpUtils.getHotBrands();
    if (BeeUtils.isListEmpty(hotBrandEntitiesTemp) || hotBrandEntitiesTemp.size() < 10) return;
    List<BrandEntity> hotBrandEntities = hotBrandEntitiesTemp.subList(0, 10);

    List<Brand> bList = new ArrayList<>();
    Brand brand;
    for (BrandEntity brandEntity : brandEntityList) {
      brand = new Brand();
      int brand_id = brandEntity.getBrand_id();
      brand.setBrandId(brand_id);
      brand.setBrandName(brandEntity.getBrand_name());
      brand.setSeries_ids(brandEntity.getSeries());
      brand.setSortLetter(brandEntity.getFirst_char());
      bList.add(brand);
    }

    // 设置headerView必须在setAdapter之前
    if (!hotBrandEntities.isEmpty()) {
      setHeaderView(hotBrandEntities);
    }

    mBrandsData = sortBrand(bList);
    mBrandAdapter = new FilterBrandAdapter(mBrandsData, this, type);
    mBrandLv.setAdapter(mBrandAdapter);

    // 设置右侧触摸监听
    mSideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
      @Override public void onTouchingLetterChanged(String s) {
        if ("热".equals(s)) {
          mBrandLv.smoothScrollToPositionFromTop(0, -titleHeight, 0);
        } else if ("#".equals(s)) {
          int offset = BeeUtils.getDimenPixels(R.dimen.px_55dp);
          mBrandLv.smoothScrollToPositionFromTop(1, offset, 0);
        }
        // 该字母首次出现的位置
        int position = mBrandAdapter.getPositionForSection(s.charAt(0));
        if (position != -1) {
          //因为有headView,所以实际的位置要加1.
          mBrandLv.setSelection(position + 1);
        }
      }
    });
    mBrandLv.setOnScrollListener(new AbsListView.OnScrollListener() {
      @Override public void onScrollStateChanged(AbsListView view, int scrollState) {

      }

      @Override public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
          int totalItemCount) {
        if (titleHeight + mHeaderView.getTop() > 0) {
          mTopTitle.setVisibility(View.GONE);
          mTopLine.setVisibility(View.GONE);
        } else {
          mTopTitle.setVisibility(View.VISIBLE);
          mTopLine.setVisibility(View.VISIBLE);
        }
      }
    });
  }

  private void setHeaderView(List<BrandEntity> entities) {
    mHeaderView =
        LayoutInflater.from(getActivity()).inflate(R.layout.fragment_filter_brand_header, null);
    mAllBrandTv = (TextView) mHeaderView.findViewById(R.id.tv_all_brand);
    mAllBrandIv = (ImageView) mHeaderView.findViewById(R.id.iv_all_brand_choose);
    mHeaderView.findViewById(R.id.rel_all_brand).setOnClickListener(this);
    setAllBrandColor();
    BeeGridView gv = (BeeGridView) mHeaderView.findViewById(R.id.gv_filter_brand_header);
    int resId = R.layout.gvitem_filter_brand;
    gvAdapter = new FilterBrandGridViewAdapter(getActivity(), entities, resId, type);
    gvAdapter.setListener(this);
    gv.setAdapter(gvAdapter);
    mBrandLv.addHeaderView(mHeaderView);
  }

  private void setAllBrandColor() {
    FilterTerm term = FilterUtils.getFilterTerm(type);
    boolean choose = term.getBrand_id() == 0 && term.getClass_id() == 0;
    int mTextColor;
    if (choose) {
      mTextColor = R.color.common_yellow;
      mAllBrandIv.setVisibility(View.VISIBLE);
    } else {
      mTextColor = R.color.common_text_black;
      mAllBrandIv.setVisibility(View.GONE);
    }
    int color = BeeUtils.getResColor(mTextColor);
    mAllBrandTv.setTextColor(color);
  }

  private List<Brand> sortBrand(List<Brand> brands) {
    Collections.sort(brands, new ComparatorBrand());
    return brands;
  }

  @Override public void onClick(View v) {
    int vid = v.getId();
    switch (vid) {
      case R.id.iv_filter_brand_close:
        BeeEvent.postEvent(BeeEvent.ACTION_HIDE_BRAND_FRAGMENT);
        BeeStatistics.closeClick("品牌");
        break;
      case R.id.rel_all_brand:// 全部品牌
        BeeEvent.postEvent(type + BeeEvent.ACTION_BRAND_CHOOSE, null);
        BeeStatistics.brandClick("全部品牌");
        break;

      case R.id.linear_filter_brand_item:// 热门品牌
        BrandEntity brandEntity = (BrandEntity) v.getTag();
        BeeEvent.postEvent(type + BeeEvent.ACTION_BRAND_CHOOSE, brandEntity);
        BeeStatistics.brandClick("热门品牌" + brandEntity.getBrand_name());
        break;

      case R.id.brand_name:// 列表品牌
        if (mBrandLv == null) return;
        BrandEntity entity = (BrandEntity) v.getTag();
        BeeEvent.postEvent(type + BeeEvent.ACTION_BRAND_CHOOSE, entity);
        BeeStatistics.brandClick("列表品牌" + entity.getBrand_name());
        break;
    }
  }

  @OnClick({ R.id.rel_filter_brand_top, R.id.frame_brand_parent }) public void setNull() {
  }

  @SuppressWarnings("unused") @Subscribe(threadMode = ThreadMode.MAIN)
  public void brandChooseChangeEvent(EventBusEntity entity) {
    String action = entity.getAction();
    if (action.equals(type + BeeEvent.ACTION_BRAND_CHOOSE_CHANGE)) {
      setAllBrandColor();
      gvAdapter.notifyDataSetChanged();
      mBrandAdapter.notifyDataSetChanged();
    }
  }
}
