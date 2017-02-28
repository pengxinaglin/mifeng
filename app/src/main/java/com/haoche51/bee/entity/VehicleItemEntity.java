package com.haoche51.bee.entity;

import java.util.List;

public class VehicleItemEntity {

  /** 公共字段 */
  private String id;
  private String title;
  private String sell_price;
  private String register_time;
  private String gearbox;
  private String miles;
  private String img_url;
  private String cut_price;
  private List<String> platform;

  /** 我的咨询专属字段,用来标识当前车辆状态 */
  private String status;

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getSell_price() {
    return sell_price;
  }

  public void setSell_price(String sell_price) {
    this.sell_price = sell_price;
  }

  public String getRegister_time() {
    return register_time;
  }

  public void setRegister_time(String register_time) {
    this.register_time = register_time;
  }

  public String getGearbox() {
    return gearbox;
  }

  public void setGearbox(String gearbox) {
    this.gearbox = gearbox;
  }

  public String getMiles() {
    return miles;
  }

  public void setMiles(String miles) {
    this.miles = miles;
  }

  public String getImg_url() {
    return img_url;
  }

  public void setImg_url(String img_url) {
    this.img_url = img_url;
  }

  public String getCut_price() {
    return cut_price;
  }

  public void setCut_price(String cut_price) {
    this.cut_price = cut_price;
  }

  public List<String> getPlatform() {
    return platform;
  }

  public void setPlatform(List<String> platform) {
    this.platform = platform;
  }
}
