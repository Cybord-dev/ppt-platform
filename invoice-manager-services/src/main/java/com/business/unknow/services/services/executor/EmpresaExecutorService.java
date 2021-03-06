package com.business.unknow.services.services.executor;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.business.unknow.enums.ResourceFileEnum;
import com.business.unknow.enums.TipoRecursoEnum;
import com.business.unknow.model.dto.files.ResourceFileDto;
import com.business.unknow.model.dto.services.EmpresaDto;
import com.business.unknow.services.entities.Contribuyente;
import com.business.unknow.services.entities.Empresa;
import com.business.unknow.services.mapper.ContribuyenteMapper;
import com.business.unknow.services.mapper.EmpresaMapper;
import com.business.unknow.services.repositories.ContribuyenteRepository;
import com.business.unknow.services.repositories.EmpresaRepository;
import com.business.unknow.services.services.FilesService;

@Service
public class EmpresaExecutorService {

	@Autowired
	private EmpresaRepository empresaRepository;

	@Autowired
	private ContribuyenteRepository contribuyenteRepository;

	@Autowired
	private EmpresaMapper empresaMapper;

	@Autowired
	private ContribuyenteMapper contribuyenteMapper;

	@Autowired
	private FilesService filesService;

	public EmpresaDto createEmpresa(EmpresaDto empresaDto) {
		empresaDto.getInformacionFiscal().setFechaActualizacion(new Date());
		empresaDto.getInformacionFiscal().setFechaCreacion(new Date());
		String logo = empresaDto.getLogotipo();
		filesService.upsertResourceFile(
				new ResourceFileDto(ResourceFileEnum.CERT.name(), empresaDto.getInformacionFiscal().getRfc(),
						TipoRecursoEnum.EMPRESA.name(), empresaDto.getCertificado()));
		filesService.upsertResourceFile(
				new ResourceFileDto(ResourceFileEnum.KEY.name(), empresaDto.getInformacionFiscal().getRfc(),
						TipoRecursoEnum.EMPRESA.name(), empresaDto.getLlavePrivada()));
		filesService.upsertResourceFile(
				new ResourceFileDto(ResourceFileEnum.LOGO.name(), empresaDto.getInformacionFiscal().getRfc(),
						TipoRecursoEnum.EMPRESA.name(), logo.substring(logo.indexOf("base64") + 7)));
		Contribuyente contribuyente = contribuyenteRepository
				.save(contribuyenteMapper.getEntityFromContribuyenteDto(empresaDto.getInformacionFiscal()));
		Empresa empresa = empresaMapper.getEntityFromEmpresaDto(empresaDto);
		empresa.setInformacionFiscal(contribuyente);
		return empresaMapper.getEmpresaDtoFromEntity(empresaRepository.save(empresa));
	}

	public void updateLogo(String rfc, String data) {
		if (data != null) {
			filesService.upsertResourceFile(new ResourceFileDto(ResourceFileEnum.LOGO.name(), rfc,
					TipoRecursoEnum.EMPRESA.name(), data.substring(data.indexOf("base64") + 7)));
		}
	}

	public void updateCertificado(String rfc, String data) {
		if (data != null) {
			filesService.upsertResourceFile(
					new ResourceFileDto(ResourceFileEnum.CERT.name(), rfc, TipoRecursoEnum.EMPRESA.name(), data));
		}
	}

	public void updateKey(String rfc, String data) {
		if (data != null) {
			filesService.upsertResourceFile(
					new ResourceFileDto(ResourceFileEnum.KEY.name(), rfc, TipoRecursoEnum.EMPRESA.name(), data));
		}
	}

}
