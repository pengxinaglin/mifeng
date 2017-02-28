package com.haoche51.bee.dao;

import java.util.List;

public class FilterTerm {

  /**
   * 排序类型
   * 默认排序 refresh_time
   * 车龄 register_time
   * 价格 sell_price
   * 里程 miles
   */
  private String order = "refresh_time";

  /** 排序类型   升序0 降序1 */
  private int sort = 1;

  /** 记录排序描述信息 */
  private String descriptionSort = "智能排序";

  private int brand_id;

  private int class_id;

  private float highPrice = 0; // 价格上限 0 不限

  private float lowPrice = 0;// 价格下限 0 不限

  /** 变速箱类型 0:未知 1:手动 2:自动 3:双离合 4:手自一体 5:无级变速' */
  private int gearboxType = 0; // 变速箱类型 0 不限
  /** 车龄 */
  private int from_year;
  private int to_year;
  /** 里程区间 */
  private int from_miles;
  private int to_miles;
  /** 车身结构 0:未知 1:两厢 2: 三厢 3: SUV' */
  private int structure;
  /** 平台 */
  private List<String> platform;

  public List<String> getPlatform() {
    return platform;
  }

  public void setPlatform(List<String> platform) {
    this.platform = platform;
  }

  public String getDescriptionSort() {
    return descriptionSort;
  }

  public void setDescriptionSort(String descriptionSort) {
    this.descriptionSort = descriptionSort;
  }

  public int getSort() {
    return sort;
  }

  public void setSort(int sort) {
    this.sort = sort;
  }

  public String getOrder() {
    return order;
  }

  public void setOrder(String order) {
    this.order = order;
  }

  public int getFrom_year() {
    return from_year;
  }

  public void setFrom_year(int from_year) {
    this.from_year = from_year;
  }

  public int getTo_year() {
    return to_year;
  }

  public void setTo_year(int to_year) {
    this.to_year = to_year;
  }

  public int getFrom_miles() {
    return from_miles;
  }

  public void setFrom_miles(int from_miles) {
    this.from_miles = from_miles;
  }

  public int getTo_miles() {
    return to_miles;
  }

  public void setTo_miles(int to_miles) {
    this.to_miles = to_miles;
  }

  public int getStructure() {
    return structure;
  }

  public void setStructure(int structure) {
    this.structure = structure;
  }

  public float getHighPrice() {
    return highPrice;
  }

  public void setHighPrice(float highPrice) {
    this.highPrice = highPrice;
  }

  public float getLowPrice() {
    return lowPrice;
  }

  public void setLowPrice(float lowPrice) {
    this.lowPrice = lowPrice;
  }

  public int getGearboxType() {
    return gearboxType;
  }

  public void setGearboxType(int gearboxType) {
    this.gearboxType = gearboxType;
  }

  public int getBrand_id() {
    return brand_id;
  }

  public void setBrand_id(int brand_id) {
    this.brand_id = brand_id;
  }

  public int getClass_id() {
    return class_id;
  }

  public void setClass_id(int class_id) {
    this.class_id = class_id;
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    FilterTerm term = (FilterTerm) o;

    if (sort != term.sort) return false;
    if (brand_id != term.brand_id) return false;
    if (class_id != term.class_id) return false;
    if (Float.compare(term.highPrice, highPrice) != 0) return false;
    if (Float.compare(term.lowPrice, lowPrice) != 0) return false;
    if (gearboxType != term.gearboxType) return false;
    if (from_year != term.from_year) return false;
    if (to_year != term.to_year) return false;
    if (from_miles != term.from_miles) return false;
    if (to_miles != term.to_miles) return false;
    if (structure != term.structure) return false;
    if (order != null ? !order.equals(term.order) : term.order != null) return false;
    if (descriptionSort != null ? !descriptionSort.equals(term.descriptionSort)
        : term.descriptionSort != null) {
      return false;
    }
    return platform != null ? platform.equals(term.platform) : term.platform == null;
  }

  @Override public int hashCode() {
    int result = order != null ? order.hashCode() : 0;
    result = 31 * result + sort;
    result = 31 * result + (descriptionSort != null ? descriptionSort.hashCode() : 0);
    result = 31 * result + brand_id;
    result = 31 * result + class_id;
    result = 31 * result + (highPrice != +0.0f ? Float.floatToIntBits(highPrice) : 0);
    result = 31 * result + (lowPrice != +0.0f ? Float.floatToIntBits(lowPrice) : 0);
    result = 31 * result + gearboxType;
    result = 31 * result + from_year;
    result = 31 * result + to_year;
    result = 31 * result + from_miles;
    result = 31 * result + to_miles;
    result = 31 * result + structure;
    result = 31 * result + (platform != null ? platform.hashCode() : 0);
    return result;
  }
}
