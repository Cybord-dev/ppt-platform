package com.business.unknow.services.mapper;

import com.business.unknow.model.dto.services.EmpresaDto;
import com.business.unknow.services.entities.Empresa;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/** @author eej000f */
@Mapper(config = IgnoreUnmappedMapperConfig.class)
public interface EmpresaMapper {

  @Mapping(target = "rfc", source = "informacionFiscal.rfc")
  @Mapping(target = "nombre", source = "informacionFiscal.nombre")
  @Mapping(target = "razonSocial", source = "informacionFiscal.razonSocial")
  @Mapping(target = "calle", source = "informacionFiscal.calle")
  @Mapping(target = "noExterior", source = "informacionFiscal.noExterior")
  @Mapping(target = "noInterior", source = "informacionFiscal.noInterior")
  @Mapping(target = "municipio", source = "informacionFiscal.municipio")
  @Mapping(target = "estado", source = "informacionFiscal.estado")
  @Mapping(target = "pais", source = "informacionFiscal.pais")
  @Mapping(target = "cp", source = "informacionFiscal.cp")
  EmpresaDto getEmpresaDtoFromEntity(Empresa entity);

  List<EmpresaDto> getEmpresaDtosFromEntities(List<Empresa> entities);

  Empresa getEntityFromEmpresaDto(EmpresaDto dto);

  List<Empresa> getEntitiesFromEmpresaDtos(List<EmpresaDto> dto);
}
