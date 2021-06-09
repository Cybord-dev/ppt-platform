package com.business.unknow.services.services.executor;

import com.business.unknow.enums.ContactoDevolucionEnum;
import com.business.unknow.model.context.FacturaContext;
import com.business.unknow.model.dto.FacturaDto;
import com.business.unknow.model.dto.cfdi.CfdiDto;
import com.business.unknow.model.dto.cfdi.CfdiPagoDto;
import com.business.unknow.services.entities.Client;
import com.business.unknow.services.entities.Devolucion;
import com.business.unknow.services.repositories.facturas.DevolucionRepository;
import com.business.unknow.services.services.FacturaService;
import com.business.unknow.services.services.builder.DevolucionesBuilderService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DevolucionExecutorService {

  @Autowired private DevolucionRepository devolucionRepository;

  @Autowired private DevolucionesBuilderService devolucionesBuilderService;

  @Autowired private FacturaService facturaService;

  public void executeDevolucionForPue(FacturaContext context, Client client) {
    BigDecimal baseComisiones = calculaImpuestos(context.getFacturaDto().getCfdi());
    BigDecimal realSubtotal = calculaImporteBaseFactura(context.getFacturaDto().getCfdi());
    BigDecimal total = context.getFacturaDto().getCfdi().getTotal();
    devolucionRepository.save(
        devolucionesBuilderService.buildDevolucion(
            total,
            context.getFacturaDto().getFolio(),
            context.getIdPago(),
            baseComisiones,
            client.getPorcentajePromotor(),
            client.getCorreoPromotor(),
            ContactoDevolucionEnum.PROMOTOR.name()));
    devolucionRepository.save(
        devolucionesBuilderService.buildDevolucion(
            total,
            context.getFacturaDto().getFolio(),
            context.getIdPago(),
            baseComisiones,
            client.getPorcentajeDespacho(),
            "invoice.manager.sj@gmail.com",
            ContactoDevolucionEnum.DESPACHO.name()));
    if (client.getPorcentajeCliente().compareTo(BigDecimal.ZERO) > 0) {
      Devolucion devolucion =
          devolucionesBuilderService.buildDevolucion(
              total,
              context.getFacturaDto().getFolio(),
              context.getIdPago(),
              baseComisiones,
              client.getPorcentajeCliente(),
              client.getInformacionFiscal().getRfc(),
              ContactoDevolucionEnum.CLIENTE.name());
      devolucion.setMonto(realSubtotal.add(devolucion.getMonto()));
      devolucionRepository.save(devolucion);
    } else {
      Devolucion devolucion =
          devolucionesBuilderService.buildDevolucion(
              total,
              context.getFacturaDto().getFolio(),
              context.getIdPago(),
              baseComisiones,
              client.getPorcentajeCliente(),
              client.getInformacionFiscal().getRfc(),
              ContactoDevolucionEnum.CLIENTE.name());
      devolucion.setMonto(realSubtotal);
      devolucionRepository.save(devolucion);
    }
    if (client.getPorcentajeContacto().compareTo(BigDecimal.ZERO) > 0) {
      devolucionRepository.save(
          devolucionesBuilderService.buildDevolucion(
              total,
              context.getFacturaDto().getFolio(),
              context.getIdPago(),
              baseComisiones,
              client.getPorcentajeContacto(),
              client.getCorreoContacto(),
              ContactoDevolucionEnum.CONTACTO.name()));
    }
  }

  public void executeDevolucionForPpd(FacturaContext context, Client client) {
    for (CfdiPagoDto pagoDto : context.getFacturaDto().getCfdi().getComplemento().getPagos()) {
      FacturaDto facturaDto = facturaService.getFacturaByFolio(pagoDto.getFolio());
      BigDecimal impuestos = calculaImpuestos(facturaDto.getCfdi());
      BigDecimal baseComisiones = impuestos.divide(facturaDto.getTotal(), 6, RoundingMode.HALF_UP);
      devolucionRepository.save(
          devolucionesBuilderService.buildDevolucion(
              pagoDto.getImportePagado(),
              facturaDto.getFolio(),
              context.getIdPago(),
              pagoDto.getImportePagado().multiply(baseComisiones),
              client.getPorcentajePromotor(),
              client.getCorreoPromotor(),
              ContactoDevolucionEnum.PROMOTOR.name()));
      devolucionRepository.save(
          devolucionesBuilderService.buildDevolucion(
              pagoDto.getImportePagado(),
              facturaDto.getFolio(),
              context.getIdPago(),
              pagoDto.getImportePagado().multiply(baseComisiones),
              client.getPorcentajeDespacho(),
              "invoice.manager.sj@gmail.com",
              ContactoDevolucionEnum.DESPACHO.name()));
      if (client.getPorcentajeCliente().compareTo(BigDecimal.ZERO) > 0) {
        Devolucion devolucion =
            devolucionesBuilderService.buildDevolucion(
                pagoDto.getImportePagado(),
                facturaDto.getFolio(),
                context.getIdPago(),
                pagoDto.getImportePagado().multiply(baseComisiones),
                client.getPorcentajeCliente(),
                client.getInformacionFiscal().getRfc(),
                ContactoDevolucionEnum.CLIENTE.name());
        devolucion.setMonto(
            pagoDto
                .getImportePagado()
                .subtract(pagoDto.getImportePagado().multiply(baseComisiones))
                .add(devolucion.getMonto()));
        devolucionRepository.save(devolucion);
      } else {
        Devolucion devolucion =
            devolucionesBuilderService.buildDevolucion(
                pagoDto.getImportePagado(),
                facturaDto.getFolio(),
                context.getIdPago(),
                pagoDto.getImportePagado().multiply(baseComisiones),
                client.getPorcentajeCliente(),
                client.getInformacionFiscal().getRfc(),
                ContactoDevolucionEnum.CLIENTE.name());
        devolucion.setMonto(
            pagoDto
                .getImportePagado()
                .subtract(pagoDto.getImportePagado().multiply(baseComisiones)));
        devolucionRepository.save(devolucion);
      }
      if (client.getPorcentajeContacto().compareTo(BigDecimal.ZERO) > 0) {
        devolucionRepository.save(
            devolucionesBuilderService.buildDevolucion(
                pagoDto.getImportePagado(),
                facturaDto.getFolio(),
                context.getIdPago(),
                pagoDto.getImportePagado().multiply(baseComisiones),
                client.getPorcentajeContacto(),
                client.getCorreoContacto(),
                ContactoDevolucionEnum.CONTACTO.name()));
      }
    }
  }

  public BigDecimal calculaImporteBaseFactura(CfdiDto cfdiDto) {
    BigDecimal subtotal =
        cfdiDto.getConceptos().stream()
            .map(c -> c.getImporte())
            .reduce(BigDecimal.ZERO, (i1, i2) -> i1.add(i2))
            .setScale(2, RoundingMode.HALF_UP);
    BigDecimal retenciones =
        cfdiDto.getConceptos().stream()
            .map(
                i ->
                    i.getRetenciones().stream()
                        .map(imp -> imp.getImporte())
                        .reduce(BigDecimal.ZERO, (i1, i2) -> i1.add(i2)))
            .reduce(BigDecimal.ZERO, (i1, i2) -> i1.add(i2))
            .setScale(2, RoundingMode.HALF_UP);
    return subtotal.subtract(retenciones);
  }

  public BigDecimal calculaImpuestos(CfdiDto cfdiDto) {
    return cfdiDto.getConceptos().stream()
        .map(
            i ->
                i.getImpuestos().stream()
                    .map(imp -> imp.getImporte())
                    .reduce(
                        BigDecimal.ZERO,
                        (i1, i2) -> i1.add(i2))) // suma importe impuestos por concepto
        .reduce(BigDecimal.ZERO, (i1, i2) -> i1.add(i2))
        .setScale(2, RoundingMode.HALF_UP);
  }
}
