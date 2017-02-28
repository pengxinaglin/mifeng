package com.haoche51.bee.entity;


import java.util.List;

public class BBrandEntity {
  private int errno;
  private String errmsg;
  private List<BrandEntity> data;

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

  public List<BrandEntity> getData() {
    return data;
  }

  public void setData(List<BrandEntity> data) {
    this.data = data;
  }
}
