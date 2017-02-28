package com.haoche51.bee.entity;

public class EventBusEntity {
  private String action;
  private int intValue;
  private String strValue;
  private boolean booleanValue;
  private Object objValue;

  public String getAction() {
    return action;
  }

  public void setAction(String action) {
    this.action = action;
  }

  public Object getObjValue() {
    return objValue;
  }

  public void setObjValue(Object objValue) {
    this.objValue = objValue;
  }

  public boolean isBooleanValue() {
    return booleanValue;
  }

  public void setBooleanValue(boolean booleanValue) {
    this.booleanValue = booleanValue;
  }

  public int getIntValue() {
    return intValue;
  }

  public void setIntValue(int intValue) {
    this.intValue = intValue;
  }

  public String getStrValue() {
    return strValue;
  }

  public void setStrValue(String strValue) {
    this.strValue = strValue;
  }

  private EventBusEntity() {

  }

  public EventBusEntity(String action) {
    this.action = action;
  }

  public EventBusEntity(String action, int intValue) {
    this.action = action;
    this.intValue = intValue;
  }

  public EventBusEntity(String action, boolean booleanValue) {
    this.action = action;
    this.booleanValue = booleanValue;
  }

  public EventBusEntity(String action, Object objValue) {
    this.action = action;
    this.objValue = objValue;
  }

  public EventBusEntity(String action, String strValue) {
    this.action = action;
    this.strValue = strValue;
  }
}
