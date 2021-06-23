package com.business.unknow.services.services;

import com.business.unknow.model.dto.services.EmpresaDto;
import com.business.unknow.model.error.InvoiceManagerException;
import com.business.unknow.services.entities.Empresa;
import com.business.unknow.services.mapper.ContribuyenteMapper;
import com.business.unknow.services.mapper.EmpresaMapper;
import com.business.unknow.services.repositories.EmpresaRepository;
import com.business.unknow.services.services.executor.EmpresaExecutorService;
import com.business.unknow.services.util.validators.EmpresaValidator;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class EmpresaService {

  @Autowired private EmpresaRepository repository;

  @Autowired private EmpresaMapper mapper;

  @Autowired private EmpresaExecutorService empresaEvaluatorService;

  @Autowired private ContribuyenteMapper contribuyenteMapper;

  private EmpresaValidator empresaValidator = new EmpresaValidator();

  public Page<EmpresaDto> getEmpresasByParametros(
      Optional<String> rfc, Optional<String> razonSocial, String linea, int page, int size) {
    Page<Empresa> result;
    if (!razonSocial.isPresent() && !rfc.isPresent()) {
      result =
          repository.findAllWithLinea(String.format("%%%s%%", linea), PageRequest.of(page, size));
    } else if (rfc.isPresent()) {
      result =
          repository.findByRfcIgnoreCaseContaining(
              String.format("%%%s%%", rfc.get()),
              String.format("%%%s%%", linea),
              PageRequest.of(page, size));
    } else {
      result =
          repository.findByRazonSocialIgnoreCaseContaining(
              String.format("%%%s%%", razonSocial.get()),
              String.format("%%%s%%", linea),
              PageRequest.of(page, size));
    }
    return new PageImpl<>(
        mapper.getEmpresaDtosFromEntities(result.getContent()),
        result.getPageable(),
        result.getTotalElements());
  }

  public EmpresaDto getEmpresaByRfc(String rfc) {
    Empresa empresa =
        repository
            .findByRfc(rfc)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        String.format("No existe la empresa con rfc %s", rfc)));
    return mapper.getEmpresaDtoFromEntity(empresa);
  }

  public List<EmpresaDto> getEmpresasByGiroAndLinea(String tipo, Integer giro) {
    return mapper.getEmpresaDtosFromEntities(repository.findByTipoAndGiro(tipo, giro));
  }

  @Transactional(
      rollbackOn = {InvoiceManagerException.class, DataAccessException.class, SQLException.class})
  public EmpresaDto insertNewEmpresa(EmpresaDto empresaDto) throws InvoiceManagerException {
    empresaValidator.validatePostEmpresa(empresaDto);
    empresaDto.setActivo(false);

    if (repository.findByRfc(empresaDto.getInformacionFiscal().getRfc()).isPresent()) {
      throw new InvoiceManagerException(
          "Ya existe la empresa",
          String.format("La empresa %s ya existe", empresaDto.getInformacionFiscal().getRfc()),
          HttpStatus.CONFLICT.value());
    }
    return empresaEvaluatorService.createEmpresa(empresaDto);
  }

  public EmpresaDto updateEmpresaInfo(EmpresaDto empresaDto, String rfc)
      throws InvoiceManagerException {
    Empresa empresa =
        repository
            .findByRfc(rfc)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        String.format("El empresa con el rfc %s no existe", rfc)));
    empresa.setTipo(empresaDto.getTipo());
    empresa.setReferencia(empresaDto.getReferencia());
    empresa.setWeb(empresaDto.getWeb());
    empresa.setSucursal(empresaDto.getSucursal());
    empresa.setPwSat(empresaDto.getPwSat());
    empresa.setCorreo(empresaDto.getCorreo());

    empresa.setGiro(empresaDto.getGiro());
    empresa.setContactoAdmin(empresaDto.getContactoAdmin());
    empresa.setEncabezado(empresaDto.getEncabezado());
    empresa.setPiePagina(empresaDto.getPiePagina());
    empresa.setRegimenFiscal(empresaDto.getRegimenFiscal());

    empresa.setPwCorreo(empresaDto.getPwCorreo());
    empresa.setActivo(empresaDto.getActivo());
    empresa.setNoCertificado(empresaDto.getNoCertificado());
    empresa.setInformacionFiscal(
        contribuyenteMapper.getEntityFromContribuyenteDto(empresaDto.getInformacionFiscal()));
    empresaEvaluatorService.updateLogo(rfc, empresaDto.getLogotipo());
    empresaEvaluatorService.updateCertificado(rfc, empresaDto.getCertificado());
    empresaEvaluatorService.updateKey(rfc, empresaDto.getLlavePrivada());
    return mapper.getEmpresaDtoFromEntity(repository.save(empresa));
  }
}
