package com.business.unknow.rules.facturar;

import org.jeasy.rules.annotation.Action;
import org.jeasy.rules.annotation.Condition;
import org.jeasy.rules.annotation.Fact;
import org.jeasy.rules.annotation.Rule;

import com.business.unknow.enums.FacturaStatusEnum;
import com.business.unknow.model.context.FacturaContext;
import com.business.unknow.rules.common.Constants.FacturaSuite;

@Rule(name = FacturaSuite.FACTURA_STATUS, description = FacturaSuite.FACTURA_STATUS_RULE)
public class FacturaStatusRule {

	@Condition
	public boolean condition(@Fact("facturaContext") FacturaContext fc) {
		return fc.getFacturaDto().getStatusFactura().equals(FacturaStatusEnum.TIMBRADA.getValor())
				|| fc.getFacturaDto().getStatusFactura().equals(FacturaStatusEnum.CANCELADA.getValor())
						|| fc.getFacturaDto().getStatusFactura().equals(FacturaStatusEnum.RECHAZO_OPERACIONES.getValor())
								|| fc.getFacturaDto().getStatusFactura().equals(FacturaStatusEnum.RECHAZO_TESORERIA.getValor());
	}

	@Action
	public void execute(@Fact("facturaContext") FacturaContext fc) {
		fc.setRuleErrorDesc(FacturaSuite.FACTURA_STATUS_RULE_DESC);
		fc.setSuiteError(String.format("Error durante : %s", FacturaSuite.FACTURAR_SUITE));
		fc.setValid(false);
	}
}
