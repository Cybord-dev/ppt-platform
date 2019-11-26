package com.business.unknow.model.factura;

import java.io.Serializable;
import java.util.Date;

import com.business.unknow.model.factura.cfdi.components.CfdiDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class FacturaDto implements Serializable {

	private static final long serialVersionUID = -1019751668989298682L;
	private Integer id;
	private String rfcEmisor;
	private String rfcRemitente;
	private String razonSocialEmisor;
	private String razonSocialRemitente;
	private String folio;
	private String folioPadre;
	private String uuid;
	private Integer statusPago;
	private Integer statusDevolucion;
	private Integer statusFactura;
	private String statusDetail;
	private String tipoDocumento;
	private String formaPago;
	private String metodoPago;
	private String packFacturacion;
	private String notas;
	private Double total;
	private Double subtotal;
	private Double descuento;
	private Date fechaCreacion;
	private Date fechaCancelacion;
	private Date fechaActualizacion;
	private Date fechaTimbrado;
	private CfdiDto cfdi;

	public String getRfcEmisor() {
		return rfcEmisor;
	}

	public void setRfcEmisor(String rfcEmisor) {
		this.rfcEmisor = rfcEmisor;
	}

	public String getRfcRemitente() {
		return rfcRemitente;
	}

	public void setRfcRemitente(String rfcRemitente) {
		this.rfcRemitente = rfcRemitente;
	}

	public String getRazonSocialEmisor() {
		return razonSocialEmisor;
	}

	public void setRazonSocialEmisor(String razonSocialEmisor) {
		this.razonSocialEmisor = razonSocialEmisor;
	}

	public String getRazonSocialRemitente() {
		return razonSocialRemitente;
	}

	public void setRazonSocialRemitente(String razonSocialRemitente) {
		this.razonSocialRemitente = razonSocialRemitente;
	}

	public String getFolio() {
		return folio;
	}

	public void setFolio(String folio) {
		this.folio = folio;
	}

	public String getFolioPadre() {
		return folioPadre;
	}

	public void setFolioPadre(String folioPadre) {
		this.folioPadre = folioPadre;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public Integer getStatusPago() {
		return statusPago;
	}

	public void setStatusPago(Integer statusPago) {
		this.statusPago = statusPago;
	}

	public Integer getStatusDevolucion() {
		return statusDevolucion;
	}

	public void setStatusDevolucion(Integer statusDevolucion) {
		this.statusDevolucion = statusDevolucion;
	}

	public Integer getStatusFactura() {
		return statusFactura;
	}

	public void setStatusFactura(Integer statusFactura) {
		this.statusFactura = statusFactura;
	}

	public String getStatusDetail() {
		return statusDetail;
	}

	public void setStatusDetail(String statusDetail) {
		this.statusDetail = statusDetail;
	}

	public String getTipoDocumento() {
		return tipoDocumento;
	}

	public void setTipoDocumento(String tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
	}

	public String getFormaPago() {
		return formaPago;
	}

	public void setFormaPago(String formaPago) {
		this.formaPago = formaPago;
	}

	public String getMetodoPago() {
		return metodoPago;
	}

	public void setMetodoPago(String metodoPago) {
		this.metodoPago = metodoPago;
	}

	public String getNotas() {
		return notas;
	}

	public void setNotas(String notas) {
		this.notas = notas;
	}

	public Double getTotal() {
		return total;
	}

	public void setTotal(Double total) {
		this.total = total;
	}

	public Date getFechaCreacion() {
		return fechaCreacion;
	}

	public void setFechaCreacion(Date fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}

	public Date getFechaActualizacion() {
		return fechaActualizacion;
	}

	public void setFechaActualizacion(Date fechaActualizacion) {
		this.fechaActualizacion = fechaActualizacion;
	}

	public Date getFechaTimbrado() {
		return fechaTimbrado;
	}

	public void setFechaTimbrado(Date fechaTimbrado) {
		this.fechaTimbrado = fechaTimbrado;
	}

	public CfdiDto getCfdi() {
		return cfdi;
	}

	public void setCfdi(CfdiDto cfdi) {
		this.cfdi = cfdi;
	}

	public Double getSubtotal() {
		return subtotal;
	}

	public void setSubtotal(Double subtotal) {
		this.subtotal = subtotal;
	}

	public Double getDescuento() {
		return descuento;
	}

	public void setDescuento(Double descuento) {
		this.descuento = descuento;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getPackFacturacion() {
		return packFacturacion;
	}

	public void setPackFacturacion(String packFacturacion) {
		this.packFacturacion = packFacturacion;
	}

	public Date getFechaCancelacion() {
		return fechaCancelacion;
	}

	public void setFechaCancelacion(Date fechaCancelacion) {
		this.fechaCancelacion = fechaCancelacion;
	}

	@Override
	public String toString() {
		return "FacturaDto [id=" + id + ", rfcEmisor=" + rfcEmisor + ", rfcRemitente=" + rfcRemitente
				+ ", razonSocialEmisor=" + razonSocialEmisor + ", razonSocialRemitente=" + razonSocialRemitente
				+ ", folio=" + folio + ", folioPadre=" + folioPadre + ", uuid=" + uuid + ", statusPago=" + statusPago
				+ ", statusDevolucion=" + statusDevolucion + ", statusFactura=" + statusFactura + ", statusDetail="
				+ statusDetail + ", tipoDocumento=" + tipoDocumento + ", formaPago=" + formaPago + ", metodoPago="
				+ metodoPago + ", packFacturacion=" + packFacturacion + ", notas=" + notas + ", total=" + total
				+ ", subtotal=" + subtotal + ", descuento=" + descuento + ", fechaCreacion=" + fechaCreacion
				+ ", fechaActualizacion=" + fechaActualizacion + ", fechaTimbrado=" + fechaTimbrado + ", cfdi=" + cfdi
				+ "]";
	}

}
