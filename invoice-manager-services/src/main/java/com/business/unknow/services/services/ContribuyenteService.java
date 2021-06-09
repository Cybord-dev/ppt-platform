package com.business.unknow.services.services;

import com.business.unknow.model.dto.services.ContribuyenteDto;
import com.business.unknow.services.entities.Contribuyente;
import com.business.unknow.services.mapper.ContribuyenteMapper;
import com.business.unknow.services.repositories.ContribuyenteRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ContribuyenteService {

  @Autowired private ContribuyenteRepository repository;

  @Autowired private ContribuyenteMapper mapper;

  public Page<ContribuyenteDto> getContribuyentesByParametros(
      Optional<String> rfc, Optional<String> razonSocial, int page, int size) {
    Page<Contribuyente> result;
    if (razonSocial.isEmpty() && rfc.isEmpty()) {
      result = repository.findAll(PageRequest.of(page, size));
    } else if (rfc.isPresent()) {
      result =
          repository.findByRfcIgnoreCaseContaining(
              String.format("%%%s%%", rfc.get()), PageRequest.of(page, size));
    } else {
      result =
          repository.findByRazonSocialIgnoreCaseContaining(
              String.format("%%%s%%", razonSocial.get()), PageRequest.of(page, size));
    }
    return new PageImpl<>(
        mapper.getContribuyenteDtosFromEntities(result.getContent()),
        result.getPageable(),
        result.getTotalElements());
  }

  public ContribuyenteDto getContribuyenteByRfc(String rfc) {
    Optional<Contribuyente> client = repository.findByRfc(rfc);
    if (client.isPresent()) {
      return mapper.getContribuyenteToFromEntity(client.get());
    } else {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND, String.format("El Contribuyente con el rfc %s no existe", rfc));
    }
  }

  public ContribuyenteDto insertNewContribuyente(ContribuyenteDto contribuyente) {
    return mapper.getContribuyenteToFromEntity(
        repository.save(mapper.getEntityFromContribuyenteDto(contribuyente)));
  }

  public ContribuyenteDto updateContribuyenteInfo(ContribuyenteDto contribuyenteDto, String rfc) {
    var contribuyente =
        repository
            .findByRfc(rfc)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        String.format("El cliente con el rfc %s no existe", rfc)));
    contribuyente.setGiro(contribuyenteDto.getGiro());
    contribuyente.setNombre(contribuyenteDto.getNombre());
    contribuyente.setCurp(contribuyenteDto.getCurp());
    contribuyente.setRazonSocial(contribuyenteDto.getRazonSocial());
    contribuyente.setCalle(contribuyenteDto.getCalle());
    contribuyente.setNoExterior(contribuyenteDto.getNoExterior());
    contribuyente.setNoInterior(contribuyenteDto.getNoInterior());
    contribuyente.setMunicipio(contribuyenteDto.getMunicipio());
    contribuyente.setLocalidad(contribuyenteDto.getLocalidad());
    contribuyente.setEstado(contribuyenteDto.getEstado());
    contribuyente.setPais(contribuyenteDto.getPais());
    contribuyente.setCp(contribuyenteDto.getCp());
    contribuyente.setCorreo(contribuyenteDto.getCorreo());
    contribuyente.setTelefono(contribuyenteDto.getTelefono());
    return mapper.getContribuyenteToFromEntity(repository.save(contribuyente));
  }
}
