package com.business.unknow.model.dto.services;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.business.unknow.Constants;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClientDto implements Serializable {

	private static final long serialVersionUID = 4951260411762447946L;

	private int id;
	private Boolean activo;
	private BigDecimal porcentajePromotor;
	private BigDecimal porcentajeCliente;
	private BigDecimal porcentajeDespacho;
	private BigDecimal porcentajeContacto;
	private String correoPromotor;
	private String correoContacto;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.JSON_DATE_FORMAT)
	private Date fechaCreacion;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.JSON_DATE_FORMAT)
	private Date fechaActualizacion;
	private ContribuyenteDto informacionFiscal;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Boolean getActivo() {
		return activo;
	}
	public void setActivo(Boolean activo) {
		this.activo = activo;
	}
	public BigDecimal getPorcentajePromotor() {
		return porcentajePromotor;
	}
	public void setPorcentajePromotor(BigDecimal porcentajePromotor) {
		this.porcentajePromotor = porcentajePromotor;
	}
	public BigDecimal getPorcentajeCliente() {
		return porcentajeCliente;
	}
	public void setPorcentajeCliente(BigDecimal porcentajeCliente) {
		this.porcentajeCliente = porcentajeCliente;
	}
	public BigDecimal getPorcentajeDespacho() {
		return porcentajeDespacho;
	}
	public void setPorcentajeDespacho(BigDecimal porcentajeDespacho) {
		this.porcentajeDespacho = porcentajeDespacho;
	}
	public BigDecimal getPorcentajeContacto() {
		return porcentajeContacto;
	}
	public void setPorcentajeContacto(BigDecimal porcentajeContacto) {
		this.porcentajeContacto = porcentajeContacto;
	}
	public String getCorreoPromotor() {
		return correoPromotor;
	}
	public void setCorreoPromotor(String correoPromotor) {
		this.correoPromotor = correoPromotor;
	}
	public String getCorreoContacto() {
		return correoContacto;
	}
	public void setCorreoContacto(String correoContacto) {
		this.correoContacto = correoContacto;
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
	public ContribuyenteDto getInformacionFiscal() {
		return informacionFiscal;
	}
	public void setInformacionFiscal(ContribuyenteDto informacionFiscal) {
		this.informacionFiscal = informacionFiscal;
	}
	@Override
	public String toString() {
		return "ClientDto [id=" + id + ", activo=" + activo + ", porcentajePromotor=" + porcentajePromotor
				+ ", porcentajeCliente=" + porcentajeCliente + ", porcentajeDespacho=" + porcentajeDespacho
				+ ", porcentajeContacto=" + porcentajeContacto + ", correoPromotor=" + correoPromotor
				+ ", correoContacto=" + correoContacto + ", fechaCreacion=" + fechaCreacion + ", fechaActualizacion="
				+ fechaActualizacion + ", informacionFiscal=" + informacionFiscal + "]";
	}
}
