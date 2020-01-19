package com.business.unknow.services.services.evaluations;

import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.RulesEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.business.unknow.model.context.FacturaContext;
import com.business.unknow.model.error.InvoiceManagerException;
import com.business.unknow.model.factura.FacturaDto;
import com.business.unknow.rules.suites.facturas.FacturaSuite;

@Service
public class FacturaEvaluatorService extends AbstractEvaluatorService {

	@Autowired
	private FacturaSuite facturaSuite;

	@Autowired
	private RulesEngine rulesEngine;

	public FacturaContext facturaEvaluation(FacturaDto facturaDto) throws InvoiceManagerException {
		FacturaContext facturaContext = buildFacturaContextCreateFactura(facturaDto);
		facturaDefaultValues.assignaDefaultsFactura(facturaContext.getFacturaDto());// not sure but all this defaults can be setup in a rule
		Facts facts = new Facts();
		facts.put("facturaContext", facturaContext);
		rulesEngine.fire(facturaSuite.getSuite(), facts);
		validateFacturaContext(facturaContext);
//		Commenting not validation methods
//		CfdiDto cfdiDto = facturaContext.getFacturaDto().getCfdi();
//		
//		facturaContext.setFacturaDto(mapper.getFacturaDtoFromEntity(
//				repository.save(mapper.getEntityFromFacturaDto(facturaContext.getFacturaDto()))));
//		facturaContext.getFacturaDto().setCfdi(cfdiDto);
//		
//		facturaContext.getFacturaDto().setCfdi(cfdiEvaluatorService
//				.insertNewCfdi(facturaContext.getFacturaDto().getFolio(), facturaContext.getFacturaDto().getCfdi()));
//		if (facturaContext.getFacturaDto().getCfdi().getMetodoPago().equals(MetodosPagoEnum.PPD.getNombre())) {
//			pagoRepository.save(facturaDefaultValues.assignaDefaultsFacturaPPD(facturaContext.getFacturaDto()));//GENERACION PAGO SISTEMA PPD
//		}
		return facturaContext;
	}

}
