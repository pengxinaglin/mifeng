package com.haoche51.bee.entity;

import java.util.List;

public class SuggestionEntity {

  private int errno;
  private List<String> data;

  public void setErrno(int errno) {
    this.errno = errno;
  }

  public void setData(List<String> data) {
    this.data = data;
  }

  public int getErrno() {
    return errno;
  }

  public List<String> getData() {
    return data;
  }
}
