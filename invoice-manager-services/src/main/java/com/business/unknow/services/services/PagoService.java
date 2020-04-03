/**
 * 
 */
package com.business.unknow.services.services;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.business.unknow.enums.FacturaStatusEnum;
import com.business.unknow.enums.MetodosPagoEnum;
import com.business.unknow.enums.PagoStatusEnum;
import com.business.unknow.enums.RevisionPagosEnum;
import com.business.unknow.model.dto.FacturaDto;
import com.business.unknow.model.dto.services.PagoDto;
import com.business.unknow.model.error.InvoiceManagerException;
import com.business.unknow.services.entities.Pago;
import com.business.unknow.services.mapper.PagoMapper;
import com.business.unknow.services.repositories.PagoRepository;
import com.business.unknow.services.services.evaluations.PagoEvaluatorService;
import com.business.unknow.services.services.executor.PagoExecutorService;
import com.business.unknow.services.util.PagoBuilder;

/**
 * @author ralfdemoledor
 *
 */
@Service
public class PagoService {

	@Autowired
	private PagoRepository repository;

	@Autowired
	private PagoMapper mapper;

	@Autowired
	private PagoEvaluatorService pagoEvaluatorService;


	@Autowired
	private PagoExecutorService pagoExecutorService;

	@Autowired
	private FacturaService facturaService;


	private static final Logger log = LoggerFactory.getLogger(PagoService.class);

	public Page<PagoDto> getPaginatedPayments(Optional<String> folio, Optional<String> acredor, Optional<String> deudor,
			String formaPago, String status, String banco, Date since, Date to, int page, int size) {

		Date start = (since == null) ? new DateTime().minusYears(1).toDate() : since;
		Date end = (to == null) ? new Date() : to;
		Page<Pago> result = null;
		if (folio.isPresent()) {
			result = repository.findByFolioIgnoreCaseContaining(folio.get(),
					PageRequest.of(0, 10, Sort.by("fechaActualizacion").descending()));
		} else if (acredor.isPresent()) {
			result = repository.findPagosAcredorFilteredByParams(acredor.get(), String.format("%%%s%%", status),
					String.format("%%%s%%", formaPago), String.format("%%%s%%", banco), start, end,
					PageRequest.of(page, size, Sort.by("fechaActualizacion").descending()));
		} else if (deudor.isPresent()) {
			result = repository.findPagosDeudorFilteredByParams(deudor.get(), String.format("%%%s%%", status),
					String.format("%%%s%%", formaPago), String.format("%%%s%%", banco), start, end,
					PageRequest.of(page, size, Sort.by("fechaActualizacion").descending()));
		} else {
			result = repository.findPagosFilteredByParams(String.format("%%%s%%", status),
					String.format("%%%s%%", formaPago), String.format("%%%s%%", banco), start, end,
					PageRequest.of(page, size, Sort.by("fechaActualizacion").descending()));
		}

		return new PageImpl<>(mapper.getPagosDtoFromEntities(result.getContent()), result.getPageable(),
				result.getTotalElements());
	}

	public List<PagoDto> getPagos(String folio) {
		return mapper.getPagosDtoFromEntities(repository.findByFolioPadre(folio));
	}

	public Page<PagoDto> getIngresosPaginados(String formaPago, String status, String banco, String cuenta, Date since,
			Date to, int page, int size) {
		Date start = (since == null) ? new DateTime().minusYears(1).toDate() : since;
		Date end = (to == null) ? new Date() : to;
		log.info("Search ingresos by status {}, formapago {}, banco {} and start {} y end {}", status, formaPago, banco,
				start, end);
		Page<Pago> result = repository.findIngresosByFilterParams(String.format("%%%s%%", status),
				String.format("%%%s%%", formaPago), String.format("%%%s%%", banco), String.format("%%%s%%", cuenta),
				start, end, PageRequest.of(page, size, Sort.by("fechaCreacion").descending()));

		return new PageImpl<>(mapper.getPagosDtoFromEntities(result.getContent()), result.getPageable(),
				result.getTotalElements());
	}

	public Page<PagoDto> getEgresosPaginados(String formaPago, String status, String banco, String cuenta, Date since,
			Date to, int page, int size) {
		Date start = (since == null) ? new DateTime().minusYears(1).toDate() : since;
		Date end = (to == null) ? new Date() : to;
		log.info("Search egresos by status {}, formapago {}, banco {} and start {} y end {}", status, formaPago, banco,
				start, end);
		Page<Pago> result = repository.findEgresosByFilterParams(String.format("%%%s%%", status),
				String.format("%%%s%%", formaPago), String.format("%%%s%%", banco), String.format("%%%s%%", cuenta),
				start, end, PageRequest.of(page, size, Sort.by("fechaCreacion").descending()));

		return new PageImpl<>(mapper.getPagosDtoFromEntities(result.getContent()), result.getPageable(),
				result.getTotalElements());
	}

	public Double getSumaIngresosbyParams(String formaPago, String banco, String cuenta, Date since, Date to) {
		Date start = (since == null) ? new DateTime().minusYears(1).toDate() : since;
		Date end = (to == null) ? new Date() : to;
		return repository.sumIngresosByFilterParams(String.format("%%%s%%", formaPago), String.format("%%%s%%", banco),
				String.format("%%%s%%", cuenta), start, end);
	}

	public Double getSumaEgresosbyParams(String formaPago, String banco, String cuenta, Date since, Date to) {
		Date start = (since == null) ? new DateTime().minusYears(1).toDate() : since;
		Date end = (to == null) ? new Date() : to;
		return repository.sumEgresosByFilterParams(String.format("%%%s%%", formaPago), String.format("%%%s%%", banco),
				String.format("%%%s%%", cuenta), start, end);
	}

	public PagoDto getPaymentById(Integer id) throws InvoiceManagerException {
		Optional<Pago> payment = repository.findById(id);
		if (payment.isPresent()) {
			return mapper.getPagoDtoFromEntity(payment.get());
		} else {
			throw new InvoiceManagerException("Pago no encontrado",
					String.format("El pago con id %d no fu encontrado.", id), HttpStatus.NOT_FOUND.value());
		}
	}

	public PagoDto insertNewPaymentWithoutValidation(PagoDto payment) {
		return mapper.getPagoDtoFromEntity(repository.save(mapper.getEntityFromPagoDto(payment)));
	}

	@Transactional(rollbackOn = { InvoiceManagerException.class, DataAccessException.class, SQLException.class })
	public PagoDto insertNewPayment(String folio, PagoDto pagoDto) throws InvoiceManagerException {
		//1.- validate fields required
		//2.- validate if payment is credit or real payment
		//3.- Validate payment amount can't be greater than invoice total
		//4.- Populate invoice total in payment
		//5.- PPD crean en automatico complemento
		
		pagoEvaluatorService.validatePayment(pagoDto);
		FacturaDto facturaPadre = facturaService.getFacturaByFolio(folio);
		List<PagoDto> payments = getPagos(folio);
		pagoDto.setFolio(folio);
		pagoDto.setFolioPadre(folio);
		pagoDto.setTotal(facturaPadre.getCfdi().getTotal());
		pagoEvaluatorService.validatePaymentCreation(pagoDto, payments, facturaPadre.getCfdi());
		if (facturaPadre.getCfdi().getMetodoPago().equals(MetodosPagoEnum.PPD.name())) {
			FacturaDto complemento = facturaService.generateComplemento(facturaPadre, pagoDto);
			pagoDto.setFolio(complemento.getFolio()); //Override folio payment
		} 
		return pagoExecutorService.createPagoExecutor(pagoDto, payments);
	}

	@Transactional(rollbackOn = { InvoiceManagerException.class, DataAccessException.class, SQLException.class })
	public PagoDto updatePago(String folio, Integer id, PagoDto pago) throws InvoiceManagerException {
		Pago entity = repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
				String.format("El pago con el id %d no existe", id)));
		FacturaDto factura = facturaService.getFacturaByFolio(folio);
		PagoBuilder pagoBuilder = new PagoBuilder(mapper.getPagoDtoFromEntity(entity)) // payment only update revision
				.setRevision1(pago.getRevision1()).setRevision2(pago.getRevision2()).setRevisor1(pago.getRevisor1())
				.setRevisor2(pago.getRevisor2());
		pagoEvaluatorService.validatePayment(pago);

		pagoEvaluatorService.validatePaymentUpdate(pago, mapper.getPagoDtoFromEntity(entity), getPagos(folio), facturaService.getFacturaByFolio(folio).getCfdi());
		
		if (entity.getRevision1()&&entity.getRevision2()) {
			throw new InvoiceManagerException(
					"Incongruencia en la validacion de pagos ya se valido dos veces ",
					"Incongruencia de pago.", HttpStatus.CONFLICT.value());
		} else if (pago.getStatusPago().equals(RevisionPagosEnum.RECHAZADO.name())) {
			if (entity.getRevision1() && entity.getRevision2()) {
				throw new InvoiceManagerException("No puede ser rechazado un pago que ya fue aprobado",
						"El pago ya fue aprobado por dos personas.", HttpStatus.CONFLICT.value());
			} else {
				factura.setStatusFactura(FacturaStatusEnum.RECHAZO_TESORERIA.getValor());
				factura.setStatusDetail(pago.getComentarioPago());
				facturaService.updateFactura(factura, folio);
				pagoBuilder.setStatusPago(RevisionPagosEnum.RECHAZADO.name());
			}
		}else if (entity.getRevision1() && !pago.getRevision2()&&!pago.getRevisor1().equals(entity.getRevision1())) {
			throw new InvoiceManagerException(
					"Ya se realizo la primera validacion",
					"Ya se realizo la primera validacion.", HttpStatus.CONFLICT.value());
		}else if (!entity.getRevision1() && pago.getRevision2()) {
			throw new InvoiceManagerException(
					"Incongruencia en la validacion de pagos, el segundo pago no puede ser validado si el primer pago ya fue validado",
					"Incongruencia de pago.", HttpStatus.CONFLICT.value());
		} else if (entity.getRevision1() && pago.getRevision2()
				&& (factura.getStatusFactura().equals(FacturaStatusEnum.VALIDACION_OPERACIONES.getValor())
						|| factura.getStatusFactura().equals(FacturaStatusEnum.VALIDACION_TESORERIA.getValor())
						|| factura.getStatusFactura().equals(FacturaStatusEnum.RECHAZO_TESORERIA.getValor()))) {
			entity.setStatusPago(RevisionPagosEnum.ACEPTADO.name());
			factura.setStatusPago(PagoStatusEnum.PAGADA.getValor());
			if (!factura.getStatusFactura().equals(FacturaStatusEnum.VALIDACION_OPERACIONES.getValor())) {
				factura.setStatusFactura(FacturaStatusEnum.POR_TIMBRAR.getValor());
			}
			facturaService.updateFactura(factura, folio);
			pagoBuilder.setStatusPago(RevisionPagosEnum.ACEPTADO.name());
		}

		return mapper.getPagoDtoFromEntity(repository.save(mapper.getEntityFromPagoDto(pagoBuilder.build())));
	}

	public void deletePago(String folio, Integer id) throws InvoiceManagerException {
		PagoDto payment =  mapper.getPagoDtoFromEntity(repository.findById(id).orElseThrow(() -> new InvoiceManagerException("Metodo de pago no soportado",
				String.format("El pago con el id no existe %d", id), HttpStatus.BAD_REQUEST.value())));
		FacturaDto factura = facturaService.getBaseFacturaByFolio(payment.getFolio());
		List<PagoDto> payments = getPagos(folio);
		pagoEvaluatorService.deletepaymentValidation(payment, factura);
		pagoExecutorService.deletePagoExecutor(payment, payments, factura);
	}

}
