package com.business.unknow.services.util.validators;

import com.business.unknow.Constants;
import com.business.unknow.model.dto.FacturaDto;
import com.business.unknow.model.error.InvoiceManagerException;

public class FacturaValidator extends AbstractValidator {

  public void validatePostFactura(FacturaDto dto) throws InvoiceManagerException {
    checkNotNull(dto.getRfcEmisor(), "Rfc Emisor");
    checkNotNull(dto.getRazonSocialEmisor(), "Razon Social Emisor");
    checkNotNull(dto.getRfcRemitente(), "Rfc Remitente");
    checkNotNull(dto.getRazonSocialRemitente(), "Razon Social Remitente");
  }

  public void validateTimbrado(FacturaDto dto, String folio) throws InvoiceManagerException {
    checkNotNull(dto.getFolio(), "folio ");
    if (!folio.equals(dto.getFolio())) {
      throw new InvoiceManagerException(
          "Error en folio", "Los folios son diferentes", Constants.BAD_REQUEST);
    }
  }

  public void validatePostFacturaWithDetail(FacturaDto dto) throws InvoiceManagerException {
    checkNotNull(dto.getRfcEmisor(), "Rfc Emisor");
    checkNotNull(dto.getRazonSocialEmisor(), "Razon Social Emisor");
    checkNotNull(dto.getRfcRemitente(), "Rfc Remitente");
    checkNotNull(dto.getRazonSocialRemitente(), "Razon Social Remitente");
    checkNotNull(dto.getCfdi(), "cfdi");
    checkNotNull(dto.getCfdi().getReceptor(), "Receptor Info");
    checkNotNull(dto.getCfdi().getEmisor(), "Emisor Info");
    checkNotNull(dto.getCfdi().getReceptor().getRfc(), "RFC receptor");
    checkNotNull(dto.getCfdi().getEmisor().getRfc(), "RFC Emisor");
  }
}
