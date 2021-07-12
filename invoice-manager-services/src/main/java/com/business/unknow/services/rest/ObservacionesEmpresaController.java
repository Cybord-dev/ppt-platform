package com.business.unknow.services.rest;

import com.business.unknow.model.dto.services.ObservacionDto;
import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api")
public class ObservacionesEmpresaController {

  // TODO code all this controller implementation, for now this is only a mock response
  private List<ObservacionDto> observaciones = new ArrayList<>();

  @GetMapping("/empresas/{rfc}/observaciones")
  public ResponseEntity<List<ObservacionDto>> findObservacionesBy(@PathVariable String rfc) {
    return new ResponseEntity<>(observaciones, HttpStatus.OK);
  }

  @PostMapping("/empresas/{rfc}/observaciones")
  public ResponseEntity<ObservacionDto> createObservation(
      @PathVariable String rfc, @RequestBody @Valid ObservacionDto observacion) {
    observacion.setId(observaciones.size() + 1);
    this.observaciones.add(observacion);
    return new ResponseEntity<>(observacion, HttpStatus.CREATED);
  }

  @PutMapping("/empresas/{rfc}/observaciones/{id}")
  public ResponseEntity<ObservacionDto> updateObservation(
      @PathVariable String id,
      @PathVariable String rfc,
      @RequestBody @Valid ObservacionDto observacion) {

    ObservacionDto ob =
        this.observaciones.stream()
            .filter(o -> o.getId().equals(id))
            .findAny()
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "La observacion a modificar no existe"));

    observaciones.remove(ob);
    observacion.setId(ob.getId());
    observaciones.add(observacion);
    return new ResponseEntity<>(observacion, HttpStatus.OK);
  }

  @DeleteMapping("/empresas/{rfc}/observaciones/{id}")
  public ResponseEntity<Void> deleteObservation(@PathVariable String id, @PathVariable String rfc) {

    ObservacionDto ob =
        this.observaciones.stream()
            .filter(o -> o.getId().equals(id))
            .findAny()
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "La observacion a modificar no existe"));

    observaciones.remove(ob);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
