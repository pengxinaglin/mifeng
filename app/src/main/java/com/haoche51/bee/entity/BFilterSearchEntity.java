package com.haoche51.bee.entity;

public class BFilterSearchEntity {

  private int errno;
  private String errmsg;
  private FilterSearchEntity data;

  public int getErrno() {
    return errno;
  }

  public void setErrno(int errno) {
    this.errno = errno;
  }

  public String getErrmsg() {
    return errmsg;
  }

  public void setErrmsg(String errmsg) {
    this.errmsg = errmsg;
  }

  public FilterSearchEntity getData() {
    return data;
  }

  public void setData(FilterSearchEntity data) {
    this.data = data;
  }
}
