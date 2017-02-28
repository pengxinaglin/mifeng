package com.haoche51.bee.util;

import android.support.v4.app.Fragment;
import com.haoche51.bee.fragment.HomePageFragment;
import com.haoche51.bee.fragment.ProfileFragment;
import com.haoche51.bee.fragment.VehicleListFragment;

public class FragmentController {

  /** 首页 */
  public static final String HOME_TAG = "home_page";
  /** 买车 */
  public static final String CHOOSE_TAG = "vehicle_list_page";
  /** 我的 */
  public static final String PROFILE_TAG = "profile_page";

  public static Fragment newInstance(String tag) {
    Fragment fragment = null;
    switch (tag) {
      case HOME_TAG:
        fragment = new HomePageFragment();
        break;
      case CHOOSE_TAG:
        fragment = new VehicleListFragment();
        break;
      case PROFILE_TAG:
        fragment = new ProfileFragment();
        break;
    }
    return fragment;
  }
}
