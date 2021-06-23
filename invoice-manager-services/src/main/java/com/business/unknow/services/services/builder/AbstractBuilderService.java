package com.business.unknow.services.services.builder;

import com.business.unknow.enums.ResourceFileEnum;
import com.business.unknow.enums.TipoRecursoEnum;
import com.business.unknow.model.dto.FacturaDto;
import com.business.unknow.model.dto.files.ResourceFileDto;
import com.business.unknow.model.dto.services.EmpresaDto;
import com.business.unknow.model.error.InvoiceManagerException;
import com.business.unknow.services.services.FilesService;
import org.springframework.beans.factory.annotation.Autowired;

public class AbstractBuilderService {

  @Autowired private FilesService service;

  public void getEmpresaFiles(EmpresaDto empresaDto, FacturaDto facturaDto)
      throws InvoiceManagerException {
    ResourceFileDto certFile =
        service.getResourceFileByResourceReferenceAndType(
            TipoRecursoEnum.EMPRESAS.name(),
            facturaDto.getRfcEmisor(),
            ResourceFileEnum.CERT.name());
    ResourceFileDto keyFile =
        service.getResourceFileByResourceReferenceAndType(
            TipoRecursoEnum.EMPRESAS.name(), facturaDto.getRfcEmisor(), ResourceFileEnum.KEY.name());
    empresaDto.setCertificado(certFile.getData());
    empresaDto.setLlavePrivada(keyFile.getData());
  }
}
