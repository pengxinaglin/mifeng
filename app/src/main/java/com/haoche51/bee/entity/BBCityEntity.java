package com.haoche51.bee.entity;

public class BBCityEntity {

  private int errno;
  private String errmsg;

  private BCityEntity data;

  public void setErrno(int errno) {
    this.errno = errno;
  }

  public void setErrmsg(String errmsg) {
    this.errmsg = errmsg;
  }

  public void setData(BCityEntity data) {
    this.data = data;
  }

  public int getErrno() {
    return errno;
  }

  public String getErrmsg() {
    return errmsg;
  }

  public BCityEntity getData() {
    return data;
  }
}
