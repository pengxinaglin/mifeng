package com.haoche51.bee.helper;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.widget.ImageView;
import com.haoche51.bee.R;
import com.haoche51.bee.util.BeeUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class ImageLoaderHelper {

  /** 有memoryCache的option */
  private static DisplayImageOptions normalOpts =
      new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.empty_image)
          .showImageForEmptyUri(R.drawable.empty_image)
          .showImageOnFail(R.drawable.empty_image)
          .cacheInMemory(true)
          .cacheOnDisk(true)
          .considerExifParams(true)
          .build();

  private static DisplayImageOptions noMemoryOpts =
      new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.empty_image)
          .showImageForEmptyUri(R.drawable.empty_image)
          .showImageOnFail(R.drawable.empty_image)
          .cacheInMemory(false)
          .cacheOnDisk(true)
          .considerExifParams(true)
          .build();

  public static void displayNormalImage(String url, ImageView imageView) {
    url = processPre(url, imageView);
    ImageLoader.getInstance().displayImage(url, imageView, normalOpts);
  }

  public static void displayNoMemoryImage(String url, ImageView imageView) {
    if (imageView != null && !TextUtils.isEmpty(url)) {
      url = processPre(url, imageView);
      ImageLoader.getInstance().displayImage(url, imageView, noMemoryOpts);
    }
  }

  // TODO: 17/1/14 是否还要手动拼接图片的url
  private static String processPre(String url, ImageView imageView) {
    if (imageView != null && !TextUtils.isEmpty(url)) {
      int preW = imageView.getLayoutParams().width;
      int preH = imageView.getLayoutParams().height;

      String split = "?imageView2/";
      if (url.contains(split)) {
        split = "\\" + split;
        url = url.split(split)[0];
      }
      if (preW >= 0 && preH >= 0) {
        url = BeeUtils.convertImageURL(url, preW, preH);
      }
    }
    return url;
  }

  private static DisplayImageOptions justDiskCache =
      new DisplayImageOptions.Builder().cacheInMemory(false)
          .cacheOnDisk(true)
          .imageScaleType(ImageScaleType.EXACTLY)
          .bitmapConfig(Bitmap.Config.RGB_565)
          .build();

  /** Banner的option,不进行内存缓存 */
  public static DisplayImageOptions getBannerOptions(int defaultImg) {
    return new DisplayImageOptions.Builder().showImageForEmptyUri(defaultImg)
        .showImageOnFail(R.drawable.empty_image)
        .resetViewBeforeLoading(false)
        .cacheOnDisk(true)
        .cacheInMemory(true)
        .imageScaleType(ImageScaleType.EXACTLY)
        .bitmapConfig(Bitmap.Config.RGB_565)
        .build();
  }

  public static void simpleDisplay(String url, ImageView iv) {

    if (iv == null || TextUtils.isEmpty(url)) return;

    ImageLoader.getInstance().displayImage(url, iv, justDiskCache);
  }

  public static void simpleDisplay(String url, ImageView iv, SimpleImageLoadingListener listener) {
    if (iv == null || TextUtils.isEmpty(url)) return;

    ImageLoader.getInstance().displayImage(url, iv, justDiskCache, listener);
  }
}
