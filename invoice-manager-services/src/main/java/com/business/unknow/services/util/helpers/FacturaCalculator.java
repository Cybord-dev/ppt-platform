package com.business.unknow.services.util.helpers;

import com.business.unknow.Constants;
import com.business.unknow.model.dto.FacturaDto;
import com.business.unknow.model.error.InvoiceManagerException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FacturaCalculator {

  private final DateHelper dateHelper = new DateHelper();

  public String folioEncrypt(FacturaDto dto) throws InvoiceManagerException {
    SimpleDateFormat dt1 = new SimpleDateFormat(Constants.DATE_STANDAR_FORMAT);
    String cadena =
        String.format(
            "%s|%s|%s",
            dto.getRfcEmisor(), dto.getRfcRemitente(), dt1.format(dto.getFechaCreacion()));
    try {
      MessageDigest md = MessageDigest.getInstance("MD5");
      byte[] bytes = md.digest(cadena.getBytes());
      StringBuilder sb = new StringBuilder();
      for (byte aByte : bytes) {
        sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
      }
      return sb.toString();
    } catch (NoSuchAlgorithmException e) {
      throw new InvoiceManagerException(
          e.getMessage(), e.getCause().toString(), Constants.INTERNAL_ERROR);
    }
  }

  public void assignFolioInFacturaDto(FacturaDto dto) {
    String folio = dateHelper.getStringFromFecha(new Date(), Constants.DATE_FOLIO_GENERIC_FORMAT);
    dto.setFolio(folio);
    if (dto.getCfdi() != null) {
      dto.getCfdi().setFolio(folio);
    }
  }

  public void assignPreFolioInFacturaDto(FacturaDto dto, int amount) {
    String amountWithZeros = String.format("%05d", amount + 1);
    String folio =
        String.format(
            "%s-%s",
            dateHelper.getStringFromFecha(new Date(), Constants.DATE_PRE_FOLIO_GENERIC_FORMAT),
            amountWithZeros);
    dto.setPreFolio(folio);
  }
}
