package com.business.unknow.services.entities.factura;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "RETENCIONES")
public class Retencion implements Serializable {

	private static final long serialVersionUID = -2655293148503394319L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID_RETENCION")
	private Integer id;

	@Column(name = "ID_CONCEPTO")
	private Integer idConcepto;

	@Column(name = "BASE")
	private String base;

	@Column(name = "IMPUESTO")
	private Double impuesto;

	@Column(name = "TIPO_FACTOR")
	private String tipoFactor;

	@Column(name = "TASA_CUOTA")
	private Double tasaOCuota;

	@Column(name = "IMPORTE")
	private Double importe;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getBase() {
		return base;
	}

	public void setBase(String base) {
		this.base = base;
	}

	public Double getImpuesto() {
		return impuesto;
	}

	public void setImpuesto(Double impuesto) {
		this.impuesto = impuesto;
	}

	public String getTipoFactor() {
		return tipoFactor;
	}

	public void setTipoFactor(String tipoFactor) {
		this.tipoFactor = tipoFactor;
	}

	public Double getTasaOCuota() {
		return tasaOCuota;
	}

	public void setTasaOCuota(Double tasaOCuota) {
		this.tasaOCuota = tasaOCuota;
	}

	public Double getImporte() {
		return importe;
	}

	public void setImporte(Double importe) {
		this.importe = importe;
	}

	public Integer getIdConcepto() {
		return idConcepto;
	}

	public void setIdConcepto(Integer idConcepto) {
		this.idConcepto = idConcepto;
	}

	@Override
	public String toString() {
		return "Retencion [id=" + id + ", idConcepto=" + idConcepto + ", base=" + base + ", impuesto=" + impuesto
				+ ", tipoFactor=" + tipoFactor + ", tasaOCuota=" + tasaOCuota + ", importe=" + importe + "]";
	}

}