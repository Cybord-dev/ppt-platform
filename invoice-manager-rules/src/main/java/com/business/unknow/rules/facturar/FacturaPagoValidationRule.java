package com.business.unknow.rules.facturar;

import org.jeasy.rules.annotation.Action;
import org.jeasy.rules.annotation.Condition;
import org.jeasy.rules.annotation.Fact;
import org.jeasy.rules.annotation.Rule;

import com.business.unknow.enums.RevisionPagosEnum;
import com.business.unknow.model.context.FacturaContext;
import com.business.unknow.rules.common.Constants.FacturaSuite;

@Rule(name = FacturaSuite.FACTURA_PAGO_VALIDATION, description = FacturaSuite.FACTURA_PAGO_VALIDATION_RULE)
public class FacturaPagoValidationRule {

	@Condition
	public boolean condition(@Fact("facturaContext") FacturaContext fc) {
		return fc.getPagos() == null || fc.getPagos().isEmpty() || fc.getPagos().stream()
				.anyMatch(a -> !a.getStatusPago().equals(RevisionPagosEnum.ACEPTADO.getDescripcion()));

	}

	@Action
	public void execute(@Fact("facturaContext") FacturaContext fc) {
		fc.setRuleErrorDesc(FacturaSuite.FACTURA_PAGO_VALIDATION_RULE_DES);
		fc.setSuiteError(String.format("Error durante : %s", FacturaSuite.FACTURAR_SUITE));
		fc.setValid(false);
	}
}