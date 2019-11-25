package com.business.unknow.services.services.evaluations;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;

import com.business.unknow.model.context.FacturaContext;
import com.business.unknow.model.error.InvoiceManagerException;
import com.business.unknow.model.files.FacturaFileDto;
import com.business.unknow.services.mapper.CfdiMapper;
import com.business.unknow.services.mapper.FacturaMapper;
import com.business.unknow.services.mapper.FilesMapper;
import com.business.unknow.services.repositories.facturas.CfdiRepository;
import com.business.unknow.services.repositories.facturas.FacturaRepository;
import com.business.unknow.services.repositories.files.FacturaFileRepository;

public class AbstractFacturaServiceEvaluator {

	@Autowired
	private FacturaRepository repository;

	@Autowired
	private FacturaFileRepository facturaFileRepository;

	@Autowired
	private CfdiRepository cfdiRepository;

	@Autowired
	private FacturaMapper mapper;
	
	@Autowired
	private FilesMapper filesMapper;

	@Autowired
	private CfdiMapper cfdiMapper;

	protected void validateFacturaContext(FacturaContext facturaContexrt) throws InvoiceManagerException {
		if (!facturaContexrt.isValid()) {
			throw new InvoiceManagerException(facturaContexrt.getRuleErrorDesc(), facturaContexrt.getSuiteError(),
					HttpStatus.SC_BAD_REQUEST);
		}
	}

	protected void updateFacturaValues(FacturaContext context) {
		repository.save(mapper.getEntityFromFacturaDto(context.getFacturaDto()));
	}

	protected void updateFacturaAndCfdiValues(FacturaContext context) {
		repository.save(mapper.getEntityFromFacturaDto(context.getFacturaDto()));
		cfdiRepository.save(cfdiMapper.getEntityFromCfdiDto(context.getFacturaDto().getCfdi()));
		for (FacturaFileDto facturaFileDto : context.getFacturaFilesDto())
			if (facturaFileDto != null) {
				facturaFileRepository.save(filesMapper.getFacturaFileFromDto(facturaFileDto));
			}
	}

}