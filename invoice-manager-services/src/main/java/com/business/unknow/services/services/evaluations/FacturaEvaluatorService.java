package com.business.unknow.services.services.evaluations;

import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.RulesEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.business.unknow.model.context.FacturaContext;
import com.business.unknow.model.dto.FacturaDto;
import com.business.unknow.model.error.InvoiceManagerException;
import com.business.unknow.rules.suites.facturas.ComplementoSuite;
import com.business.unknow.rules.suites.facturas.FacturaSuite;

@Service
public class FacturaEvaluatorService extends AbstractEvaluatorService {

	@Autowired
	private FacturaSuite facturaSuite;
	
	@Autowired
	private ComplementoSuite complementoSuite;

	@Autowired
	private RulesEngine rulesEngine;

	public FacturaContext facturaEvaluation(FacturaDto facturaDto) throws InvoiceManagerException {
		FacturaContext facturaContext = buildFacturaContextCreateFactura(facturaDto);
		facturaDefaultValues.assignaDefaultsFactura(facturaContext.getFacturaDto());// not sure but all this defaults can be setup in a rule
		Facts facts = new Facts();
		facts.put("facturaContext", facturaContext);
		rulesEngine.fire(facturaSuite.getSuite(), facts);
		validateFacturaContext(facturaContext);
		return facturaContext;
	}
	
	public void complementoValidation(FacturaContext facturaContext)
			throws InvoiceManagerException {
		Facts facts = new Facts();
		facts.put("facturaContext", facturaContext);
		rulesEngine.fire(complementoSuite.getSuite(), facts);
		validateFacturaContext(facturaContext);
	}

}
