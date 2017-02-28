package com.haoche51.bee.entity;

/**
 * 坐标位置实体
 */
public class LocationEntity {
  private double latitude;
  private double longitude;
  private String city_name;

  public LocationEntity() {

  }

  public LocationEntity(double latitude, double longitude, String city_name) {
    this.latitude = latitude;
    this.longitude = longitude;
    this.city_name = city_name;
  }

  public double getLatitude() {
    return latitude;
  }

  public void setLatitude(double latitude) {
    this.latitude = latitude;
  }

  public double getLongitude() {
    return longitude;
  }

  public void setLongitude(double longitude) {
    this.longitude = longitude;
  }

  public String getCity_name() {
    return city_name;
  }

  public void setCity_name(String city_name) {
    this.city_name = city_name;
  }

  @Override public String toString() {
    return "LocationEntity [latitude=" + latitude + ", longitude=" + longitude + ", city_name="
        + city_name + "]";
  }
}
