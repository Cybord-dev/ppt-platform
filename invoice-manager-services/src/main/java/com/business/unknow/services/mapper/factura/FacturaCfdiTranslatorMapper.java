package com.business.unknow.services.mapper.factura;

import com.business.unknow.model.cfdi.Cfdi;
import com.business.unknow.model.cfdi.ComplementoDocRelacionado;
import com.business.unknow.model.cfdi.Concepto;
import com.business.unknow.model.cfdi.Retencion;
import com.business.unknow.model.cfdi.Translado;
import com.business.unknow.model.dto.FacturaDto;
import com.business.unknow.model.dto.cfdi.CfdiDto;
import com.business.unknow.model.dto.cfdi.CfdiPagoDto;
import com.business.unknow.model.dto.cfdi.ConceptoDto;
import com.business.unknow.model.dto.cfdi.ImpuestoDto;
import com.business.unknow.model.dto.cfdi.RetencionDto;
import com.business.unknow.model.dto.services.EmpresaDto;
import com.business.unknow.services.mapper.IgnoreUnmappedMapperConfig;
import com.business.unknow.services.mapper.decorator.FacturaCfdiTranslatorDecorator;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(config = IgnoreUnmappedMapperConfig.class)
@DecoratedWith(FacturaCfdiTranslatorDecorator.class)
public interface FacturaCfdiTranslatorMapper {

  @Mappings({
    @Mapping(source = "facturaDto.folio", target = "folio"),
    @Mapping(source = "facturaDto.cfdi.emisor.rfc", target = "emisor.rfc"),
    @Mapping(source = "facturaDto.cfdi.emisor.nombre", target = "emisor.nombre"),
    @Mapping(source = "empresaDto.regimenFiscal", target = "emisor.regimenFiscal"),
    @Mapping(source = "facturaDto.cfdi.receptor.rfc", target = "receptor.rfc"),
    @Mapping(source = "facturaDto.cfdi.receptor.nombre", target = "receptor.nombre"),
    @Mapping(source = "facturaDto.cfdi.receptor.usoCfdi", target = "receptor.usoCfdi"),
    @Mapping(source = "facturaDto.cfdi.formaPago", target = "formaPago"),
    @Mapping(source = "facturaDto.cfdi.metodoPago", target = "metodoPago"),
    @Mapping(source = "facturaDto.cfdi.tipoCambio", target = "tipoCambio"),
    @Mapping(source = "facturaDto.cfdi.total", target = "total"),
    @Mapping(source = "facturaDto.cfdi.subtotal", target = "subtotal"),
    @Mapping(source = "facturaDto.cfdi.descuento", target = "descuento"),
    @Mapping(source = "facturaDto.cfdi.moneda", target = "moneda"),
    @Mapping(source = "facturaDto.cfdi.tipoDeComprobante", target = "tipoDeComprobante"),
    @Mapping(source = "empresaDto.certificado", target = "certificado"),
    @Mapping(source = "empresaDto.noCertificado", target = "noCertificado"),
    @Mapping(source = "facturaDto.cfdi.sello", target = "sello"),
    @Mapping(source = "empresaDto.cp", target = "lugarExpedicion")
  })
  Cfdi cdfiRootInfo(FacturaDto facturaDto, EmpresaDto empresaDto);

  @Mappings({
    @Mapping(source = "cfdiDto.folio", target = "folio"),
    @Mapping(source = "cfdiDto.emisor.rfc", target = "emisor.rfc"),
    @Mapping(source = "cfdiDto.emisor.nombre", target = "emisor.nombre"),
    @Mapping(source = "cfdiDto.emisor.regimenFiscal", target = "emisor.regimenFiscal"),
    @Mapping(source = "cfdiDto.receptor.rfc", target = "receptor.rfc"),
    @Mapping(source = "cfdiDto.receptor.nombre", target = "receptor.nombre"),
    @Mapping(source = "cfdiDto.receptor.usoCfdi", target = "receptor.usoCfdi"),
    @Mapping(source = "cfdiDto.total", target = "total"),
    @Mapping(source = "cfdiDto.subtotal", target = "subtotal"),
    @Mapping(source = "cfdiDto.moneda", target = "moneda"),
    @Mapping(source = "cfdiDto.tipoCambio", target = "tipoCambio"),
    @Mapping(source = "cfdiDto.tipoDeComprobante", target = "tipoDeComprobante"),
    @Mapping(source = "empresaDto.certificado", target = "certificado"),
    @Mapping(source = "cfdiDto.noCertificado", target = "noCertificado"),
    @Mapping(source = "cfdiDto.sello", target = "sello"),
    @Mapping(source = "cfdiDto.lugarExpedicion", target = "lugarExpedicion"),
    @Mapping(source = "cfdiDto.serie", target = "serie"),
    @Mapping(target = "formaPago", ignore = true),
    @Mapping(target = "metodoPago", ignore = true),
    @Mapping(target = "descuento", ignore = true),
    @Mapping(source = "cfdiDto.version", target = "version"),
    @Mapping(target = "conceptos", ignore = true)
  })
  Cfdi complementoRootInfo(CfdiDto cfdiDto, EmpresaDto empresaDto);

  @Mappings({
    @Mapping(target = "impuestos", ignore = true),
    @Mapping(source = "cantidad", target = "cantidad"),
    @Mapping(source = "claveProdServ", target = "claveProdServ"),
    @Mapping(source = "descripcion", target = "descripcion"),
    @Mapping(source = "claveUnidad", target = "claveUnidad"),
    @Mapping(source = "importe", target = "importe"),
    @Mapping(source = "valorUnitario", target = "valorUnitario")
  })
  Concepto complementoConcepto(ConceptoDto dto);

  @Mappings({
    @Mapping(target = "folio", source = "cfdiPago.folio"),
    @Mapping(target = "idDocumento", source = "cfdiPago.idDocumento"),
    @Mapping(target = "impPagado", source = "cfdiPago.importePagado"),
    @Mapping(target = "impSaldoAnt", source = "cfdiPago.importeSaldoAnterior"),
    @Mapping(target = "impSaldoInsoluto", source = "cfdiPago.importeSaldoInsoluto"),
    @Mapping(target = "metodoDePagoDR", source = "cfdiPago.metodoPago"),
    @Mapping(target = "monedaDR", source = "cfdiPago.monedaDr"),
    @Mapping(target = "numParcialidad", source = "cfdiPago.numeroParcialidad"),
    @Mapping(target = "serie", source = "cfdiPago.serie")
  })
  ComplementoDocRelacionado complementoComponente(CfdiPagoDto cfdiPago);

  @Mappings({@Mapping(target = "impuestos", ignore = true)})
  Concepto cfdiConcepto(ConceptoDto dto);

  Translado cfdiImpuesto(ImpuestoDto dto);

  Retencion cfdiRetencion(RetencionDto dto);
}
