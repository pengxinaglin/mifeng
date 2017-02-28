package com.haoche51.bee.entity;

public class BHomeDataEntity {

  private int errno;
  private String errmsg;

  private HomeDataEntity data;

  public void setErrno(int errno) {
    this.errno = errno;
  }

  public void setErrmsg(String errmsg) {
    this.errmsg = errmsg;
  }

  public void setData(HomeDataEntity data) {
    this.data = data;
  }

  public int getErrno() {
    return errno;
  }

  public String getErrmsg() {
    return errmsg;
  }

  public HomeDataEntity getData() {
    return data;
  }
}
