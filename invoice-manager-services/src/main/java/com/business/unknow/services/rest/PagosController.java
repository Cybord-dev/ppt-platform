/**
 * 
 */
package com.business.unknow.services.rest;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.business.unknow.model.DevolucionDto;
import com.business.unknow.model.PagoDto;
import com.business.unknow.model.SolicitudDevolucionDto;
import com.business.unknow.model.error.InvoiceManagerException;
import com.business.unknow.services.services.DevolucionService;
import com.business.unknow.services.services.PagoService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 *@author ralfdemoledor
 *
 */
@RestController
@RequestMapping("/api/pagos")
@Api(value = "PagosController", produces = "application/json")
public class PagosController {
	
	@Autowired
	private PagoService service;
	
	@Autowired
	private DevolucionService devolucionService;
	
//	@Autowired
//	private AnimalDispatcher dispatcher;
	
	
	@GetMapping
	@ApiOperation(value = "Get all payments.")
	public ResponseEntity<Page<PagoDto>> getAllPayments(
			@RequestParam(name = "folio", required = false) Optional<String> folio,
			@RequestParam(name = "status", defaultValue = "") String status,
			@RequestParam(name = "formaPago", defaultValue = "") String formaPago,
			@RequestParam(name = "banco", defaultValue = "") String banco,
			@RequestParam(name = "since", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date since,
			@RequestParam(name = "to", required = false)  @DateTimeFormat(pattern = "yyyy-MM-dd") Date to,
			@RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "size", defaultValue = "10") int size) {
		
		Page<PagoDto> pagos = service.getPaginatedPayments(folio, formaPago, status, banco, since, to, page, size);
		
		return new ResponseEntity<>(pagos,HttpStatus.OK);
	}
	
	@GetMapping("/ingresos")
	@ApiOperation(value = "Get all payments.")
	public ResponseEntity<Page<PagoDto>> getIngresos(
			@RequestParam(name = "status", defaultValue = "") String status,
			@RequestParam(name = "formaPago", defaultValue = "") String formaPago,
			@RequestParam(name = "banco", defaultValue = "") String banco,
			@RequestParam(name = "since", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date since,
			@RequestParam(name = "to", required = false)  @DateTimeFormat(pattern = "yyyy-MM-dd") Date to,
			@RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "size", defaultValue = "10") int size){
		Page<PagoDto> pagos = service.getIngresosPaginados(formaPago, status, banco, since, to, page, size);
		
		return new ResponseEntity<>(pagos,HttpStatus.OK);
	}
	
	@GetMapping("/egresos")
	@ApiOperation(value = "Get all payments.")
	public ResponseEntity<Page<PagoDto>> getEgresos(
			@RequestParam(name = "status", defaultValue = "") String status,
			@RequestParam(name = "formaPago", defaultValue = "") String formaPago,
			@RequestParam(name = "banco", defaultValue = "") String banco,
			@RequestParam(name = "since", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date since,
			@RequestParam(name = "to", required = false)  @DateTimeFormat(pattern = "yyyy-MM-dd") Date to,
			@RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "size", defaultValue = "10") int size){
		Page<PagoDto> pagos = service.getEgresosPaginados(formaPago, status, banco, since, to, page, size);
		
		return new ResponseEntity<>(pagos,HttpStatus.OK);
	}
	
	
	@GetMapping("{idPago}/devoluciones")
	public ResponseEntity<List<DevolucionDto>> getDevolucionesByPagoDestino(@PathVariable(name = "idPago")Integer idPagoDestino){
		return new ResponseEntity<>(devolucionService.getDevolucionesByPagoDestino(idPagoDestino),HttpStatus.OK);
	}	
	
	
	
	@PostMapping("/devoluciones")
	public ResponseEntity<PagoDto> solicitudDevolucion(@RequestBody SolicitudDevolucionDto solicitud) throws InvoiceManagerException{
		return new ResponseEntity<PagoDto>(service.solicitudDevolucion(solicitud), HttpStatus.CREATED);
	}
	
	
	
	
//	@GetMapping("/{folio}")
//	public ResponseEntity<Animal> getFactura(@PathVariable String folio) throws InvoiceCommonException {
//		Animal animal=dispatcher.getAnimal(folio);
//		animal.imprime();
//		return new ResponseEntity<>(animal, HttpStatus.OK);
//	}

}
