package com.haoche51.bee.util;

import com.haoche51.bee.dao.Brand;
import java.util.Comparator;

public class ComparatorBrand implements Comparator<Brand> {

  public int compare(Brand o1, Brand o2) {
    if (o1.getSortLetter().equals("@") || o2.getSortLetter().equals("#")) {
      return -1;
    } else if (o1.getSortLetter().equals("#") || o2.getSortLetter().equals("@")) {
      return 1;
    } else {
      return o1.getSortLetter().compareTo(o2.getSortLetter());
    }
  }
}
