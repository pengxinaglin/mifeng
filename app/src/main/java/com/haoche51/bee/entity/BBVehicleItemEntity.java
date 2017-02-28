package com.haoche51.bee.entity;

public class BBVehicleItemEntity {

  private int errno;
  private String errmsg;
  private BVehicleItemEntity data;

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

  public BVehicleItemEntity getData() {
    return data;
  }

  public void setData(BVehicleItemEntity data) {
    this.data = data;
  }
}
