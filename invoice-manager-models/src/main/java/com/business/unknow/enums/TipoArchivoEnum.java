package com.business.unknow.enums;

public enum TipoArchivoEnum {

	XML( "XML", ".xml", "text/plain"),
	QR( "QR", ".png", "N/A"),
	PDF( "PDF", ".pdf", "application/pdf"),
	TXT( "TXT", ".txt", "text/plain"),
	CERT( "CERT", ".crt", "N/A"),
	KEY( "KEY", ".key", "N/A");


	private String descripcion;
	private String format;
	private String byteArrayData;

	private TipoArchivoEnum( String descripcion, String format, String byteArrayData) {
		this.descripcion = descripcion;
		this.format = format;
		this.byteArrayData = byteArrayData;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public String getFormat() {
		return format;
	}

	public String getByteArrayData() {
		return byteArrayData;
	}
}
