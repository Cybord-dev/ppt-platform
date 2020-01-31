package com.business.unknow.services.services.executor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.business.unknow.client.swsapiens.util.SwSapiensClientException;
import com.business.unknow.enums.ResourceFileEnum;
import com.business.unknow.enums.TipoRecursoEnum;
import com.business.unknow.model.dto.services.EmpresaDto;
import com.business.unknow.model.error.InvoiceManagerException;
import com.business.unknow.services.mapper.EmpresaMapper;
import com.business.unknow.services.repositories.EmpresaRepository;

@Service
public class EmpresaExecutorService extends AbstractExecutorService{
	
	@Autowired
	private SwSapinsExecutorService swSapinsExecutorService;
	
	@Autowired
	private EmpresaRepository empresaRepository;
	
	@Autowired
	private EmpresaMapper empresaMapper;

	public EmpresaDto validateEmpresa(EmpresaDto empresaDto) throws InvoiceManagerException {
		try {
			swSapinsExecutorService.validateLco(empresaDto.getNoCertificado());
			createResourceFile(empresaDto.getCertificado(), empresaDto.getInformacionFiscal().getRfc(),
					TipoRecursoEnum.EMPRESA.name(),ResourceFileEnum.CERT.name());
			createResourceFile(empresaDto.getLlavePrivada(), empresaDto.getInformacionFiscal().getRfc(),
					TipoRecursoEnum.EMPRESA.name(),ResourceFileEnum.KEY.name());
			createResourceFile(empresaDto.getLogotipo(), empresaDto.getInformacionFiscal().getRfc(),
					TipoRecursoEnum.EMPRESA.name(),ResourceFileEnum.LOGO.name());
			return empresaMapper
					.getEmpresaDtoFromEntity(empresaRepository.save(empresaMapper.getEntityFromEmpresaDto(empresaDto)));
		} catch (SwSapiensClientException e) {
			throw new InvoiceManagerException("Error validando la empresa en LCO", e.getMessage(), e.getHttpStatus());
		}
	}

}