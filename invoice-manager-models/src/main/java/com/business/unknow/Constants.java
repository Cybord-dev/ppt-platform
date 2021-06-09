package com.business.unknow;

public  class Constants {

	private Constants(){}
	public static final String JSON_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
	public static final String DATE_STANDAR_FORMAT = "yyyy-MM-dd-hh:mm:ss";
	public static final String DATE_FOLIO_GENERIC_FORMAT = "yyyySSSMMhhssddmm";
	public static final String DATE_PRE_FOLIO_GENERIC_FORMAT = "MMyy";

	public static final Integer INTERNAL_ERROR = 500;
	public static final Integer MILISECONDS = 1000;
	public static final Integer BAD_REQUEST = 400;
	public static final Long MILISECONDS_PER_DAY = 86400000L;

	public static class FacturaSustitucionConstants {
		private FacturaSustitucionConstants(){}
		public static final String NOTA_CREDITO_USO_CFDI = "G02";
		public static final String NOTA_CREDITO_CLAVE_CONCEPTO="84111506";
		public static final String NOTA_CREDITO_CLAVE_UNIDAD="ACT";
		public static final String NOTA_CREDITO_DESC_CONCEPTO="Devolucion";
		public static final String NOTA_CREDITO_CLAVE_CONCEPTO_DESC="Servicios de facturación";
	}
	public static class FacturaConstants {
		private FacturaConstants(){}
		public static final String FACTURA_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
		public static final String FECHA_ATTRIBUTE = "Fecha";
		public static final String SELLO_ATTRIBUTE = "Sello";

		public static final String CADENA_ORIGINAL = "src/main/resources/factura-xslt/cadenaoriginal_3_3.xslt";
	}

	public static class CfdiConstants {
		private CfdiConstants(){}
		public static final String FACTURA_VERSION = "3.3";
		public static final String FACTURA_COMPLEMENTO_VERSION = "1.0";
	}
	
	public static class NtlinkModernaRequest{
		private NtlinkModernaRequest(){}
		public static final String ISER = "iser";
		public static final String TIMBRA_CFDI = "TimbraCfdiQr";
		public static final String CANCELA_CFDI = "CancelaCfdi";
		public static final String USER = "userName";
		public static final String PASS = "password";
		public static final String COMPROBANTE = "comprobante";
		public static final String RFC_EMISOR = "rfc";
		public static final String EXPRESION = "expresion";
		public static final String RFC_RECEPTOR = "rfcReceptor";
		public static final String UUID = "uuid";
		
		public static final String SOAP_ENV = "soapenv";
		public static final String SOAP_ENV_URL = "http://schemas.xmlsoap.org/soap/envelope/";
		
		public static final String ISER_URL = "https://ntlink.com.mx/IServicioTimbrado";
	}

	public static class FacturacionModernaRequest{
		private FacturacionModernaRequest(){}
		public static final String USER_PASS_PARAMETER = "UserPass";
		public static final String USER_ID_PARAMETER = "UserID";
		public static final String EMISOR_PARAMETER = "emisorRFC";
		public static final String UUID_PARAMETER = "uuid";
		public static final String TEXT_PARAMETER = "text2CFDI";
		public static final String GENERAR_TEXT_PARAMETER = "generarTXT";
		public static final String GENERAR_PDF_PARAMETER = "generarPDF";
		public static final String GENERAR_CBB_PARAMETER = "generarCBB";
		public static final String TYPE_PARAMETER = "xsi:type";
		public static final String STRING_TYPE_PARAMETER = "xsd:string";
		public static final String BOOLEAN_TYPE_PARAMETER = "xsd:boolean";
		
		public static final String REQUEST = "request";
		public static final String NS1 = "ns1";
		public static final String XSD = "xsd";
		public static final String XSI = "xsi";
		public static final String SOAP_ENV = "SOAP-ENV";
		public static final String SOAP_ENV_STRUCT="SOAP-ENC:Struct";

		public static final String URL_TIMBRADO = "https://t1demo.facturacionmoderna.com/timbrado/soap";
		public static final String URL_SCHEMA = "http://www.w3.org/2001/XMLSchema";
		public static final String URL_SCHEMA_INSTANCE = "http://www.w3.org/2001/XMLSchema-instance";
		public static final String URL_ENCODING = "http://schemas.xmlsoap.org/soap/envelope/";
	
		public static final String REQUEST_CANCELADO="requestCancelarCFDI";
		public static final String REQUEST_TIMBRADO="requestTimbrarCFDI";
	}
	
	public static  class PagoPpdCreditoDefaults{
		private PagoPpdCreditoDefaults(){}
		public static final String BANCO = "N/A";
		public static final String USER = "Sistema";
		public static final String COMENTARIO ="Pago Automatico por sistema";
		public static final String FORMA_PAGO ="CREDITO";
		public static final String MONEDA ="MXN";
		public static final String TIPO_CAMBIO ="1.0";
		public static final String STATUS_PAGO ="ACEPTADO";
		public static final String CUENTA = "CreditoPPD";
	}
	
	public static class ComplementoPpdDefaults{
		private ComplementoPpdDefaults(){}
		public static final String VERSION = "1.0";
		public static final String VERSION_CFDI = "3.3";
		public static final String MONEDA = "XXX";
		public static final String TOTAL = "0";
		public static final String SUB_TOTAL = "0";
		public static final String SERIE = "PFPC";
		public static final String COMPROBANTE = "P";
		public static final String USO_CFDI = "P01";
		
		public static final int CANTIDAD = 1;
		public static final String CLAVE_PROD = "84111506";
		public static final String CLAVE="ACT";
		public static final String DESCRIPCION="Pago";
		public static final String IMPORTE="0";
		public static final String VALOR_UNITARIO="0";
		public static final String METODO_PAGO="PPD";
		public static final String SERIE_PAGO="PFP";
		
		
	}
	
	public static class FacturaComplemento {
		private FacturaComplemento(){}
		public static final String FORMA_PAGO = "CREDITO";
		public static final String TOTAL = "Total=\"0.0\"";
		public static final String TOTAL_FINAL = "Total=\"0\"";
		public static final String SUB_TOTAL = "SubTotal=\"0.0\"";
		public static final String SUB_TOTAL_FINAL = "SubTotal=\"0\"";
	}
}