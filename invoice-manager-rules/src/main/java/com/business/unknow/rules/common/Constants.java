package com.business.unknow.rules.common;

public class Constants {
	private Constants(){}
	
	public static class FacturaSuite {
		private FacturaSuite(){}
		public static final String FACTURA_SUITE = "FacturaSuite";
		public static final String EMISOR_VALIDATION = "EmisorValidation";
		public static final String EMISOR_VALIDATION_RULE = "EmisorValidationRule";
		public static final String EMISOR_VALIDATION_RULE_DESC = "La empresa Emisora no se encunetra activa en el sistema";
		
	}
	public static class DeletePagoSuite {
		private DeletePagoSuite(){}
		public static final String DELETE_PAYMENT = "DeletePayment";
		public static final String DELETE_STATUS_PAYMENT_RULE_DESC = "No se permite borrar el pago por motivos de seguridad";
		public static final String DELETE_STATUS_PAYMENT_RULE = "DeleteStatusPaymentRule";

	}
	
	
	public static class FacturaValidationSuite {
		private FacturaValidationSuite(){}
		public static final String FACTURA_VALIDATION_PUE_RULE = "ValidacionFacturaPueRule";
		public static final String FACTURA_VALIDATION_PUE_RULE_DESC = "Validar estado de la factura PUE";
		public static final String FACTURA_VALIDATION_PPD_RULE = "ValidacionFacturaPpdRule";
		public static final String FACTURA_VALIDATION_PPD_RULE_DESC = "Validar estado de la factura PPD";
		public static final String FACTURA_VALIDATION_COMP_RULE = "ValidacionFacturaComplementoRule";
		public static final String FACTURA_VALIDATION_COMP_RULE_DESC = "Validar estado del complemento";
		public static final String FACTURA_VALIDATION_NOTA_CREDITO_RULE = "ValidacionNotaCreditoRule";
		public static final String FACTURA_VALIDATION_NOTA_CREDITO_RULE_DESC = "Validar estado de la Nota de Credito";
	}
	
	public static class PaymentsSuite {
		private PaymentsSuite(){}
		public static final String MONTO_PAGO_VALIDATION = "MontoPagoValidation";
		public static final String MONTO_PAGO_VALIDATION_RULE = "MontoPagoValidationRule";
		public static final String MONTO_PAGO_VALIDATION_RULE_DESC = "Monto invalido de pago, la suma de los pagos no puede ser superior o diferente al moto total del pago raiz";
		public static final String ZERO_AMMOUNT_VALIDATION_RULE = "ZeroAmountValidationRule";
		public static final String ZERO_AMMOUNT_VALIDATION_RULE_DESC = "Uno o varios pagos asignados son  iguales o menores a $0.00.";
		public static final String ORDER_PAYMENT_VALIDATION_RULE = "PaymentOrderValidationRule";
		public static final String ORDER_PAYMENT_VALIDATION_RULE_DESC = "Incongruencia en la validacion de pagos, el segundo pago no puede ser validado si el primer pago no ha sido validado.";
		public static final String INVOICE_STATUS_PAYMENT_UPADTE_VALIDATION_RULE = "UpdatePaymentInvoiceStatusRulesRule";
		public static final String INVOICE_STATUS_PAYMENT_UPADTE_VALIDATION_RULE_DESC = "Incongruencia en el estatus de alguna de las facturas, los pagos de facturas rechazadas, canceladas no pueden validar pagos.";
		public static final String DOUBLE_PAYMENT_VALIDATION_RULE = "DoubleOrderValidationRule";
		public static final String DOUBLE_PAYMENT_VALIDATION_RULE_DESC = "Incongruencia en la validacion del segundo pago, el primer pago  no ha sido validado.";
		public static final String CREDIT_PAYMENT_VALIDATION_RULE = "CreditPaymentRule";
		public static final String CREDIT_PAYMENT_VALIDATION_RULE_DESC = "Las facturas PPD no pueden cargar pagos a credito.";
		public static final String CONFLICT_PAYMENT_VALIDATION_RULE = "ConflictOrderValidationRule";
		public static final String CONFLICT_PAYMENT_VALIDATION_RULE_DESC = "Incongruencia en la validacion del pago.";
		
	}
	

	public static class Prevalidations {
		private Prevalidations(){}
		public static final String FACTURA_PADRE_STATUS = "FacturaPadrePagos";
	}

	public static class Timbrado {
		private Timbrado(){}
		public static final String TIMBRADO_SUITE = "FacturarSuite";
		public static final String TIMBRADO_STATUS = "FacturaStatus";
		public static final String TIMBRADO_STATUS_RULE = "FacturaStatusRule";
		public static final String TIMBRADO_STATUS_RULE_DESC = "La estatus de la factura no es correcta.";
		public static final String TIMBRADO_DATOS_VALIDATION = "FacturaDatosValidation";
		public static final String TIMBRADO_DATOS_VALIDATION_RULE = "FacturaDatosValidationRule";
		public static final String TIMBRADO_DATOS_VALIDATION_RULE_DESC = "Los datos de la factura tienen una inconsistencia.";
		public static final String - = "FacturaPadreStatusValidation";
		public static final String TIMBRADO_PADRE_STATUS_RULE = "FacturaPadreStatusValidationRule";
		public static final String TIMBRADO_PAGO_VALIDATION = "FacturaPagoValidation";
		public static final String TIMBRADO_PAGO_VALIDATION_RULE = "FacturaPagoValidationRule";
		public static final String TIMBRADO_PAGO_VALIDATION_RULE_DES = "Los pagos de la factura tienen incosistencias";
	}
	
	public static class CancelacionSuite {
		private CancelacionSuite(){}
		public static final String CANCELAR_SUITE = "CancelarSuite";
		public static final String CANCELAR_STATUS_VALIDATION = "StatusCancelarValidation";
		public static final String CANCELAR_STATUS_VALIDATION_RULE = "StatusCancelarValidationRule";
		public static final String CANCELAR_STATUS_VALIDATION_RULE_DESC = "Los status de la facura son incorrectos.";
	}

	public static class DevolucionSuite {
		private DevolucionSuite(){}
		public static final String DEVOLUCION_SUITE = "DevolucionSuite";
		public static final String CLIENT_VALIDATION = "ClientValidation";
		public static final String FACTURA_VALIDATION = "FacturaValidation";
		public static final String FACTURA_VALIDATION_RULE = "FacturaValidationRule";
		public static final String FACTURA_VALIDATION_RULE_DESC = "La factura debe estar timbrada para solicitar la devolucion.";
	}
}
