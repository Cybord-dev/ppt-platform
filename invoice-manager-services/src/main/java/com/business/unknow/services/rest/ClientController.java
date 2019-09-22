package com.business.unknow.services.rest;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.business.unknow.model.ClientDto;
import com.business.unknow.services.services.ClientService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @author eej000f
 *
 */
@RestController
@RequestMapping
@Api(value = "ClientController", produces = "application/json")
public class ClientController {

	@Autowired
	private ClientService service;

	@GetMapping("/clients/{rfc}")
	@ApiOperation(value = "Get all client by promotor name and name.")
	public ResponseEntity<ClientDto> getClientByRfc(@PathVariable String rfc) {
		return new ResponseEntity<>(service.getClientByRfc(rfc), HttpStatus.OK);
	}

	
	@GetMapping("promotores/{promotor}/clients")
	@ApiOperation(value = "Get all client by promotor name and name.")
	public ResponseEntity<Page<ClientDto>> getClients(@PathVariable String promotor,
			@RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "size", defaultValue = "10") int size) {
		return new ResponseEntity<>(service.getAllClientsByPromotor(promotor, page, size), HttpStatus.OK);
	}

	@GetMapping("/clients")
	@ApiOperation(value = "Get all client by promotor name and name.")
	public ResponseEntity<Page<ClientDto>> getClientsByEmpresa(
			@RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "size", defaultValue = "10") int size) {
		return new ResponseEntity<>(service.getAllClients(page, size), HttpStatus.OK);
	}
	
	@PostMapping("/clients")
	@ApiOperation(value = "insert a new clien into the system")
	public ResponseEntity<ClientDto> insertClient(@RequestBody @Valid ClientDto client){
		return new ResponseEntity<>(service.insertNewClient(client),HttpStatus.CREATED);
	}
	
	@PutMapping("/clients/{rfc}")
	@ApiOperation(value = "insert a new clien into the system")
	public ResponseEntity<ClientDto> updateClient(@PathVariable String rfc, @RequestBody @Valid ClientDto client){
		return new ResponseEntity<>(service.updateClientInfo(client, rfc),HttpStatus.OK);
	}
}
