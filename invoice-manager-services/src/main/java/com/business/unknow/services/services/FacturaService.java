package com.business.unknow.services.services;

import com.business.unknow.enums.FacturaStatusEnum;
import com.business.unknow.enums.MetodosPagoEnum;
import com.business.unknow.enums.PackFacturarionEnum;
import com.business.unknow.enums.TipoDocumentoEnum;
import com.business.unknow.enums.TipoEmail;
import com.business.unknow.model.context.FacturaContext;
import com.business.unknow.model.dto.FacturaDto;
import com.business.unknow.model.dto.FacturaReportDto;
import com.business.unknow.model.dto.PagoReportDto;
import com.business.unknow.model.dto.cfdi.CfdiDto;
import com.business.unknow.model.dto.cfdi.CfdiPagoDto;
import com.business.unknow.model.dto.cfdi.ComplementoDto;
import com.business.unknow.model.dto.files.FacturaFileDto;
import com.business.unknow.model.dto.pagos.PagoDto;
import com.business.unknow.model.dto.pagos.PagoFacturaDto;
import com.business.unknow.model.error.InvoiceManagerException;
import com.business.unknow.services.entities.cfdi.CfdiPago;
import com.business.unknow.services.entities.factura.Factura;
import com.business.unknow.services.mapper.factura.FacturaMapper;
import com.business.unknow.services.repositories.facturas.CfdiPagoRepository;
import com.business.unknow.services.repositories.facturas.FacturaDao;
import com.business.unknow.services.repositories.facturas.FacturaRepository;
import com.business.unknow.services.services.builder.FacturaBuilderService;
import com.business.unknow.services.services.builder.TimbradoBuilderService;
import com.business.unknow.services.services.evaluations.FacturaEvaluatorService;
import com.business.unknow.services.services.evaluations.TimbradoEvaluatorService;
import com.business.unknow.services.services.executor.FacturacionModernaExecutor;
import com.business.unknow.services.services.executor.NtinkExecutorService;
import com.business.unknow.services.services.executor.SwSapinsExecutorService;
import com.business.unknow.services.services.executor.TimbradoExecutorService;
import com.business.unknow.services.services.translators.FacturaTranslator;
import com.business.unknow.services.services.translators.RelacionadosTranslator;
import com.business.unknow.services.util.FacturaDefaultValues;
import com.business.unknow.services.util.validators.FacturaValidator;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class FacturaService {

  @Autowired private FacturaDao facturaDao;

  @Autowired private FacturaRepository repository;

  @Autowired private CfdiPagoRepository cfdiPagoRepository;

  @Autowired private CfdiService cfdiService;

  @Autowired private FacturaMapper mapper;

  @Autowired private TimbradoEvaluatorService timbradoServiceEvaluator;

  @Autowired private FacturaEvaluatorService facturaServiceEvaluator;

  @Autowired private FacturaBuilderService facturaBuilderService;

  @Autowired private TimbradoBuilderService timbradoBuilderService;

  @Autowired private FacturaTranslator facturaTranslator;

  @Autowired private SwSapinsExecutorService swSapinsExecutorService;

  @Autowired private FacturacionModernaExecutor facturacionModernaExecutor;

  @Autowired private NtinkExecutorService ntinkExecutorService;

  @Autowired private TimbradoExecutorService timbradoExecutorService;

  @Autowired private DevolucionService devolucionService;

  @Autowired private PDFService pdfService;

  @Autowired private FacturaDefaultValues facturaDefaultValues;

  @Autowired private PagoService pagoService;

  @Autowired private RelacionadosTranslator sustitucionTranslator;

  private FacturaValidator validator = new FacturaValidator();

  private static final Logger log = LoggerFactory.getLogger(FacturaService.class);

  private Specification<Factura> buildSearchFilters(Map<String, String> parameters) {
    String linea = (parameters.get("lineaEmisor") == null) ? "A" : parameters.get("lineaEmisor");

    log.info("Finding facturas by {}", parameters);

    return new Specification<>() {

      private static final long serialVersionUID = -7435096122716669730L;

      @Override
      public Predicate toPredicate(
          Root<Factura> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("lineaEmisor"), linea)));
        // TODO move this logic into a enum class that handles all this logic
        if (parameters.get("solicitante") != null) {
          predicates.add(
              criteriaBuilder.and(
                  criteriaBuilder.like(
                      root.get("solicitante"), "%" + parameters.get("solicitante") + "%")));
        }
        if (parameters.get("emisor") != null) {
          predicates.add(
              criteriaBuilder.and(
                  criteriaBuilder.like(
                      root.get("razonSocialEmisor"), "%" + parameters.get("emisor") + "%")));
        }
        if (parameters.get("remitente") != null) {
          predicates.add(
              criteriaBuilder.and(
                  criteriaBuilder.like(
                      root.get("razonSocialRemitente"), "%" + parameters.get("remitente") + "%")));
        }

        if (parameters.get("status") != null) {
          predicates.add(
              criteriaBuilder.and(
                  criteriaBuilder.equal(root.get("statusFactura"), parameters.get("status"))));
        }

        if (parameters.get("tipoDocumento") != null) {
          predicates.add(
              criteriaBuilder.and(
                  criteriaBuilder.equal(
                      root.get("tipoDocumento"), parameters.get("tipoDocumento"))));
        }

        if (parameters.get("metodoPago") != null) {
          predicates.add(
              criteriaBuilder.and(
                  criteriaBuilder.equal(root.get("metodoPago"), parameters.get("metodoPago"))));
        }

        if (parameters.get("saldoPendiente") != null) {
          BigDecimal saldo = new BigDecimal(parameters.get("saldoPendiente"));
          predicates.add(
              criteriaBuilder.and(
                  criteriaBuilder.greaterThanOrEqualTo(root.get("saldoPendiente"), saldo)));
        }

        if (parameters.get("since") != null && parameters.get("to") != null) {
          java.sql.Date start = java.sql.Date.valueOf(LocalDate.parse(parameters.get("since")));
          java.sql.Date end =
              java.sql.Date.valueOf(LocalDate.parse(parameters.get("to")).plusDays(1));
          predicates.add(
              criteriaBuilder.and(criteriaBuilder.between(root.get("fechaCreacion"), start, end)));
        }

        return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
      }
    };
  }

  // FACTURAS
  public Page<FacturaDto> getFacturasByParametros(Map<String, String> parameters) {

    Page<Factura> result;
    int page = (parameters.get("page") == null) ? 0 : Integer.parseInt(parameters.get("page"));
    int size = (parameters.get("size") == null) ? 10 : Integer.parseInt(parameters.get("size"));
    if (parameters.get("prefolio") != null) {
      result = repository.findByPreFolio(parameters.get("prefolio"), PageRequest.of(0, 10));
    } else {
      result =
          repository.findAll(
              buildSearchFilters(parameters),
              PageRequest.of(page, size, Sort.by("fechaActualizacion").descending()));
    }
    return new PageImpl<>(
        mapper.getFacturaDtosFromEntities(result.getContent()),
        result.getPageable(),
        result.getTotalElements());
  }

  public Page<FacturaReportDto> getFacturaReportsByParams(Map<String, String> parameters) {
    int page = (parameters.get("page") == null) ? 0 : Integer.parseInt(parameters.get("page"));
    int size = (parameters.get("size") == null) ? 10 : Integer.parseInt(parameters.get("size"));
    Page<Factura> result =
        repository.findAll(
            buildSearchFilters(parameters),
            PageRequest.of(page, size, Sort.by("fechaActualizacion").descending()));

    List<String> folios =
        result.getContent().stream().map(f -> f.getFolio()).collect(Collectors.toList());
    if (folios.isEmpty()) {
      return new PageImpl<>(new ArrayList<>(), result.getPageable(), result.getTotalElements());
    } else {
      return new PageImpl<>(
          facturaDao.getInvoiceDetailsByFolios(folios),
          result.getPageable(),
          result.getTotalElements());
    }
  }

  public Page<PagoReportDto> getComplementoReportsByParams(Map<String, String> parameters) {
    int page = (parameters.get("page") == null) ? 0 : Integer.parseInt(parameters.get("page"));
    int size = (parameters.get("size") == null) ? 10 : Integer.parseInt(parameters.get("size"));
    Page<Factura> result =
        repository.findAll(
            buildSearchFilters(parameters),
            PageRequest.of(page, size, Sort.by("fechaActualizacion").descending()));
    List<String> folios =
        result.getContent().stream().map(f -> f.getFolio()).collect(Collectors.toList());
    if (folios.isEmpty()) {
      return new PageImpl<>(new ArrayList<>(), result.getPageable(), result.getTotalElements());
    } else {
      return new PageImpl<PagoReportDto>(
          facturaDao.getComplementsDetailsByFolios(folios),
          result.getPageable(),
          result.getTotalElements());
    }
  }

  public FacturaDto getComplementoByIdCfdiAnParcialidad(String folio, Integer parcialidad) {
    List<CfdiPago> pagos = cfdiPagoRepository.findByIdCfdiAndParcialidad(folio, parcialidad);
    Optional<CfdiPago> pago = pagos.stream().findFirst();
    if (pago.isPresent()) {
      return getFacturaBaseByPrefolio(pago.get().getCfdi().getId());
    } else {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND, String.format("La factura con el pre-folio %s no existe", folio));
    }
  }

  public FacturaDto getFacturaByFolio(String folio) {
    FacturaDto factura =
        mapper.getFacturaDtoFromEntity(
            repository
                .findByFolio(folio)
                .orElseThrow(
                    () ->
                        new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            String.format("La factura con el folio %s no existe", folio))));
    factura.setCfdi(cfdiService.getCfdiByFolio(folio));
    return factura;
  }

  public FacturaDto getFacturaByIdCfdi(int id) {
    CfdiDto cfdiDto = cfdiService.getCfdiById(id);
    return mapper.getFacturaDtoFromEntity(
        repository
            .findByFolio(cfdiDto.getFolio())
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        String.format(
                            "La factura con el folio %s no existe", cfdiDto.getFolio()))));
  }

  public FacturaDto getBaseFacturaByFolio(String folio) {
    return mapper.getFacturaDtoFromEntity(
        repository
            .findByFolio(folio)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        String.format("La factura con el folio %s no existe", folio))));
  }

  public FacturaDto getFacturaBaseByPrefolio(Integer id) {
    return mapper.getFacturaDtoFromEntity(
        repository
            .findByIdCfdi(id)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        String.format("La factura con el pre-folio %d no existe", id))));
  }

  @Transactional(
      rollbackOn = {InvoiceManagerException.class, DataAccessException.class, SQLException.class})
  public FacturaDto insertNewFacturaWithDetail(FacturaDto facturaDto)
      throws InvoiceManagerException {
    validator.validatePostFacturaWithDetail(facturaDto);
    FacturaContext facturaContext =
        facturaBuilderService.buildFacturaContextCreateFactura(facturaDto);
    facturaDefaultValues.assignaDefaultsFactura(
        facturaContext.getFacturaDto(),
        facturaDao.getCantidadFacturasOfTheCurrentMonthByTipoDocumento());
    FacturaDto facturaBuilded =
        facturaServiceEvaluator.facturaEvaluation(facturaContext).getFacturaDto();
    CfdiDto cfdi = cfdiService.insertNewCfdi(facturaDto.getCfdi());
    Factura entity = mapper.getEntityFromFacturaDto(facturaBuilded);
    entity.setIdCfdi(cfdi.getId());
    entity.setTotal(cfdi.getTotal());
    entity.setSaldoPendiente(cfdi.getTotal());
    FacturaDto saveFactura = mapper.getFacturaDtoFromEntity(repository.save(entity));
    if (facturaDto.getTipoDocumento().equals(TipoDocumentoEnum.FACTURA.getDescripcion())
        || facturaDto.getTipoDocumento().equals(TipoDocumentoEnum.NOTA_CREDITO.getDescripcion())) {
      pdfService.generateInvoicePDF(facturaBuilded, facturaContext.getCfdi());
    }
    saveFactura.setCfdi(cfdi);
    return saveFactura;
  }

  public FacturaDto updateTotalAndSaldoFactura(
      Integer idCfdi, Optional<BigDecimal> newTotal, Optional<BigDecimal> pago)
      throws InvoiceManagerException {
    Factura factura =
        repository
            .findByIdCfdi(idCfdi)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        String.format("La factura con el pre-folio %d no existe", idCfdi)));
    CfdiDto cfdi = cfdiService.getCfdiById(idCfdi);
    BigDecimal total = newTotal.isPresent() ? newTotal.get() : factura.getTotal();
    BigDecimal montoPagado =
        pagoService.findPagosByFolio(factura.getFolio()).stream()
            .filter(p -> !"CREDITO".equals(p.getFormaPago()))
            .map(
                p ->
                    cfdi.getMoneda().equals(p.getMoneda())
                        ? p.getMonto()
                        : p.getMonto().divide(p.getTipoDeCambio(), 2, RoundingMode.HALF_UP))
            .reduce(BigDecimal.ZERO, (p1, p2) -> p1.add(p2));

    if (pago.isPresent()) {
      montoPagado = montoPagado.add(pago.get());
    }
    BigDecimal saldo = total.subtract(montoPagado);
    validator.checkNotNegative(saldo, "Saldo pendiente");
    factura.setTotal(total);
    factura.setSaldoPendiente(saldo);
    return mapper.getFacturaDtoFromEntity(repository.save(factura));
  }

  public FacturaDto updateTotalAndSaldoFacturaComplemento(
      Integer idCfdi, Optional<BigDecimal> newTotal, Optional<BigDecimal> deuda)
      throws InvoiceManagerException {
    Factura factura =
        repository
            .findByIdCfdi(idCfdi)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        String.format("La factura con el pre-folio %d no existe", idCfdi)));
    if (newTotal.isPresent()) {
      factura.setTotal(newTotal.get());
    }
    if (deuda.isPresent()) {
      validator.checkNotNegative(deuda.get(), "Saldo pendiente");
      factura.setSaldoPendiente(deuda.get());
    }
    return mapper.getFacturaDtoFromEntity(repository.save(factura));
  }

  public FacturaDto updateTotalAndSaldoComplemento(
      Integer idCfdi, Optional<BigDecimal> newTotal, Optional<BigDecimal> pago)
      throws InvoiceManagerException {
    Factura factura =
        repository
            .findByIdCfdi(idCfdi)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        String.format("La factura con el pre-folio %d no existe", idCfdi)));
    BigDecimal total = newTotal.isPresent() ? newTotal.get() : factura.getTotal();
    BigDecimal montoPagado =
        pagoService.findPagosByFolio(factura.getFolio()).stream()
            .filter(p -> !"CREDITO".equals(p.getFormaPago()))
            .map(p -> p.getMonto())
            .reduce(BigDecimal.ZERO, (p1, p2) -> p1.add(p2));

    if (pago.isPresent()) {
      montoPagado = montoPagado.add(pago.get());
    }
    BigDecimal saldo = total.subtract(montoPagado);
    validator.checkNotNegative(saldo, "Saldo pendiente");
    factura.setTotal(total);
    factura.setSaldoPendiente(saldo);
    return mapper.getFacturaDtoFromEntity(repository.save(factura));
  }

  public FacturaDto updateFacturaStatus(Integer idCfdi, FacturaStatusEnum status) {
    Factura factura =
        repository
            .findByIdCfdi(idCfdi)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        String.format("La factura con el pre-folio %d no existe", idCfdi)));
    factura.setStatusFactura(status.getValor());
    return mapper.getFacturaDtoFromEntity(repository.save(factura));
  }

  public FacturaDto updateFactura(Integer idCfdi, FacturaDto facturaDto) {
    Optional<Factura> factura = repository.findByIdCfdi(idCfdi);
    if (factura.isPresent()) {
      facturaServiceEvaluator.facturaStatusValidation(facturaDto);
      factura.get().setStatusFactura(facturaDto.getStatusFactura());
      factura.get().setValidacionOper(facturaDto.getValidacionOper());
      factura.get().setValidacionTeso(facturaDto.getValidacionTeso());
      factura.get().setStatusDetail(facturaDto.getStatusDetail());
      return mapper.getFacturaDtoFromEntity(repository.save(factura.get()));
    } else {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND, String.format("La factura con el pre-folio %d no existe", idCfdi));
    }
  }

  @Transactional(
      rollbackOn = {InvoiceManagerException.class, DataAccessException.class, SQLException.class})
  public void deleteFactura(String folio) throws InvoiceManagerException {
    Factura fact =
        repository
            .findByFolio(folio)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        String.format("La factura con el folio %s no existe", folio)));
    repository.delete(fact);
    cfdiService.deleteCfdi(fact.getIdCfdi());
  }

  public FacturaDto createComplemento(String folio, PagoDto pagoDto)
      throws InvoiceManagerException {
    pagoDto.setMonto(pagoDto.getMonto().setScale(2));
    FacturaDto facturaDto = getBaseFacturaByFolio(folio);
    List<FacturaDto> facturas = new ArrayList<>();
    List<PagoFacturaDto> facturaPagos = new ArrayList<>();
    PagoFacturaDto facturaPagoDto = new PagoFacturaDto();
    facturaPagoDto.setMonto(pagoDto.getMonto());
    facturaPagoDto.setFolio(folio);
    facturaPagos.add(facturaPagoDto);
    facturas.add(facturaDto);
    pagoDto.setFacturas(facturaPagos);
    return generateComplemento(facturas, pagoDto);
  }

  // TIMBRADO
  public FacturaContext timbrarFactura(String folio, FacturaDto facturaDto)
      throws InvoiceManagerException {
    validator.validateTimbrado(facturaDto, folio);
    FacturaContext facturaContext =
        timbradoBuilderService.buildFacturaContextTimbrado(facturaDto, folio);
    timbradoServiceEvaluator.facturaTimbradoValidation(facturaContext);
    switch (TipoDocumentoEnum.findByDesc(facturaContext.getTipoDocumento())) {
      case NOTA_CREDITO:
        if (facturaDto.getIdCfdiRelacionadoPadre() != null) {
          FacturaDto facturaPadre =
              getFacturaBaseByPrefolio(facturaDto.getIdCfdiRelacionadoPadre());
          if (facturaDto.getTotal().compareTo(facturaPadre.getTotal()) > 0
              || facturaDto.getTotal().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvoiceManagerException(
                "El monto total de la nota credito es invalido",
                "Nota de credito invalida",
                HttpStatus.CONFLICT.value());
          }
        } else {
          throw new InvoiceManagerException(
              "Nota de credito invalida",
              "No esta correctamente referenciada",
              HttpStatus.CONFLICT.value());
        }
      case FACTURA:
        facturaContext = facturaTranslator.translateFactura(facturaContext);
        break;
      case COMPLEMENTO:
        facturaContext = facturaTranslator.translateComplemento(facturaContext);
        break;
      default:
        throw new InvoiceManagerException(
            "The type of document not supported",
            String.format("The type of document %s not valid", facturaContext.getTipoDocumento()),
            HttpStatus.BAD_REQUEST.value());
    }
    switch (PackFacturarionEnum.findByNombre(facturaContext.getFacturaDto().getPackFacturacion())) {
      case SW_SAPIENS:
        swSapinsExecutorService.stamp(facturaContext);
        break;
      case FACTURACION_MODERNA:
        facturacionModernaExecutor.stamp(facturaContext);
        break;
      case NTLINK:
        ntinkExecutorService.stamp(facturaContext);
        break;
      default:
        throw new InvoiceManagerException(
            "Pack not supported yet", "Validate with programers", HttpStatus.BAD_REQUEST.value());
    }

    timbradoExecutorService.updateFacturaAndCfdiValues(facturaContext);
    // PDF GENERATION
    FacturaFileDto pdfFile = pdfService.generateInvoicePDF(facturaContext);
    facturaContext.getFacturaFilesDto().add(pdfFile);
    if ((facturaContext.getFacturaDto().getMetodoPago().equals(MetodosPagoEnum.PUE.name())
            || (facturaContext.getFacturaDto().getMetodoPago().equals(MetodosPagoEnum.PPD.name())
                && facturaContext
                    .getFacturaDto()
                    .getTipoDocumento()
                    .equals(TipoDocumentoEnum.COMPLEMENTO.getDescripcion())))
        && facturaContext.getFacturaDto().getLineaEmisor().equals("A")
        && facturaContext.getFacturaDto().getLineaRemitente().equals("CLIENTE")) {
      devolucionService.generarDevoluciones(facturaContext.getFacturaDto());
    }
    final FacturaContext fc = facturaContext;
    CompletableFuture.supplyAsync(() -> enviarCorreo(fc));
    return facturaContext;
  }

  private Boolean enviarCorreo(FacturaContext fc) {
    try {
      if (fc.getFacturaDto().getLineaRemitente().equals("CLIENTE")) {
        try {
          timbradoExecutorService.sentEmail(fc, TipoEmail.SEMEL_JACK);
        } catch (InvoiceManagerException e) {
          timbradoExecutorService.sentEmail(fc, TipoEmail.GMAIL);
        }
      }
      return true;
    } catch (InvoiceManagerException e) {
      return false;
    }
  }

  public FacturaContext cancelarFactura(String folio, FacturaDto facturaDto)
      throws InvoiceManagerException {
    FacturaContext facturaContext =
        timbradoBuilderService.buildFacturaContextCancelado(facturaDto, folio);
    validator.validateTimbrado(facturaDto, folio);
    if (facturaDto.getTipoDocumento().equals("Factura")
        || facturaDto.getMetodoPago().equals("PPD")) {
      for (CfdiPagoDto cfdiPagoDto : cfdiService.getCfdiPagosByFolio(facturaDto.getFolio())) {
        FacturaDto complemento = getFacturaByIdCfdi(cfdiPagoDto.getCfdi().getId());
        if (complemento.getStatusFactura().equals(FacturaStatusEnum.TIMBRADA.getValor())) {
          cancelarFactura(complemento.getFolio(), complemento);
        } else {
          if (!complemento.getStatusFactura().equals(FacturaStatusEnum.CANCELADA.getValor())) {
            complemento.setStatusFactura(FacturaStatusEnum.CANCELADA.getValor());
            updateFactura(complemento.getIdCfdi(), complemento);
          }
        }
      }
    }

    timbradoServiceEvaluator.facturaCancelacionValidation(facturaContext);
    switch (PackFacturarionEnum.findByNombre(facturaContext.getFacturaDto().getPackFacturacion())) {
      case SW_SAPIENS:
        swSapinsExecutorService.cancelarFactura(facturaContext);
        break;
      case FACTURACION_MODERNA:
        facturacionModernaExecutor.cancelarFactura(facturaContext);
        break;
      case NTLINK:
        ntinkExecutorService.cancelarFactura(facturaContext);
        break;
      default:
        throw new InvoiceManagerException(
            "Pack not supported yet", "Validate with programers", HttpStatus.BAD_REQUEST.value());
    }
    timbradoExecutorService.updateCanceladoValues(facturaContext);
    return facturaContext;
  }

  @Transactional(
      rollbackOn = {InvoiceManagerException.class, DataAccessException.class, SQLException.class})
  public FacturaDto generateComplemento(List<FacturaDto> facturas, PagoDto pagoPpd)
      throws InvoiceManagerException {
    if (facturas.stream()
        .anyMatch(a -> !a.getStatusFactura().equals(FacturaStatusEnum.TIMBRADA.getValor()))) {
      throw new InvoiceManagerException(
          "Una factura no esta timbrada",
          "Una factura no esta timbrada",
          HttpStatus.BAD_REQUEST.value());
    }
    Optional<FacturaDto> primerfactura = facturas.stream().findFirst();
    if (primerfactura.isPresent()) {
      FacturaDto factura = getFacturaByFolio(primerfactura.get().getFolio());
      factura.setPackFacturacion(primerfactura.get().getPackFacturacion());
      FacturaContext factContext =
          facturaBuilderService.buildFacturaContextPagoPpdCreation(
              pagoPpd, factura, factura.getFolio());
      CfdiDto cfdiDto = facturaBuilderService.buildFacturaComplementoCreation(factContext);
      FacturaDto complemento =
          facturaBuilderService.buildFacturaDtoPagoPpdCreation(factura, pagoPpd);
      List<CfdiPagoDto> cfdiPagos =
          facturaBuilderService.buildFacturaComplementoPagos(pagoPpd, facturas);
      cfdiDto.setComplemento(new ComplementoDto());
      cfdiDto.getComplemento().setPagos(cfdiPagos);
      complemento.setCfdi(cfdiDto);
      facturaDefaultValues.assignaDefaultsComplemento(
          complemento, facturaDao.getCantidadFacturasOfTheCurrentMonthByTipoDocumento());
      CfdiDto cfdi = cfdiService.insertNewCfdi(complemento.getCfdi());
      Factura fact = mapper.getEntityFromFacturaDto(complemento);
      fact.setIdCfdi(cfdi.getId());
      for (FacturaDto dto : facturas) {
        Optional<CfdiPagoDto> cfdiPago =
            complemento.getCfdi().getComplemento().getPagos().stream()
                .filter(a -> a.getFolio().equals(dto.getFolio()))
                .findFirst();
        updateTotalAndSaldoFacturaComplemento(
            dto.getIdCfdi(),
            Optional.of(dto.getTotal()),
            Optional.of(cfdiPago.get().getImporteSaldoInsoluto()));
      }
      return mapper.getFacturaDtoFromEntity(repository.save(fact));
    } else {
      throw new InvoiceManagerException(
          "Debe tener por lo menos un pago",
          "No asigno el pago a una factura",
          HttpStatus.BAD_REQUEST.value());
    }
  }

  public void recreatePdf(CfdiDto dto) {
    FacturaDto factura =
        mapper.getFacturaDtoFromEntity(
            repository
                .findByFolio(dto.getFolio())
                .orElseThrow(
                    () ->
                        new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            String.format(
                                "La factura con el folio %s no existe", dto.getFolio()))));
    factura.setCfdi(dto);
    pdfService.generateInvoicePDF(factura, null);
  }

  public FacturaContext renviarCorreo(String folio, FacturaDto facturaDto)
      throws InvoiceManagerException {
    FacturaContext facturaContext =
        facturaBuilderService.buildEmailContext(folio, getFacturaByFolio(folio));
    timbradoExecutorService.sentEmail(facturaContext, TipoEmail.GMAIL);
    return facturaContext;
  }

  public FacturaDto postRelacion(FacturaDto dto, TipoDocumentoEnum tipoDocumento)
      throws InvoiceManagerException {
    FacturaDto facturaDto = getFacturaByFolio(dto.getFolio());
    if (TipoDocumentoEnum.FACTURA.getDescripcion().equals(facturaDto.getTipoDocumento())) {
      switch (tipoDocumento) {
        case FACTURA:
          sustitucionTranslator.sustitucionFactura(facturaDto);
          break;
        case NOTA_CREDITO:
          sustitucionTranslator.notaCreditoFactura(facturaDto);
          break;
        default:
          throw new InvoiceManagerException(
              "The type of document not supported",
              String.format("The type of document %s not valid", facturaDto.getTipoDocumento()),
              HttpStatus.BAD_REQUEST.value());
      }
      facturaDto.setIdCfdiRelacionadoPadre(dto.getIdCfdi());
      facturaDto = insertNewFacturaWithDetail(facturaDto);
      FacturaDto facturaAnterior = getFacturaByFolio(dto.getFolio());
      facturaAnterior.setIdCfdiRelacionado(facturaDto.getIdCfdi());
      repository.save(mapper.getEntityFromFacturaDto(facturaAnterior));
      return dto;
    } else {
      throw new InvoiceManagerException(
          "El tipo de documento en la relacion no es de tipo factura",
          HttpStatus.BAD_REQUEST.value());
    }
  }
}
