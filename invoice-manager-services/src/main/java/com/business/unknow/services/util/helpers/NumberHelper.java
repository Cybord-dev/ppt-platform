package com.business.unknow.services.util.helpers;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class NumberHelper {

  public Double assignPrecision(Double number, int scale) {
    BigDecimal tempBig = new BigDecimal(Double.toString(number));
    return tempBig.setScale(scale, RoundingMode.HALF_EVEN).doubleValue();
  }
}
