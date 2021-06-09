package com.business.unknow.services.util.validators;

import com.business.unknow.model.dto.cfdi.ConceptoDto;
import com.business.unknow.model.error.InvoiceManagerException;

public class ConceptoValidator extends AbstractValidator {

  public void validatePostConcepto(ConceptoDto dto) throws InvoiceManagerException {
    checkNotNull(dto.getClaveProdServ(), "Clave Producto servicio");
    checkNotNull(dto.getCantidad(), "Cantidad");
    checkNotNull(dto.getClaveUnidad(), "Clave Unidad");
    checkNotNull(dto.getUnidad(), "Unidad");
    checkNotNull(dto.getDescripcion(), "Descripcion");
    checkNotNull(dto.getValorUnitario(), "getValor Unitario");
    checkNotNull(dto.getImporte(), "Importe");
  }
}
