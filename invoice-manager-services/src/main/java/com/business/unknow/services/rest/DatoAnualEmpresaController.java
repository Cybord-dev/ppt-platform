package com.business.unknow.services.rest;

import com.business.unknow.model.dto.services.DatoAnualEmpresaDto;
import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api")
public class DatoAnualEmpresaController {

  // TODO code all this controller implementation, for now this is only a mock response
  private List<DatoAnualEmpresaDto> datos = new ArrayList<>();

  @GetMapping("/empresas/{rfc}/datos")
  public ResponseEntity<List<DatoAnualEmpresaDto>> findDataBy(@PathVariable String rfc) {
    return new ResponseEntity<>(datos, HttpStatus.OK);
  }

  @PostMapping("/empresas/{rfc}/datos")
  public ResponseEntity<DatoAnualEmpresaDto> createData(
      @PathVariable String rfc, @RequestBody @Valid DatoAnualEmpresaDto dato) {
    dato.setId(datos.size() + 1);
    this.datos.add(dato);
    return new ResponseEntity<>(dato, HttpStatus.CREATED);
  }

  @PutMapping("/empresas/{rfc}/datos/{id}")
  public ResponseEntity<DatoAnualEmpresaDto> updateData(
      @PathVariable String id,
      @PathVariable String rfc,
      @RequestBody @Valid DatoAnualEmpresaDto observacion) {

    DatoAnualEmpresaDto ob =
        this.datos.stream()
            .filter(o -> o.getId().equals(id))
            .findAny()
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "La observacion a modificar no existe"));

    datos.remove(ob);
    observacion.setId(ob.getId());
    datos.add(observacion);
    return new ResponseEntity<>(observacion, HttpStatus.OK);
  }

  @DeleteMapping("/empresas/{rfc}/datos/{id}")
  public ResponseEntity<Void> deleteData(@PathVariable String id, @PathVariable String rfc) {

    DatoAnualEmpresaDto ob =
        this.datos.stream()
            .filter(o -> o.getId().equals(id))
            .findAny()
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "El dato a borrar no existe"));

    datos.remove(ob);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
