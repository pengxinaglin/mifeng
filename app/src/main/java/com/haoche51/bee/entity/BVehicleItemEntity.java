package com.haoche51.bee.entity;

import java.util.List;

public class BVehicleItemEntity {

  private String count;
  private List<VehicleItemEntity> vehicles;

  public void setCount(String count) {
    this.count = count;
  }

  public String getCount() {
    return count;
  }

  public List<VehicleItemEntity> getVehicles() {
    return vehicles;
  }

  public void setVehicles(List<VehicleItemEntity> vehicles) {
    this.vehicles = vehicles;
  }
}

