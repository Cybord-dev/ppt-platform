package com.business.unknow.services.services;

import com.business.unknow.builder.FacturaPdfModelDtoBuilder;
import com.business.unknow.enums.*;
import com.business.unknow.model.cfdi.Cfdi;
import com.business.unknow.model.cfdi.ComplementoPago;
import com.business.unknow.model.context.FacturaContext;
import com.business.unknow.model.dto.FacturaDto;
import com.business.unknow.model.dto.FacturaPdfModelDto;
import com.business.unknow.model.dto.files.FacturaFileDto;
import com.business.unknow.model.dto.files.ResourceFileDto;
import com.business.unknow.model.error.InvoiceCommonException;
import com.business.unknow.model.error.InvoiceManagerException;
import com.business.unknow.services.entities.catalogs.FormaPago;
import com.business.unknow.services.entities.catalogs.RegimenFiscal;
import com.business.unknow.services.entities.catalogs.UsoCfdi;
import com.business.unknow.services.mapper.xml.CfdiXmlMapper;
import com.business.unknow.services.services.translators.FacturaTranslator;
import com.business.unknow.services.util.helpers.FacturaHelper;
import com.business.unknow.services.util.helpers.FileHelper;
import com.business.unknow.services.util.helpers.NumberTranslatorHelper;
import com.business.unknow.services.util.pdf.PDFGenerator;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PDFService {

  @Autowired private PDFGenerator pdfGenerator;

  @Autowired private FacturaHelper facturaHelper;

  @Autowired private FileHelper fileHelper;

  @Autowired private CatalogCacheService catalogCacheService;

  @Autowired private CfdiXmlMapper cfdiXmlMapper;

  @Autowired private NumberTranslatorHelper numberTranslatorHelper;

  @Autowired private FacturaTranslator facturaTranslator;

  @Autowired private FilesService filesService;

  private static final Logger log = LoggerFactory.getLogger(PDFService.class);

  public FacturaPdfModelDto getPdfFromFactura(FacturaDto facturaDto, Cfdi cfdi)
      throws InvoiceCommonException, InvoiceManagerException {

    FacturaPdfModelDtoBuilder fBuilder =
        new FacturaPdfModelDtoBuilder().setFactura(getCfdiModelFromFacturaDto(facturaDto));
    try {
      fBuilder.setQr(
          filesService
              .getFacturaFileByFolioAndType(facturaDto.getFolio(), TipoArchivoEnum.QR.name())
              .getData());
    } catch (InvoiceManagerException e) {
      log.info(String.format("%s file for Qr not found", facturaDto.getFolio()));
    }
    ResourceFileDto logotipo = null;
    try {
      logotipo =
          filesService.getResourceFileByResourceReferenceAndType(
              TipoRecursoEnum.EMPRESAS.name(),
              ResourceFileEnum.LOGO.name(),
              facturaDto.getRfcEmisor());

    } catch (InvoiceManagerException ex) {
      log.info(String.format("%s file for Logo not found", facturaDto.getFolio()));
    }
    fBuilder
        .setMetodoPagoDesc(
            MetodosPagoEnum.findByValor(facturaDto.getCfdi().getMetodoPago()).getDescripcion())
        .setLogotipo(logotipo.getData())
        .setTipoDeComprobanteDesc(
            TipoComprobanteEnum.findByValor(facturaDto.getCfdi().getTipoDeComprobante())
                .getDescripcion())
        .setTotalDesc(
            numberTranslatorHelper.getStringNumber(
                facturaDto.getCfdi().getTotal(), facturaDto.getCfdi().getMoneda()))
        .setSubTotalDesc(
            numberTranslatorHelper.getStringNumber(
                facturaDto.getCfdi().getSubtotal(), facturaDto.getCfdi().getMoneda()));

    RegimenFiscal regimenFiscal =
        catalogCacheService
            .getRegimenFiscalPagoMappings()
            .get(facturaDto.getCfdi().getEmisor().getRegimenFiscal());
    UsoCfdi usoCfdi =
        catalogCacheService
            .getUsoCfdiMappings()
            .get(facturaDto.getCfdi().getReceptor().getUsoCfdi());

    fBuilder.setDireccionEmisor(facturaDto.getCfdi().getEmisor().getDireccion());
    fBuilder.setDireccionReceptor(facturaDto.getCfdi().getReceptor().getDireccion());
    if (facturaDto.getTipoDocumento().equals(TipoDocumentoEnum.COMPLEMENTO.getDescripcion())) {
      FormaPago formaPago =
          catalogCacheService
              .getFormaPagoMappings()
              .get(
                  cfdi.getComplemento()
                      .getComplemntoPago()
                      .getComplementoPagos()
                      .get(0)
                      .getFormaDePago());
      fBuilder.setFormaPagoDesc(formaPago == null ? null : formaPago.getDescripcion());
    } else {
      FormaPago formaPago =
          catalogCacheService.getFormaPagoMappings().get(facturaDto.getCfdi().getFormaPago());
      fBuilder.setFormaPagoDesc(formaPago == null ? null : formaPago.getDescripcion());
    }
    fBuilder.setRegimenFiscalDesc(regimenFiscal == null ? null : regimenFiscal.getDescripcion());
    fBuilder.setUsoCfdiDesc(usoCfdi == null ? null : usoCfdi.getDescripcion());

    fBuilder.setCadenaOriginal(facturaDto.getCadenaOriginalTimbrado());

    return fBuilder.build();
  }

  private Cfdi getCfdiModelFromFacturaDto(FacturaDto dto) {
    try {
      // TODO la factura deberia ser recuperada de la BD no del XML
      FacturaFileDto xml =
          filesService.getFacturaFileByFolioAndType(dto.getFolio(), TipoArchivoEnum.XML.name());
      return facturaHelper.getFacturaFromString(fileHelper.stringDecodeBase64(xml.getData()));
    } catch (InvoiceCommonException | InvoiceManagerException e) {
      Cfdi cfdi = cfdiXmlMapper.getEntityFromCfdiDto(dto.getCfdi());
      cfdi.setConceptos(
          dto.getCfdi().getConceptos().stream()
              .map(cfdiXmlMapper::getEntityFromConceptoDto)
              .collect(Collectors.toList()));
      cfdi.setFecha(
          (dto.getFechaCreacion() == null)
              ? new Date().toString()
              : dto.getFechaCreacion().toString());
      return cfdi;
    }
  }

  private Cfdi getCfdiModelFromContext(FacturaContext context) {
    try {
      FacturaFileDto xml =
          context.getFacturaFilesDto().stream()
              .filter(t -> "XML".equalsIgnoreCase(t.getTipoArchivo()))
              .findFirst()
              .get();
      return facturaHelper.getFacturaFromString(fileHelper.stringDecodeBase64(xml.getData()));
    } catch (InvoiceCommonException e) {
      Cfdi cfdi = cfdiXmlMapper.getEntityFromCfdiDto(context.getFacturaDto().getCfdi());
      cfdi.setConceptos(
          context.getFacturaDto().getCfdi().getConceptos().stream()
              .map(cfdiXmlMapper::getEntityFromConceptoDto)
              .collect(Collectors.toList()));
      cfdi.setFecha(
          (context.getFacturaDto().getFechaCreacion() == null)
              ? new Date().toString()
              : context.getFacturaDto().getFechaCreacion().toString());
      cfdi.getImpuestos()
          .setTotalImpuestosTrasladados(cfdi.getTotal().subtract(cfdi.getSubtotal()));
      return cfdi;
    }
  }

  public FacturaFileDto generateInvoicePDF(FacturaDto factura, Cfdi cfdi) {
    try {
      BigDecimal retenciones = facturaTranslator.calculaRetenciones(factura);
      BigDecimal impuestos = facturaTranslator.calculaImpuestos(factura);
      FacturaPdfModelDto model = getPdfFromFactura(factura, cfdi);
      model.getFactura().getImpuestos().setTotalImpuestosRetenidos(retenciones);
      model.getFactura().getImpuestos().setTotalImpuestosTrasladados(impuestos);
      if (factura.getUuid() != null) {
        model.setUuid(factura.getUuid());
      }
      if (factura.getCfdi() != null && factura.getCfdi().getRelacionado() != null) {
        model.setTipoRelacion(
            TipoRelacionEnum.findById(factura.getCfdi().getRelacionado().getTipoRelacion())
                .getValor());
        model.setRelacion(factura.getCfdi().getRelacionado().getRelacion());
      }
      String xmlContent = new FacturaHelper().facturaPdfToXml(model);
      String xslfoTemplate = getXSLFOTemplate(factura);
      InputStreamReader templateReader =
          new InputStreamReader(
              Thread.currentThread()
                  .getContextClassLoader()
                  .getResourceAsStream("pdf-config/" + xslfoTemplate));
      Reader inputReader = new StringReader(xmlContent);
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      pdfGenerator.render(inputReader, outputStream, templateReader);
      filesService.upsertS3File(
          S3BucketsEnum.CFDIS, TipoArchivoEnum.PDF.getFormat(), factura.getFolio(), outputStream);
      String data = Base64.getEncoder().encodeToString(outputStream.toByteArray());
      FacturaFileDto factFile = new FacturaFileDto();
      factFile.setData(data);
      factFile.setFolio(factura.getFolio());
      factFile.setTipoArchivo("PDF");
      log.info("PDF for factura {} was generated successfully", factura.getFolio());
      return factFile;
    } catch (InvoiceCommonException | InvoiceManagerException e) {
      log.error(e.getMessage(), e);
      throw new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR, "The PDF cannot be created");
    }
  }

  public FacturaFileDto generateInvoicePDF(FacturaContext context) {
    try {
      BigDecimal retenciones = facturaTranslator.calculaRetenciones(context);

      FacturaPdfModelDto model = getPdfFromFactura(context.getFacturaDto(), context.getCfdi());
      model.getFactura().getImpuestos().setTotalImpuestosRetenidos(retenciones);
      if (context.getFacturaDto().getUuid() != null) {
        model.setUuid(context.getFacturaDto().getUuid());
      }
      if (context.getFacturaDto().getCfdi() != null
          && context.getFacturaDto().getCfdi().getRelacionado() != null) {
        model.setTipoRelacion(
            TipoRelacionEnum.findById(
                    context.getFacturaDto().getCfdi().getRelacionado().getTipoRelacion())
                .getValor());
        model.setRelacion(context.getFacturaDto().getCfdi().getRelacionado().getRelacion());
      }
      if (context
          .getFacturaDto()
          .getTipoDocumento()
          .equals(TipoDocumentoEnum.COMPLEMENTO.getDescripcion())) {
        BigDecimal montoTotal = new BigDecimal(0);
        Optional<ComplementoPago> compLo =
            context.getCfdi().getComplemento().getComplemntoPago().getComplementoPagos().stream()
                .findFirst();
        if (compLo.isPresent()) {
          model.setFormaPagoDesc(
              FormaPagoEnum.findByPagoClave(compLo.get().getFormaDePago()).getDescripcion());
        }
        for (ComplementoPago complementoPago :
            context.getCfdi().getComplemento().getComplemntoPago().getComplementoPagos()) {
          montoTotal = montoTotal.add(new BigDecimal(complementoPago.getMonto()));
        }
        Optional<ComplementoPago> primerPago =
            context.getCfdi().getComplemento().getComplemntoPago().getComplementoPagos().stream()
                .findFirst();
        model.setMontoTotal(montoTotal);
        model.setTotalDesc(
            numberTranslatorHelper.getStringNumber(montoTotal, primerPago.get().getMoneda()));
      }
      model.setQr(
          context.getFacturaFilesDto().stream()
              .filter(f -> "QR".equalsIgnoreCase(f.getTipoArchivo()))
              .findFirst()
              .get()
              .getData());
      String xmlContent = new FacturaHelper().facturaPdfToXml(model);
      System.out.println(xmlContent);
      String xslfoTemplate = getXSLFOTemplate(context.getFacturaDto());
      InputStreamReader templateReader =
          new InputStreamReader(
              Thread.currentThread()
                  .getContextClassLoader()
                  .getResourceAsStream("pdf-config/" + xslfoTemplate));
      Reader inputReader = new StringReader(xmlContent);
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      pdfGenerator.render(inputReader, outputStream, templateReader);
      filesService.upsertS3File(
          S3BucketsEnum.CFDIS,
          TipoArchivoEnum.PDF.getFormat(),
          context.getFacturaDto().getFolio(),
          outputStream);
      String data = Base64.getEncoder().encodeToString(outputStream.toByteArray());
      FacturaFileDto factFile = new FacturaFileDto();
      factFile.setData(data);
      factFile.setFolio(context.getFacturaDto().getFolio());
      factFile.setTipoArchivo("PDF");
      log.info("PDF for factura {} was generated successfully", context.getFacturaDto().getFolio());
      return factFile;
    } catch (InvoiceCommonException | InvoiceManagerException e) {
      throw new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR, "The PDF cannot be created");
    }
  }

  private String getXSLFOTemplate(FacturaDto facturaDto) {
    if (facturaDto.getTipoDocumento().equals(TipoDocumentoEnum.FACTURA.getDescripcion())
        || facturaDto.getTipoDocumento().equals(TipoDocumentoEnum.NOTA_CREDITO.getDescripcion())) {
      if (facturaDto.getStatusFactura().equals(FacturaStatusEnum.TIMBRADA.getValor())) {
        return "factura-timbrada.xml";
      } else {
        return "factura-sin-timbrar.xml";
      }
    } else if (facturaDto
        .getTipoDocumento()
        .equals(TipoDocumentoEnum.COMPLEMENTO.getDescripcion())) {
      if (facturaDto.getStatusFactura().equals(FacturaStatusEnum.TIMBRADA.getValor())) {
        return "complemento-timbrado.xml";
      } else {
        return "complemento-sin-timbrar.xml";
      }
    } else {
      return "factura-sin-timbrar.xml";
    }
  }
}
