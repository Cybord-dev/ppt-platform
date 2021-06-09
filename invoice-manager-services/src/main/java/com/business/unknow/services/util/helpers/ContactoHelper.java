package com.business.unknow.services.util.helpers;

import java.math.BigDecimal;

public class ContactoHelper {

  public String translateContacto(String rfc, String promotor, BigDecimal porcentajeContacto) {
    StringBuilder sb = new StringBuilder();
    if (porcentajeContacto.compareTo(BigDecimal.ZERO) > 0) {
      sb.append(rfc).append("_").append(promotor);
      return sb.toString();
    } else {
      return null;
    }
  }
}
