package com.business.unknow.services.services.builder;

import com.business.unknow.builder.FacturaContextBuilder;
import com.business.unknow.model.context.FacturaContext;
import com.business.unknow.model.dto.FacturaDto;
import com.business.unknow.model.dto.pagos.PagoDevolucionDto;
import com.business.unknow.model.dto.services.DevolucionDto;
import com.business.unknow.services.entities.Devolucion;
import com.business.unknow.services.entities.PagoFactura;
import com.business.unknow.services.mapper.DevolucionMapper;
import com.business.unknow.services.repositories.PagoFacturaRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DevolucionesBuilderService {

  @Autowired private DevolucionMapper devolucionMapper;

  @Autowired private PagoFacturaRepository pagoFacturaRepository;

  public Devolucion buildDevolucion(
      BigDecimal totalPago,
      String foliofFact,
      Integer idPago,
      BigDecimal montoBase,
      BigDecimal porcentaje,
      String receptor,
      String tipoReceptor) {
    return devolucionMapper.getEntityFromDevolucionDto(
        DevolucionDto.builder()
            .pagoMonto(totalPago)
            .porcentaje(porcentaje)
            .impuesto(montoBase)
            .monto(
                (montoBase
                    .multiply(porcentaje)
                    .divide(new BigDecimal(16), 4, RoundingMode.HALF_UP)))
            .folio(foliofFact)
            .idPagoOrigen(idPago)
            .receptor(receptor)
            .tipoReceptor(tipoReceptor)
            .tipo("D")
            .build());
  }

  public FacturaContext buildContextForDevolucionPue(FacturaDto dto) {
    List<PagoFactura> facturaPago = pagoFacturaRepository.findByFolio(dto.getFolio());
    return new FacturaContextBuilder()
        .setFacturaDto(dto)
        .setIdPago(facturaPago.stream().findFirst().get().getPago().getId())
        .build();
  }

  public FacturaContext buildContextForDevolucionPpd(FacturaDto dto) {
    List<PagoFactura> facturaPago =
        pagoFacturaRepository.findByFolio(
            dto.getCfdi().getComplemento().getPagos().stream().findFirst().get().getFolio());
    return new FacturaContextBuilder()
        .setFacturaDto(dto)
        .setIdPago(
            facturaPago.stream()
                .filter(a -> a.getIdCfdi().equals(dto.getIdCfdi()))
                .findFirst()
                .get()
                .getPago()
                .getId())
        .build();
  }

  public Devolucion buildPagoDevolucion(PagoDevolucionDto dto) {
    return devolucionMapper.getEntityFromDevolucionDto(
        DevolucionDto.builder()
            .monto(dto.getMonto().negate())
            .receptor(dto.getReceptor())
            .tipoReceptor(dto.getTipoReceptor())
            .tipo("C")
            .build());
  }
}
