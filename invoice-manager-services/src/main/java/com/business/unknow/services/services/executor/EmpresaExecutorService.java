package com.business.unknow.services.services.executor;

import com.business.unknow.model.dto.services.EmpresaDto;
import com.business.unknow.services.entities.Empresa;
import com.business.unknow.services.mapper.ContribuyenteMapper;
import com.business.unknow.services.mapper.EmpresaMapper;
import com.business.unknow.services.repositories.ContribuyenteRepository;
import com.business.unknow.services.repositories.EmpresaRepository;
import com.business.unknow.services.services.FilesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmpresaExecutorService {

  @Autowired private EmpresaRepository empresaRepository;

  @Autowired private ContribuyenteRepository contribuyenteRepository;

  @Autowired private EmpresaMapper empresaMapper;

  @Autowired private ContribuyenteMapper contribuyenteMapper;

  @Autowired private FilesService filesService;

  // TODO refactor this method to enable push company data to S3 directly
  public EmpresaDto createEmpresa(EmpresaDto empresaDto) {
    /*empresaDto.setFechaActualizacion(new Date());
    empresaDto.setFechaCreacion(new Date());
    String logo = empresaDto.getLogotipo();
    filesService.upsertResourceFile(
        new ResourceFileDto(
            ResourceFileEnum.CERT.name(),
            empresaDto.getInformacionFiscal().getRfc(),
            TipoRecursoEnum.EMPRESA.name(),
            empresaDto.getCertificado()));
    filesService.upsertResourceFile(
        new ResourceFileDto(
            ResourceFileEnum.KEY.name(),
            empresaDto.getInformacionFiscal().getRfc(),
            TipoRecursoEnum.EMPRESA.name(),
            empresaDto.getLlavePrivada()));
    filesService.upsertResourceFile(
        new ResourceFileDto(
            ResourceFileEnum.LOGO.name(),
            empresaDto.getInformacionFiscal().getRfc(),
            TipoRecursoEnum.EMPRESA.name(),
            logo.substring(logo.indexOf("base64") + 7)));
    Contribuyente contribuyente =
        contribuyenteRepository.save(
            contribuyenteMapper.getEntityFromContribuyenteDto(empresaDto.getInformacionFiscal()));
    */
    Empresa empresa = empresaMapper.getEntityFromEmpresaDto(empresaDto);
    return empresaMapper.getEmpresaDtoFromEntity(empresaRepository.save(empresa));
  }
}
