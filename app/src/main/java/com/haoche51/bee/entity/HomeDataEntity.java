package com.haoche51.bee.entity;

import java.util.List;

public class HomeDataEntity {

  private int city_count;
  private int today_count;
  private List<Integer> hot_brand;

  public int getCity_count() {
    return city_count;
  }

  public void setCity_count(int city_count) {
    this.city_count = city_count;
  }

  public int getToday_count() {
    return today_count;
  }

  public void setToday_count(int today_count) {
    this.today_count = today_count;
  }

  public List<Integer> getHot_brand() {
    return hot_brand;
  }

  public void setHot_brand(List<Integer> hot_brand) {
    this.hot_brand = hot_brand;
  }
}
