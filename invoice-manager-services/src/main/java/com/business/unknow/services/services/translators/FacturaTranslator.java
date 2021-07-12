package com.business.unknow.services.services.translators;

import com.business.unknow.Constants.FacturaComplemento;
import com.business.unknow.Constants.FacturaConstants;
import com.business.unknow.model.cfdi.CFdiRelacionados;
import com.business.unknow.model.cfdi.Cfdi;
import com.business.unknow.model.cfdi.CfdiRelacionado;
import com.business.unknow.model.cfdi.Complemento;
import com.business.unknow.model.cfdi.ComplementoDocRelacionado;
import com.business.unknow.model.cfdi.ComplementoPago;
import com.business.unknow.model.cfdi.ComplementoPagos;
import com.business.unknow.model.cfdi.Concepto;
import com.business.unknow.model.cfdi.Retencion;
import com.business.unknow.model.cfdi.Translado;
import com.business.unknow.model.context.FacturaContext;
import com.business.unknow.model.dto.FacturaDto;
import com.business.unknow.model.dto.cfdi.CfdiPagoDto;
import com.business.unknow.model.dto.cfdi.ComplementoDto;
import com.business.unknow.model.dto.cfdi.ConceptoDto;
import com.business.unknow.model.dto.cfdi.RelacionadoDto;
import com.business.unknow.model.error.InvoiceCommonException;
import com.business.unknow.model.error.InvoiceManagerException;
import com.business.unknow.services.mapper.factura.FacturaCfdiTranslatorMapper;
import com.business.unknow.services.util.helpers.CdfiHelper;
import com.business.unknow.services.util.helpers.DateHelper;
import com.business.unknow.services.util.helpers.FacturaHelper;
import com.business.unknow.services.util.helpers.SignHelper;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FacturaTranslator {

  @Autowired private CdfiHelper cdfiHelper;

  @Autowired private FacturaHelper facturaHelper;

  @Autowired private DateHelper dateHelper;

  @Autowired private FacturaCfdiTranslatorMapper facturaCfdiTranslatorMapper;

  @Autowired private SignHelper signHelper;

  private static final Logger log = LoggerFactory.getLogger(FacturaTranslator.class);

  public FacturaContext translateFactura(FacturaContext context) throws InvoiceManagerException {
    try {
      Cfdi cfdi =
          facturaCfdiTranslatorMapper.cdfiRootInfo(
              context.getFacturaDto(), context.getEmpresaDto());
      BigDecimal totalImpuestos = new BigDecimal(0);
      BigDecimal totalRetenciones = new BigDecimal(0);
      List<Translado> impuestos = new ArrayList<>();
      List<Retencion> retenciones = new ArrayList<>();
      if (context.getFacturaDto().getCfdi().getRelacionado() != null) {
        RelacionadoDto relacionadoDto = context.getFacturaDto().getCfdi().getRelacionado();
        cfdi.setcFdiRelacionados(new CFdiRelacionados(relacionadoDto.getTipoRelacion()));
        cfdi.getcFdiRelacionados().setCfdiRelacionado(new CfdiRelacionado());
        cfdi.getcFdiRelacionados().getCfdiRelacionado().setUuid(relacionadoDto.getRelacion());
      }
      for (ConceptoDto conceptoDto : context.getFacturaDto().getCfdi().getConceptos()) {
        Concepto concepto = facturaCfdiTranslatorMapper.cfdiConcepto(conceptoDto);
        cfdi.getConceptos().add(concepto);
        if (!conceptoDto.getImpuestos().isEmpty()) {
          totalImpuestos = calculaImpuestos(impuestos, concepto, totalImpuestos);
        }
        if (!conceptoDto.getRetenciones().isEmpty()) {
          totalRetenciones = calculaRetenciones(retenciones, concepto, totalRetenciones);
        }
      }
      if (!impuestos.isEmpty()) {
        cfdi.getImpuestos().setTranslados(impuestos);
      }
      if (!retenciones.isEmpty()) {
        cfdi.getImpuestos().setRetenciones(retenciones);
      }

      if (!totalImpuestos.equals(BigDecimal.ZERO)) {
        cfdi.getImpuestos()
            .setTotalImpuestosTrasladados(totalImpuestos.setScale(2, BigDecimal.ROUND_HALF_UP));
      } else {
        cfdi.getImpuestos().setTotalImpuestosTrasladados(null);
      }

      if (!totalRetenciones.equals(BigDecimal.ZERO)) {
        cfdi.getImpuestos()
            .setTotalImpuestosRetenidos(totalRetenciones.setScale(2, BigDecimal.ROUND_HALF_UP));
      } else {
        cfdi.getImpuestos().setTotalImpuestosRetenidos(null);
      }
      context.setCfdi(cfdi);
      facturaToXmlSigned(context);
      log.debug(context.getXml());
      return context;
    } catch (InvoiceCommonException e) {
      e.printStackTrace();
      throw new InvoiceManagerException(
          "Error generating the xml", e.getMessage(), HttpStatus.SC_CONFLICT);
    }
  }

  public FacturaContext translateComplemento(FacturaContext context)
      throws InvoiceManagerException {
    try {
      Cfdi cfdi =
          facturaCfdiTranslatorMapper.complementoRootInfo(
              context.getFacturaDto().getCfdi(), context.getEmpresaDto());
      for (ConceptoDto concepto : context.getFacturaDto().getCfdi().getConceptos()) {
        cfdi.getConceptos().add(facturaCfdiTranslatorMapper.complementoConcepto(concepto));
      }
      if (context.getFacturaDto().getCfdi().getRelacionado() != null) {
        RelacionadoDto relacionadoDto = context.getFacturaDto().getCfdi().getRelacionado();
        cfdi.setcFdiRelacionados(new CFdiRelacionados(relacionadoDto.getTipoRelacion()));
        cfdi.getcFdiRelacionados().setCfdiRelacionado(new CfdiRelacionado());
        cfdi.getcFdiRelacionados().getCfdiRelacionado().setUuid(relacionadoDto.getRelacion());
      }
      Optional<CfdiPagoDto> primerPago =
          context.getFacturaDto().getCfdi().getComplemento().getPagos().stream().findFirst();
      Complemento complemento = new Complemento();
      ComplementoPagos complementoPagos = new ComplementoPagos();
      ComplementoPago complementoPago = new ComplementoPago();
      complementoPago.setFechaPago(
          dateHelper.getStringFromFecha(
              primerPago.get().getFechaPago(), FacturaConstants.FACTURA_DATE_FORMAT));
      if (!primerPago.get().getMoneda().equals("MXN")) {
        complementoPago.setTipoCambioP(primerPago.get().getTipoCambio());
      }
      complementoPago.setFormaDePago(primerPago.get().getFormaPago());
      complementoPago.setMoneda(primerPago.get().getMoneda());
      complemento.setComplemntoPago(complementoPagos);
      BigDecimal montoTotal = new BigDecimal(0);
      List<ComplementoPago> complementosPago = new ArrayList<>();
      List<ComplementoDocRelacionado> complementosRelacionados = new ArrayList<>();
      complementosPago.add(complementoPago);
      complementoPagos.setComplementoPagos(complementosPago);
      complementoPago.setComplementoDocRelacionado(complementosRelacionados);
      for (CfdiPagoDto cfdiPago : context.getFacturaDto().getCfdi().getComplemento().getPagos()) {
        ComplementoDocRelacionado complementoRelacionado =
            facturaCfdiTranslatorMapper.complementoComponente(cfdiPago);
        if (!cfdiPago.getMoneda().equals(cfdiPago.getMonedaDr())) {
          complementoRelacionado.setTipoCambioDR(cfdiPago.getTipoCambioDr());
        }
        complementosRelacionados.add(complementoRelacionado);
        montoTotal =
            montoTotal.add(
                cfdiPago
                    .getImportePagado()
                    .multiply(cfdiPago.getTipoCambioDr())
                    .setScale(2, BigDecimal.ROUND_DOWN));
      }
      complementoPago.setMonto(montoTotal.toString());
      cfdi.setComplemento(complemento);
      cfdi.setImpuestos(null);
      context.setCfdi(cfdi);
      complementoToXmlSigned(context);
      log.debug(context.getXml());
      return context;
    } catch (InvoiceCommonException e) {
      throw new InvoiceManagerException(
          "Error generating the xml", e.getMessage(), HttpStatus.SC_CONFLICT);
    }
  }

  public void complementoToXmlSigned(FacturaContext context) throws InvoiceCommonException {
    String xml = facturaHelper.facturaCfdiToXml(context.getCfdi());
    log.debug(context.getXml());
    xml = xml.replace(FacturaComplemento.TOTAL, FacturaComplemento.TOTAL_FINAL);
    xml = xml.replace(FacturaComplemento.SUB_TOTAL, FacturaComplemento.SUB_TOTAL_FINAL);
    xml =
        cdfiHelper.changeDate(
            xml,
            dateHelper.isMyDateAfterDaysInPast(context.getFacturaDto().getFechaActualizacion(), 3)
                ? context.getFacturaDto().getFechaActualizacion()
                : new Date());
    String cadenaOriginal = signHelper.getCadena(xml);
    String sello =
        signHelper.getSign(
            cadenaOriginal,
            context.getEmpresaDto().getFiel(),
            context.getEmpresaDto().getLlavePrivada());
    context.setXml(cdfiHelper.putsSign(xml, sello));
    if (context.getFacturaDto().getCfdi().getComplemento() == null) {
      context.getFacturaDto().getCfdi().setComplemento(new ComplementoDto());
    }
  }

  public void facturaToXmlSigned(FacturaContext context) throws InvoiceCommonException {
    String xml = facturaHelper.facturaCfdiToXml(context.getCfdi());
    xml =
        cdfiHelper.changeDate(
            xml,
            dateHelper.isMyDateAfterDaysInPast(context.getFacturaDto().getFechaActualizacion(), 3)
                ? context.getFacturaDto().getFechaActualizacion()
                : new Date());
    String cadenaOriginal = signHelper.getCadena(xml);
    String sello =
        signHelper.getSign(
            cadenaOriginal,
            context.getEmpresaDto().getFiel(),
            context.getEmpresaDto().getLlavePrivada());
    context.setXml(cdfiHelper.putsSign(xml, sello).replace("standalone=\"no\"", ""));
    context.getFacturaDto().getCfdi().setComplemento(new ComplementoDto());
  }

  public BigDecimal calculaImpuestos(
      List<Translado> impuestos, Concepto concepto, BigDecimal totalImpuestos) {
    for (Translado translado : concepto.getImpuestos().getTranslados()) {
      Optional<Translado> tempTranslado =
          impuestos.stream()
              .filter(a -> a.getImpuesto().equals(translado.getImpuesto()))
              .findFirst();
      if (tempTranslado.isPresent()) {
        tempTranslado
            .get()
            .setImporte(tempTranslado.get().getImporte().add(translado.getImporte()));
      } else {
        impuestos.add(
            new Translado(
                translado.getImpuesto(),
                translado.getTipoFactor(),
                translado.getTasaOCuota(),
                translado.getImporte()));
      }
      totalImpuestos = totalImpuestos.add(translado.getImporte());
    }
    return totalImpuestos;
  }

  public BigDecimal calculaRetenciones(FacturaContext context) {
    return calculaRetenciones(context.getFacturaDto());
  }

  public BigDecimal calculaRetenciones(FacturaDto facturaDto) {
    BigDecimal totalRetenciones = new BigDecimal(0);
    List<Retencion> retenciones = new ArrayList<>();
    for (ConceptoDto conceptoDto : facturaDto.getCfdi().getConceptos()) {
      Concepto concepto = facturaCfdiTranslatorMapper.cfdiConcepto(conceptoDto);
      if (!conceptoDto.getRetenciones().isEmpty()) {
        totalRetenciones = calculaRetenciones(retenciones, concepto, totalRetenciones);
      }
    }

    return totalRetenciones;
  }

  public BigDecimal calculaImpuestos(FacturaDto facturaDto) {
    BigDecimal totalImpuestos = new BigDecimal(0);
    List<Translado> traslados = new ArrayList<>();
    for (ConceptoDto conceptoDto : facturaDto.getCfdi().getConceptos()) {
      Concepto concepto = facturaCfdiTranslatorMapper.cfdiConcepto(conceptoDto);
      if (!conceptoDto.getImpuestos().isEmpty()) {
        totalImpuestos = calculaImpuestos(traslados, concepto, totalImpuestos);
      }
    }

    return totalImpuestos;
  }

  public BigDecimal calculaRetenciones(
      List<Retencion> retenciones, Concepto concepto, BigDecimal totalRetenciones) {
    for (Retencion translado : concepto.getImpuestos().getRetenciones()) {
      Optional<Retencion> tempTranslado =
          retenciones.stream()
              .filter(a -> a.getImpuesto().equals(translado.getImpuesto()))
              .findFirst();
      if (tempTranslado.isPresent()) {
        tempTranslado
            .get()
            .setImporte(tempTranslado.get().getImporte().add(translado.getImporte()));
      } else {
        retenciones.add(new Retencion(translado.getImpuesto(), translado.getImporte()));
      }
      totalRetenciones = totalRetenciones.add(translado.getImporte());
    }
    return totalRetenciones;
  }
}
