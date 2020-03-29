package com.business.unknow.rules.common;

/**
 * @author eej000f
 *
 */
public class Constants {
	
	public class FacturaSuite {
		
		public static final String FACTURA_SUITE = "FacturaSuite";
		
		public static final String EMISOR_VALIDATION = "EmisorValidation";
		public static final String EMISOR_VALIDATION_RULE = "EmisorValidationRule";
		public static final String EMISOR_VALIDATION_RULE_DESC = "La empresa Emisora no se encunetra activa en el sistema";
		
		public static final String RECEPTOR_VALIDATION = "ReceptorValidation";
		public static final String RECEPTOR_VALIDATION_RULE = "ReceptorValidationRule";
		public static final String RECEPTOR_VALIDATION_RULE_DESC = "La informacion del receptor no es valida";
		
	}
	
	public class DeletePagoSuite {
		public static final String DELETE_PAGO_SUITE = "DeletePagoSuite";
		
		public static final String DELETE_STATUS_PAYMENT = "DeletePpdPayment";
		public static final String DELETE_STATUS_PAYMENT_RULE = "DeletePpdPaymentRule";
		public static final String DELETE_STATUS_PAYMENT_RULE_DESC = "El estatus del pago o de la factura no permiten el borrado";
		
		public static final String DELETE_PAYMENT = "DeletePayment";
		public static final String DELETE_PAYMENT_RULE = "DeletePaymentRule";
		public static final String DELETE_PAYMENT_RULE_DESC = "No se puede borrar el pago por que hay incosistencias";
		
		public static final String DELETE_CREDIT_PAYMANT = "DeleteCreditPaymant";
		public static final String DELETE_CREDIT_PAYMANT_RULE = "DeleteCreditPaymantRule";
		public static final String DELETE_CREDIT_PAYMANT_RULE_DESC = "En facturas PPD no puede ser borrado el pago a credito";
	}
	
	public class PaymentsSuite {
		public static final String PAGO_PPD_SUITE = "PagoPpdSuite";
		public static final String PAGO_PUE_SUITE = "PagoPueSuite";
		
		public static final String MONTO_PAGO_VALIDATION = "MontoPagoValidation";
		public static final String MONTO_PAGO_VALIDATION_RULE = "MontoPagoValidationRule";
		public static final String MONTO_PAGO_VALIDATION_RULE_DESC = "Monto invalido de pago, el pago  no puede ser superior al monto faltante por acreditar en la factura  o menor a $0:00.";
		
		public static final String CREATE_CREDIT_VALIDATION = "CreateCreditValidation";
		public static final String CREATE_CREDIT_VALIDATION_RULE = "CreateCreditValidationRule";
		public static final String CREATE_CREDIT_VALIDATION_RULE_DESC = "El pago  no puede ser superior al monto faltante por acreditar en la factura.";
		
		public static final String ORDER_PAYMENT_VALIDATION = "PaymentOrderValidation";
		public static final String ORDER_PAYMENT_VALIDATION_RULE = "PaymentOrderValidationRule";
		public static final String ORDER_PAYMENT_VALIDATION_RULE_DESC = "Incongruencia en la validacion de pagos, el segundo pago no puede ser validado si el primer pago no ha sido validado.";
		
		public static final String DOUBLE_PAYMENT_VALIDATION = "DoubleOrderValidation";
		public static final String DOUBLE_PAYMENT_VALIDATION_RULE = "DoubleOrderValidationRule";
		public static final String DOUBLE_PAYMENT_VALIDATION_RULE_DESC = "Incongruencia en la validacion del segundo pago, el primer pago  no ha sido validado.";
		
		public static final String CONFLICT_PAYMENT_VALIDATION = "ConflictOrderValidation";
		public static final String CONFLICT_PAYMENT_VALIDATION_RULE = "ConflictOrderValidationRule";
		public static final String CONFLICT_PAYMENT_VALIDATION_RULE_DESC = "Incongruencia en la validacion del pago.";
		
	}
	

	public class Prevalidations {
		public static final String PREVALIDATION_SUITE = "PrevalidationSuite";
		
		public static final String FACTURA_PADRE_COMPLEMENTO = "FacturaPadreComplemento";
		public static final String FACTURA_PADRE_COMPLEMENTO_RULE = "FacturaPadreComplementoRule";
		public static final String FACTURA_PADRE_COMPLEMENTO_RULE_DESC = "La factura padre no cumple los requerimientos minimos para poder tener complementos";

		public static final String FACTURA_PADRE_PAGOS = "FacturaPadrePagos";
		public static final String FACTURA_PADRE_PAGOS_RULE = "FacturaPadrePagosRule";
		public static final String FACTURA_PADRE_PAGOS_RULE_DESC = "Existe una inconsitencia con los pagos";

		public static final String FACTURA_PADRE_STATUS = "FacturaPadrePagos";
		public static final String FACTURA_PADRE_STATUS_RULE = "FacturaPadrePagosRule";
		public static final String FACTURA_PADRE_STATUS_RULE_DESC = "Los estatus de la factura padre no son correctos";
	}

	public class Timbrado {
		public static final String TIMBRADO_SUITE = "FacturarSuite";
		
		public static final String TIMBRADO_STATUS = "FacturaStatus";
		public static final String TIMBRADO_STATUS_RULE = "FacturaStatusRule";
		public static final String TIMBRADO_STATUS_RULE_DESC = "La estatus de la factura no es correcta.";
		
		public static final String TIMBRADO_DATOS_VALIDATION = "FacturaDatosValidation";
		public static final String TIMBRADO_DATOS_VALIDATION_RULE = "FacturaDatosValidationRule";
		public static final String TIMBRADO_DATOS_VALIDATION_RULE_DESC = "Los datos de la factura tienen una inconsistencia.";
		
		public static final String TIMBRADO_PADRE_STATUS = "FacturaPadreStatusValidation";
		public static final String TIMBRADO_PADRE_STATUS_RULE = "FacturaPadreStatusValidationRule";
		public static final String TIMBRADO_PADRE_STATUS_RULE_DESC = "Los datos de la factura padre tienen una inconsistencia.";
		
		public static final String TIMBRADO_PAGO_VALIDATION = "FacturaPagoValidation";
		public static final String TIMBRADO_PAGO_VALIDATION_RULE = "FacturaPagoValidationRule";
		public static final String TIMBRADO_PAGO_VALIDATION_RULE_DES = "Los datos de la factura padre tienen una inconsistencia.";
	}
	
	public class CancelacionSuite {
		public static final String CANCELAR_SUITE = "CancelarSuite";
		
		public static final String CANCELAR_STATUS_VALIDATION = "StatusCancelarValidation";
		public static final String CANCELAR_STATUS_VALIDATION_RULE = "StatusCancelarValidationRule";
		public static final String CANCELAR_STATUS_VALIDATION_RULE_DESC = "Los status de la facura son incorrectos.";
		
		
	}

	public class DevolucionSuite {
		public static final String DEVOLUCION_SUITE = "DevolucionSuite";
		
		public static final String CLIENT_VALIDATION = "ClientValidation";
		public static final String CLIENT_VALIDATION_RULE = "ClientValidationRule";
		public static final String CLIENT_VALIDATION_RULE_DESC = "No se pueden generar devoluciones a clientes inactivos.";
		
		public static final String FACTURA_VALIDATION = "FacturaValidation";
		public static final String FACTURA_VALIDATION_RULE = "FacturaValidationRule";
		public static final String FACTURA_VALIDATION_RULE_DESC = "La factura debe estar timbrada para solicitar la devolucion.";
		
		public static final String FACTURA_PUE_STATUS_DEVOLUCION = "FacturaPueStatusDevolucion";
		public static final String FACTURA_PUE_STATUS_DEVOLUCION_RULE = "FacturaPueStatusDevolucionRule";
		public static final String FACTURA_PUE_STATUS_DEVOLUCION_RULE_DESC = "El estatus de devolucion de la factura Pue es incorrecto.";
		
		public static final String FACTURA_PPD_STATUS_DEVOLUCION = "FacturaPpdStatusDevolucion";
		public static final String FACTURA_PPD_STATUS_DEVOLUCION_RULE = "FacturaPpdStatusDevolucionRule";
		public static final String FACTURA_PPD_STATUS_DEVOLUCION_RULE_DESC = "El estatus de devolucion deL complemento es incorrecto.";
		
		public static final String PAGO_DEVOLUCION = "PagoDevolcuion";
		public static final String PAGO_DEVOLUCION_RULE = "PagoDevolcuionRule";
		public static final String PAGO_DEVOLUCION_RULE_DESC = "El estatus del pago es incorrecto.";
		
		
		
	}
}
