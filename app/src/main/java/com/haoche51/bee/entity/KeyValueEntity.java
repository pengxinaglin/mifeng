package com.haoche51.bee.entity;

import com.haoche51.bee.util.BeeConstants;

public class KeyValueEntity {
  private String key;
  private String value = BeeConstants.UNLIMITED;

  public KeyValueEntity(String key) {
    this.key = key;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  @Override public String toString() {
    return "KeyValueEntity [key=" + key + ", value=" + value + "]";
  }
}
