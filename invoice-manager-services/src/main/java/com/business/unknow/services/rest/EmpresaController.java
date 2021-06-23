package com.business.unknow.services.rest;

import com.business.unknow.model.dto.services.EmpresaDto;
import com.business.unknow.model.error.InvoiceManagerException;
import com.business.unknow.services.services.EmpresaService;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** @author eej000f */
@RestController
@RequestMapping("/api")
public class EmpresaController {

  @Autowired private EmpresaService service;

  @GetMapping("/empresas")
  public ResponseEntity<Page<EmpresaDto>> getEmpresasByParameter(
      @RequestParam(name = "razonSocial", required = false) Optional<String> razonSocial,
      @RequestParam(name = "rfc", required = false) Optional<String> rfc,
      @RequestParam(name = "linea", defaultValue = "") String linea,
      @RequestParam(name = "page", defaultValue = "0") int page,
      @RequestParam(name = "size", defaultValue = "10") int size) {
    return new ResponseEntity<>(
        service.getEmpresasByParametros(rfc, razonSocial, linea, page, size), HttpStatus.OK);
  }

  @GetMapping("/empresas/{rfc}")
  public ResponseEntity<EmpresaDto> updateClient(@PathVariable String rfc) {
    return new ResponseEntity<>(service.getEmpresaByRfc(rfc), HttpStatus.OK);
  }

  @GetMapping("/lineas/{linea}/giros/{giro}/empresas")
  public ResponseEntity<List<EmpresaDto>> getEmpresasByLineaAndGiro(
      @PathVariable(name = "linea") String linea, @PathVariable(name = "giro") Integer giro) {
    return new ResponseEntity<>(service.getEmpresasByGiroAndLinea(linea, giro), HttpStatus.OK);
  }

  @PostMapping("/empresas")
  public ResponseEntity<EmpresaDto> insertClient(@RequestBody @Valid EmpresaDto empresa)
      throws InvoiceManagerException {
    return new ResponseEntity<>(service.insertNewEmpresa(empresa), HttpStatus.CREATED);
  }

  @PutMapping("/empresas/{rfc}")
  public ResponseEntity<EmpresaDto> updateClient(
      @PathVariable String rfc, @RequestBody @Valid EmpresaDto empresa)
      throws InvoiceManagerException {
    return new ResponseEntity<>(service.updateEmpresaInfo(empresa, rfc), HttpStatus.OK);
  }
}
