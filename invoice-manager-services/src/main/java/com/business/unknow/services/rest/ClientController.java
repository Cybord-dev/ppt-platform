package com.business.unknow.services.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.business.unknow.model.ClientDto;
import com.business.unknow.model.error.InvoiceManagerException;
import com.business.unknow.services.services.ClientService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @author eej000f
 *
 */
@RestController
@RequestMapping("/clients")
@Api(value = "ClientController", produces = "application/json")
public class ClientController {

	@Autowired
	private ClientService service;

	@GetMapping("/{rfc}")
	@ApiOperation(value = "Get all client by promotor name and name.")
	public ResponseEntity<ClientDto> getClientByRfc(@PathVariable String rfc) throws InvoiceManagerException {
		return new ResponseEntity<>(service.getClientByRfc(rfc), HttpStatus.OK);
	}

}