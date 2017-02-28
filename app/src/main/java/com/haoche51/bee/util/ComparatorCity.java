package com.haoche51.bee.util;

import com.haoche51.bee.entity.CityEntity;
import java.util.Comparator;

public class ComparatorCity implements Comparator<CityEntity> {

  public int compare(CityEntity o1, CityEntity o2) {
    if (o1.getFirst_char().equals("@") || o2.getFirst_char().equals("#")) {
      return -1;
    } else if (o1.getFirst_char().equals("#") || o2.getFirst_char().equals("@")) {
      return 1;
    } else {
      return o1.getFirst_char().toUpperCase().compareTo(o2.getFirst_char().toUpperCase());
    }
  }
}
