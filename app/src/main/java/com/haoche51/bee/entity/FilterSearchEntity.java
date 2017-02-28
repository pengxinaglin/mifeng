package com.haoche51.bee.entity;

import java.util.List;

public class FilterSearchEntity {

  private String structure;
  private String brand_id;
  private String class_id;
  private String city;
  private List<Integer> es;
  private List<Integer> gear;
  private List<String> price;
  private List<Integer> register_time;
  private List<Double> emission;
  private List<Integer> type;

  public void setStructure(String structure) {
    this.structure = structure;
  }

  public void setBrand_id(String brand_id) {
    this.brand_id = brand_id;
  }

  public void setClass_id(String class_id) {
    this.class_id = class_id;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public void setEs(List<Integer> es) {
    this.es = es;
  }

  public void setGear(List<Integer> gear) {
    this.gear = gear;
  }

  public void setPrice(List<String> price) {
    this.price = price;
  }

  public void setRegister_time(List<Integer> register_time) {
    this.register_time = register_time;
  }

  public void setEmission(List<Double> emission) {
    this.emission = emission;
  }

  public void setType(List<Integer> type) {
    this.type = type;
  }

  public String getStructure() {
    return structure;
  }

  public String getBrand_id() {
    return brand_id;
  }

  public String getClass_id() {
    return class_id;
  }

  public String getCity() {
    return city;
  }

  public List<Integer> getEs() {
    return es;
  }

  public List<Integer> getGear() {
    return gear;
  }

  public List<String> getPrice() {
    return price;
  }

  public List<Integer> getRegister_time() {
    return register_time;
  }

  public List<Double> getEmission() {
    return emission;
  }

  public List<Integer> getType() {
    return type;
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    FilterSearchEntity that = (FilterSearchEntity) o;

    if (structure != null ? !structure.equals(that.structure) : that.structure != null)
      return false;
    if (brand_id != null ? !brand_id.equals(that.brand_id) : that.brand_id != null) return false;
    if (class_id != null ? !class_id.equals(that.class_id) : that.class_id != null) return false;
    if (city != null ? !city.equals(that.city) : that.city != null) return false;
    if (es != null ? !es.equals(that.es) : that.es != null) return false;
    if (gear != null ? !gear.equals(that.gear) : that.gear != null) return false;
    if (price != null ? !price.equals(that.price) : that.price != null) return false;
    if (register_time != null ? !register_time.equals(that.register_time)
        : that.register_time != null) {
      return false;
    }
    if (emission != null ? !emission.equals(that.emission) : that.emission != null) return false;
    return type != null ? type.equals(that.type) : that.type == null;
  }

  @Override public int hashCode() {
    int result = structure != null ? structure.hashCode() : 0;
    result = 31 * result + (brand_id != null ? brand_id.hashCode() : 0);
    result = 31 * result + (class_id != null ? class_id.hashCode() : 0);
    result = 31 * result + (city != null ? city.hashCode() : 0);
    result = 31 * result + (es != null ? es.hashCode() : 0);
    result = 31 * result + (gear != null ? gear.hashCode() : 0);
    result = 31 * result + (price != null ? price.hashCode() : 0);
    result = 31 * result + (register_time != null ? register_time.hashCode() : 0);
    result = 31 * result + (emission != null ? emission.hashCode() : 0);
    result = 31 * result + (type != null ? type.hashCode() : 0);
    return result;
  }
}