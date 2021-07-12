package com.business.unknow.services.services;

import com.business.unknow.model.dto.services.CuentaBancariaDto;
import com.business.unknow.services.entities.CuentaBancaria;
import com.business.unknow.services.mapper.CuentaBancariaMapper;
import com.business.unknow.services.repositories.CuentaBancariaRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CuentaBancariaService {

  @Autowired private CuentaBancariaRepository repository;

  @Autowired private CuentaBancariaMapper mapper;

  private static final Logger log = LoggerFactory.getLogger(CuentaBancariaService.class);

  private Specification<CuentaBancaria> buildSearchFilters(Map<String, String> parameters) {

    log.info("Finding facturas by {}", parameters);

    return new Specification<CuentaBancaria>() {

      private static final long serialVersionUID = -7435096122716669730L;

      @Override
      public Predicate toPredicate(
          Root<CuentaBancaria> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();
        if (parameters.get("empresa") != null) {
          predicates.add(
              criteriaBuilder.and(
                  criteriaBuilder.like(
                      root.get("empresa"), "%" + parameters.get("empresa") + "%")));
        }
        if (parameters.get("banco") != null) {
          predicates.add(
              criteriaBuilder.and(
                  criteriaBuilder.like(root.get("banco"), "%" + parameters.get("banco") + "%")));
        }
        if (parameters.get("cuenta") != null) {
          predicates.add(
              criteriaBuilder.and(
                  criteriaBuilder.like(root.get("cuenta"), "%" + parameters.get("cuenta") + "%")));
        }

        if (parameters.get("clabe") != null) {
          predicates.add(
              criteriaBuilder.and(
                  criteriaBuilder.equal(root.get("clabe"), parameters.get("clabe"))));
        }

        return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
      }
    };
  }

  public Page<CuentaBancariaDto> getCuentasBancariasByfilters(Map<String, String> parameters) {
    Page<CuentaBancaria> result;
    int page =
        (parameters.get("page") == null) || parameters.get("page").equals("")
            ? 0
            : Integer.valueOf(parameters.get("page"));
    int size = (parameters.get("size") == null) ? 10 : Integer.valueOf(parameters.get("size"));
    result =
        repository.findAll(
            buildSearchFilters(parameters),
            PageRequest.of(page, size, Sort.by("fechaActualizacion").descending()));
    return new PageImpl<>(
        mapper.getCuentaBancariaDtosFromEntities(result.getContent()),
        result.getPageable(),
        result.getTotalElements());
  }

  public List<CuentaBancariaDto> getCuentasPorRfc(String empresa) {
    return mapper.getCuentaBancariaDtosFromEntities(repository.findByEmpresa(empresa));
  }

  public CuentaBancariaDto infoCuentaBancaria(String empresa, String cuenta) {
    return mapper.getCuentaBancariaToFromEntity(
        repository.findByEmpresaAndCuenta(empresa, cuenta).get());
  }

  public CuentaBancariaDto createCuentaBancaria(CuentaBancariaDto cuentaDto) {
    Optional<CuentaBancaria> entity =
        repository.findByEmpresaAndCuenta(cuentaDto.getEmpresa(), cuentaDto.getCuenta());
    if (entity.isPresent()) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST,
          String.format("Esta Empresa con esta cuenta ya existe %s", cuentaDto.getCuenta()));
    } else {

      CuentaBancaria cuentaBancaria =
          repository.save(mapper.getEntityFromCuentaBancariaDto(cuentaDto));
      return mapper.getCuentaBancariaToFromEntity(cuentaBancaria);
    }
  }

  public CuentaBancariaDto updateCuentaBancaria(
      Integer cuentaId, CuentaBancariaDto cuentaBancariaDto) {
    CuentaBancaria entity =
        repository
            .findById(cuentaBancariaDto.getId())
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        String.format(
                            "Esta Empresa con esta cuenta ya existe %s",
                            cuentaBancariaDto.getCuenta())));
    entity.setBanco(cuentaBancariaDto.getBanco());
    entity.setEmpresa(cuentaBancariaDto.getEmpresa());
    entity.setCuenta(cuentaBancariaDto.getCuenta());
    entity.setClabe(cuentaBancariaDto.getClabe());

    return mapper.getCuentaBancariaToFromEntity(repository.save(entity));
  }

  public void deleteCuentaBancaria(Integer id) {
    CuentaBancaria entity =
        repository
            .findById(id)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, String.format("Esta cuenta no existe %d", id)));
    repository.delete(entity);
  }
}
