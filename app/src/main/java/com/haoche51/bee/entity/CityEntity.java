package com.haoche51.bee.entity;

public class CityEntity extends BaseEntity {
  private int city_id;
  private String city_name;
  private String first_char;

  public CityEntity() {

  }

  public CityEntity(int city_id, String city_name) {
    this.city_id = city_id;
    this.city_name = city_name;
  }

  public int getCity_id() {
    return city_id;
  }

  public void setCity_id(int city_id) {
    this.city_id = city_id;
  }

  public String getCity_name() {
    return city_name;
  }

  public void setCity_name(String city_name) {
    this.city_name = city_name;
  }

  public String getFirst_char() {
    return first_char;
  }

  public void setFirst_char(String first_char) {
    this.first_char = first_char;
  }

  @Override public String toString() {
    return "CityEntity{" +
        "city_id=" + city_id +
        ", city_name='" + city_name + '\'' +
        ", first_char='" + first_char + '\'' +
        '}';
  }
}
