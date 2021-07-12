package com.business.unknow.services.services.builder;

import com.business.unknow.Constants;
import com.business.unknow.Constants.ComplementoPpdDefaults;
import com.business.unknow.builder.CfdiComplementoPagoBuilder;
import com.business.unknow.builder.CfdiDtoBuilder;
import com.business.unknow.builder.ConceptoDtoBuilder;
import com.business.unknow.builder.FacturaBuilder;
import com.business.unknow.builder.FacturaContextBuilder;
import com.business.unknow.enums.FormaPagoEnum;
import com.business.unknow.enums.TipoArchivoEnum;
import com.business.unknow.enums.TipoDocumentoEnum;
import com.business.unknow.model.context.FacturaContext;
import com.business.unknow.model.dto.FacturaDto;
import com.business.unknow.model.dto.cfdi.CfdiDto;
import com.business.unknow.model.dto.cfdi.CfdiPagoDto;
import com.business.unknow.model.dto.cfdi.ComplementoDto;
import com.business.unknow.model.dto.cfdi.ConceptoDto;
import com.business.unknow.model.dto.cfdi.EmisorDto;
import com.business.unknow.model.dto.cfdi.ReceptorDto;
import com.business.unknow.model.dto.files.FacturaFileDto;
import com.business.unknow.model.dto.pagos.PagoDto;
import com.business.unknow.model.dto.pagos.PagoFacturaDto;
import com.business.unknow.model.dto.services.EmpresaDto;
import com.business.unknow.model.error.InvoiceManagerException;
import com.business.unknow.services.entities.Contribuyente;
import com.business.unknow.services.entities.Empresa;
import com.business.unknow.services.entities.cfdi.Cfdi;
import com.business.unknow.services.entities.cfdi.CfdiPago;
import com.business.unknow.services.mapper.ContribuyenteMapper;
import com.business.unknow.services.mapper.EmpresaMapper;
import com.business.unknow.services.repositories.ContribuyenteRepository;
import com.business.unknow.services.repositories.EmpresaRepository;
import com.business.unknow.services.repositories.facturas.CfdiPagoRepository;
import com.business.unknow.services.repositories.facturas.CfdiRepository;
import com.business.unknow.services.services.FilesService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FacturaBuilderService extends AbstractBuilderService {

  @Autowired private EmpresaRepository empresaRepository;

  @Autowired private ContribuyenteRepository contribuyenteRepository;

  @Autowired private CfdiPagoRepository cfdiPagoRepository;

  @Autowired private CfdiRepository cfdiRepository;

  @Autowired private EmpresaMapper empresaMapper;

  @Autowired private ContribuyenteMapper contribuyenteMapper;

  @Autowired private FilesService filesService;

  public FacturaContext buildFacturaContextPagoPpdCreation(
      PagoDto pagoDto, FacturaDto facturaDto, String folio) throws InvoiceManagerException {
    EmpresaDto empresaDto =
        empresaMapper.getEmpresaDtoFromEntity(
            empresaRepository
                .findByRfc(facturaDto.getRfcEmisor())
                .orElseThrow(
                    () ->
                        new InvoiceManagerException(
                            "Pago a credito no encontrado",
                            String.format("No existe El emisor %s", facturaDto.getRfcEmisor()),
                            HttpStatus.SC_NOT_FOUND)));
    getEmpresaFiles(empresaDto, facturaDto);
    return new FacturaContextBuilder()
        .setEmpresaDto(empresaDto)
        .setFacturaDto(facturaDto)
        .setCurrentPago(pagoDto)
        .build();
  }

  public FacturaContext buildFacturaContextPagoPueCreation(String folio, PagoDto pagoDto) {
    // List<Pago> pagos = pagoRepository.findByFolio(folio);
    //		Optional<Factura> factura = repository.findByFolio(folio);
    //		Optional<Pago> pagoCredito = pagos.stream()
    //				.filter(p -> p.getFormaPago().equals(FormaPagoEnum.CREDITO.getPagoValue())).findFirst();
    //		return new FacturaContextBuilder().setPagos(Arrays.asList(pagoDto))
    //				.setPagos(pagoMapper.getPagosDtoFromEntities(pagos)).setCurrentPago(pagoDto)
    //				.setFacturaDto(factura.isPresent() ? mapper.getFacturaDtoFromEntity(factura.get()) : null)
    //				.setPagoCredito(pagoCredito.isPresent() ?
    // pagoMapper.getPagoDtoFromEntity(pagoCredito.get()) : null)
    //				.build();
    return null;
  }

  public FacturaDto buildFacturaDtoPagoPpdCreation(FacturaDto factura, PagoDto pago) {
    return new FacturaBuilder()
        .setTotal(pago.getMonto())
        .setPackFacturacion(factura.getPackFacturacion())
        .setSaldoPendiente(BigDecimal.ZERO)
        .setLineaEmisor(factura.getLineaEmisor())
        .setRfcEmisor(factura.getRfcEmisor())
        .setMetodoPago(ComplementoPpdDefaults.METODO_PAGO)
        .setRfcRemitente(factura.getRfcRemitente())
        .setLineaRemitente(factura.getLineaRemitente())
        .setRazonSocialEmisor(factura.getRazonSocialEmisor())
        .setRazonSocialRemitente(factura.getRazonSocialRemitente())
        .setValidacionTeso(false)
        .setValidacionOper(false)
        .setSolicitante(factura.getSolicitante())
        .setTipoDocumento(TipoDocumentoEnum.COMPLEMENTO.getDescripcion())
        .build();
  }

  public CfdiDto buildFacturaComplementoCreation(FacturaContext facturaContext) {
    CfdiDtoBuilder cfdiBuilder =
        new CfdiDtoBuilder()
            .setVersion(ComplementoPpdDefaults.VERSION_CFDI)
            .setLugarExpedicion(facturaContext.getEmpresaDto().getCp())
            .setMoneda(ComplementoPpdDefaults.MONEDA)
            .setMetodoPago(ComplementoPpdDefaults.METODO_PAGO)
            .setFormaPago(
                FormaPagoEnum.findByPagoValue(facturaContext.getCurrentPago().getFormaPago())
                    .getClave())
            .setNoCertificado(facturaContext.getEmpresaDto().getNoCertificado())
            .setSerie(ComplementoPpdDefaults.SERIE)
            .setSubtotal(new BigDecimal(ComplementoPpdDefaults.SUB_TOTAL))
            .setTotal(new BigDecimal(ComplementoPpdDefaults.TOTAL))
            .setComplemento(new ComplementoDto())
            .setTipoDeComprobante(ComplementoPpdDefaults.COMPROBANTE)
            .setEmisor(
                new EmisorDto(
                    facturaContext.getFacturaDto().getRfcEmisor(),
                    facturaContext.getFacturaDto().getRazonSocialEmisor(),
                    facturaContext.getFacturaDto().getCfdi().getEmisor().getRegimenFiscal(),
                    facturaContext.getFacturaDto().getCfdi().getEmisor().getDireccion()))
            .setReceptor(
                new ReceptorDto(
                    facturaContext.getFacturaDto().getRfcRemitente(),
                    facturaContext.getFacturaDto().getRazonSocialRemitente(),
                    ComplementoPpdDefaults.USO_CFDI,
                    facturaContext.getFacturaDto().getCfdi().getReceptor().getDireccion()))
            .setConceptos(buildFacturaComplementoConceptos());
    return cfdiBuilder.build();
  }

  public List<ConceptoDto> buildFacturaComplementoConceptos() {
    List<ConceptoDto> conceptos = new ArrayList<ConceptoDto>();
    ConceptoDtoBuilder conceptoBuilder =
        new ConceptoDtoBuilder()
            .setCantidad(new BigDecimal(ComplementoPpdDefaults.CANTIDAD))
            .setClaveProdServ(ComplementoPpdDefaults.CLAVE_PROD)
            .setClaveUnidad(ComplementoPpdDefaults.CLAVE)
            .setDescripcion(ComplementoPpdDefaults.DESCRIPCION)
            .setImporte(new BigDecimal(ComplementoPpdDefaults.IMPORTE))
            .setValorUnitario(new BigDecimal(ComplementoPpdDefaults.VALOR_UNITARIO));
    conceptos.add(conceptoBuilder.build());
    return conceptos;
  }

  public List<CfdiPagoDto> buildFacturaComplementoPagos(
      FacturaDto complemento, PagoDto pagoDto, List<FacturaDto> dtos)
      throws InvoiceManagerException {
    List<CfdiPagoDto> cfdiPagos = new ArrayList<CfdiPagoDto>();
    for (FacturaDto dto : dtos) {
      List<CfdiPago> cfdiPAgos =
          cfdiPagoRepository.findByFolio(dto.getFolio()).stream()
              .filter(a -> a.getValido())
              .collect(Collectors.toList());
      Optional<Cfdi> cfdi = cfdiRepository.findById(dto.getIdCfdi());
      Optional<PagoFacturaDto> pagoFactura =
          pagoDto.getFacturas().stream()
              .filter(a -> a.getFolio().endsWith(dto.getFolio()))
              .findFirst();
      Optional<CfdiPago> cfdipago =
          cfdiPAgos.stream()
              .sorted(
                  (o2, o1) ->
                      Integer.valueOf(o1.getNumeroParcialidad())
                          .compareTo(Integer.valueOf(o2.getNumeroParcialidad())))
              .filter(a -> a.getFolio().endsWith(dto.getFolio()))
              .findFirst();

      if (!cfdipago.isPresent()) {
        cfdipago = Optional.of(new CfdiPago(pagoDto.getMonto(), 0));
      }
      if (pagoFactura.isPresent()) {
        BigDecimal montoPagado;
        if (cfdi.get().getMoneda().equals(pagoDto.getMoneda())) {
          montoPagado = pagoFactura.get().getMonto();
        } else {
          montoPagado =
              pagoFactura
                  .get()
                  .getMonto()
                  .divide(pagoDto.getTipoDeCambio(), 2, RoundingMode.HALF_UP);
        }
        CfdiComplementoPagoBuilder cfdiComplementoPagoBuilder =
            new CfdiComplementoPagoBuilder()
                .setVersion(ComplementoPpdDefaults.VERSION)
                .setFechaPago(pagoDto.getFechaPago())
                .setFormaPago(FormaPagoEnum.findByPagoValue(pagoDto.getFormaPago()).getClave())
                .setMoneda(pagoDto.getMoneda())
                .setMonto(pagoDto.getMonto())
                .setFolio(dto.getFolio())
                .setIdDocumento(dto.getUuid())
                .setImportePagado(montoPagado)
                .setMonedaDr(cfdi.get().getMoneda())
                .setMoneda(pagoDto.getMoneda())
                .setValido(true)
                .setMetodoPago(ComplementoPpdDefaults.METODO_PAGO)
                .setSerie(ComplementoPpdDefaults.SERIE_PAGO)
                .setNumeroParcialidad(cfdipago.get().getNumeroParcialidad() + 1)
                .setImporteSaldoAnterior(dto.getSaldoPendiente())
                .setTipoCambio(
                    cfdi.get().getMoneda().equals(pagoDto.getMoneda())
                        ? pagoDto.getTipoDeCambio()
                        : new BigDecimal(1))
                .setTipoCambioDr(
                    !cfdi.get().getMoneda().equals(pagoDto.getMoneda())
                        ? pagoDto.getTipoDeCambio()
                        : new BigDecimal(1))
                .setImporteSaldoInsoluto(dto.getSaldoPendiente().subtract(montoPagado));
        cfdiPagos.add(cfdiComplementoPagoBuilder.build());
      } else {
        throw new InvoiceManagerException(
            "No tiene relacion de pago la factura",
            String.format("La factura %s not tiene pago relacionado", dto.getFolio()),
            Constants.BAD_REQUEST);
      }
    }
    return cfdiPagos;
  }

  public FacturaContext buildFacturaContextCreateFactura(FacturaDto facturaDto)
      throws InvoiceManagerException {
    Empresa empresa =
        empresaRepository
            .findByRfc(facturaDto.getRfcEmisor())
            .orElseThrow(
                () ->
                    new InvoiceManagerException(
                        "Emisor de factura no existen en el sistema",
                        String.format(
                            "No se encuentra el RFC %s en el sistema", facturaDto.getRfcEmisor()),
                        Constants.BAD_REQUEST));
    Contribuyente contribuyente =
        contribuyenteRepository
            .findByRfc(facturaDto.getRfcRemitente())
            .orElseThrow(
                () ->
                    new InvoiceManagerException(
                        "Error al crear factura", "El receptor no exite", Constants.BAD_REQUEST));
    return new FacturaContextBuilder()
        .setFacturaDto(facturaDto)
        .setEmpresaDto(empresaMapper.getEmpresaDtoFromEntity(empresa))
        .setContribuyenteDto(contribuyenteMapper.getContribuyenteToFromEntity(contribuyente))
        .build();
  }

  public FacturaContext buildEmailContext(String folio, FacturaDto facturaDto)
      throws InvoiceManagerException {
    Empresa empresa =
        empresaRepository
            .findByRfc(facturaDto.getRfcEmisor())
            .orElseThrow(
                () ->
                    new InvoiceManagerException(
                        "Emisor de factura no existen en el sistema",
                        String.format(
                            "No se encuentra el RFC %s en el sistema", facturaDto.getRfcEmisor()),
                        Constants.BAD_REQUEST));
    Contribuyente contribuyente =
        contribuyenteRepository
            .findByRfc(facturaDto.getRfcRemitente())
            .orElseThrow(
                () ->
                    new InvoiceManagerException(
                        "Error al crear factura", "El receptor no exite", Constants.BAD_REQUEST));
    FacturaFileDto xml =
        filesService.getFacturaFileByFolioAndType(
            facturaDto.getFolio(), TipoArchivoEnum.XML.name());
    FacturaFileDto pdf =
        filesService.getFacturaFileByFolioAndType(
            facturaDto.getFolio(), TipoArchivoEnum.PDF.name());
    List<FacturaFileDto> archivos = new ArrayList<>();
    archivos.add(xml);
    archivos.add(pdf);
    return new FacturaContextBuilder()
        .setFacturaDto(facturaDto)
        .setFacturaFilesDto(archivos)
        .setEmpresaDto(empresaMapper.getEmpresaDtoFromEntity(empresa))
        .setContribuyenteDto(contribuyenteMapper.getContribuyenteToFromEntity(contribuyente))
        .build();
  }
}
