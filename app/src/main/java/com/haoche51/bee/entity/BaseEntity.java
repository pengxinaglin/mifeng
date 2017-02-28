package com.haoche51.bee.entity;

import com.google.gson.Gson;

public abstract class BaseEntity {
  private static Gson gson = null;

  static {
    gson = new Gson();
  }
}

