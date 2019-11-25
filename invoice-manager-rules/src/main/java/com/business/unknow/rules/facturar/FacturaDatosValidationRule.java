package com.business.unknow.rules.facturar;

import org.jeasy.rules.annotation.Action;
import org.jeasy.rules.annotation.Condition;
import org.jeasy.rules.annotation.Fact;
import org.jeasy.rules.annotation.Rule;

import com.business.unknow.enums.MetodosPagoEnum;
import com.business.unknow.enums.TipoDocumentoEnum;
import com.business.unknow.model.context.FacturaContext;
import com.business.unknow.rules.common.Constants.FacturaSuite;

@Rule(name = FacturaSuite.FACTURA_DATOS_VALIDATION, description = FacturaSuite.FACTURA_DATOS_VALIDATION_RULE)
public class FacturaDatosValidationRule {

	@Condition
	public boolean condition(@Fact("facturaContext") FacturaContext fc) {
		
		if(fc.getFacturaDto().getFechaTimbrado() != null || fc.getFacturaDto().getUuid() != null) {
			return true;
		}else {
			if(fc.getFacturaDto().getTipoDocumento().equals(TipoDocumentoEnum.FACRTURA.getDescripcion())) {
				return fc.getFacturaDto().getFolioPadre() != null;
			}
			if(fc.getFacturaDto().getTipoDocumento().equals(TipoDocumentoEnum.COMPLEMENTO.getDescripcion())) {
				return fc.getFacturaDto().getFolioPadre() == null 
						&& fc.getFacturaDto().getMetodoPago().equals(MetodosPagoEnum.PPD.getNombre());
			}
			return true;//invalid document type
		}
	}

	@Action
	public void execute(@Fact("facturaContext") FacturaContext fc) {
		fc.setRuleErrorDesc(FacturaSuite.FACTURA_DATOS_VALIDATION_RULE_DESC);
		fc.setSuiteError(String.format("Error durante : %s", FacturaSuite.FACTURAR_SUITE));
		fc.setValid(false);
	}
}