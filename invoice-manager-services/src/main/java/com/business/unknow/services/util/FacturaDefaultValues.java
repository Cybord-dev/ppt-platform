package com.business.unknow.services.util;

import com.business.unknow.enums.FacturaStatusEnum;
import com.business.unknow.enums.PackFacturarionEnum;
import com.business.unknow.model.dto.FacturaDto;
import com.business.unknow.services.util.helpers.FacturaCalculator;
import java.math.BigDecimal;
import java.util.Date;

public class FacturaDefaultValues {

  private final FacturaCalculator facturaCalculator = new FacturaCalculator();

  public void assignaDefaultsFactura(FacturaDto facturaDto, int amount) {
    facturaDto.setSaldoPendiente(facturaDto.getTotal());
    facturaDto.setPackFacturacion(PackFacturarionEnum.NTLINK.name());
    facturaDto
        .getCfdi()
        .setTipoCambio(
            facturaDto.getCfdi().getTipoCambio() == null
                ? BigDecimal.ONE
                : facturaDto.getCfdi().getTipoCambio());
    if (facturaDto.getStatusFactura() == null) {
      facturaDto.setStatusFactura(FacturaStatusEnum.VALIDACION_OPERACIONES.getValor());
    }
    facturaCalculator.assignFolioInFacturaDto(facturaDto);
    facturaCalculator.assignPreFolioInFacturaDto(facturaDto, amount);
  }

  public void assignaDefaultsComplemento(FacturaDto facturaDto, int amount) {
    facturaDto.setFechaCreacion(new Date());
    facturaDto.setFechaActualizacion(new Date());
    facturaDto.setStatusFactura(FacturaStatusEnum.VALIDACION_TESORERIA.getValor());
    facturaCalculator.assignFolioInFacturaDto(facturaDto);
    facturaCalculator.assignPreFolioInFacturaDto(facturaDto, amount);
  }
}
