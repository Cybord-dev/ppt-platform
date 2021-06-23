package com.business.unknow.services.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "PAGOS")
public class Pago implements Serializable {

  private static final long serialVersionUID = 8371622895161409889L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID_PAGO")
  private Integer id;

  @NotEmpty
  @Column(name = "MONEDA")
  private String moneda;

  @NotEmpty
  @Column(name = "BANCO")
  private String banco;

  @NotEmpty
  @Column(name = "CUENTA")
  private String cuenta;

  @NotNull
  @Column(name = "TIPO_CAMBIO")
  private BigDecimal tipoDeCambio;

  /* DEPOSITO,TRANSFERENCIA, CHEQUE, EFECTIVO */
  @NotEmpty
  @Column(name = "FORMA_PAGO")
  private String formaPago;

  @Column(name = "MONTO")
  private BigDecimal monto;

  @Column(name = "STATUS_PAGO")
  private String statusPago;

  @Column(name = "COMENTARIO_PAGO")
  private String comentarioPago;

  @Column(name = "ACREDOR")
  private String acredor;

  @Column(name = "DEUDOR")
  private String deudor;

  @Column(name = "SOLICITANTE")
  private String solicitante;

  @Column(name = "REVISION_1", columnDefinition = "TINYINT")
  private Boolean revision1;

  @Column(name = "REVISION_2", columnDefinition = "TINYINT")
  private Boolean revision2;

  @Column(name = "REVISOR_1")
  private String revisor1;

  @Column(name = "REVISOR_2")
  private String revisor2;

  @Column(name = "FECHA_PAGO")
  private Date fechaPago;

  @Temporal(TemporalType.TIMESTAMP)
  @CreatedDate
  @Column(name = "FECHA_CREACION")
  private Date fechaCreacion;

  @Temporal(TemporalType.TIMESTAMP)
  @LastModifiedDate
  @Column(name = "FECHA_ACTUALIZACION")
  private Date fechaActualizacion;

  @Column(name = "FILE_FORMAT")
  private String fileFormat;

  @OneToMany(mappedBy = "pago", cascade = CascadeType.PERSIST)
  private List<PagoFactura> facturas;

  public Pago() {
    this.facturas = new ArrayList<>();
    this.tipoDeCambio = BigDecimal.ONE;
  }

  public void addFactura(PagoFactura factura) {
    this.facturas.add(factura);
    factura.setPago(this);
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getMoneda() {
    return moneda;
  }

  public void setMoneda(String moneda) {
    this.moneda = moneda;
  }

  public String getBanco() {
    return banco;
  }

  public void setBanco(String banco) {
    this.banco = banco;
  }

  public String getCuenta() {
    return cuenta;
  }

  public void setCuenta(String cuenta) {
    this.cuenta = cuenta;
  }

  public BigDecimal getTipoDeCambio() {
    return tipoDeCambio;
  }

  public void setTipoDeCambio(BigDecimal tipoDeCambio) {
    this.tipoDeCambio = tipoDeCambio;
  }

  public String getFormaPago() {
    return formaPago;
  }

  public void setFormaPago(String formaPago) {
    this.formaPago = formaPago;
  }

  public BigDecimal getMonto() {
    return monto;
  }

  public void setMonto(BigDecimal monto) {
    this.monto = monto;
  }

  public String getStatusPago() {
    return statusPago;
  }

  public void setStatusPago(String statusPago) {
    this.statusPago = statusPago;
  }

  public String getComentarioPago() {
    return comentarioPago;
  }

  public void setComentarioPago(String comentarioPago) {
    this.comentarioPago = comentarioPago;
  }

  public String getAcredor() {
    return acredor;
  }

  public void setAcredor(String acredor) {
    this.acredor = acredor;
  }

  public String getDeudor() {
    return deudor;
  }

  public void setDeudor(String deudor) {
    this.deudor = deudor;
  }

  public String getSolicitante() {
    return solicitante;
  }

  public void setSolicitante(String solicitante) {
    this.solicitante = solicitante;
  }

  public Boolean getRevision1() {
    return revision1;
  }

  public void setRevision1(Boolean revision1) {
    this.revision1 = revision1;
  }

  public Boolean getRevision2() {
    return revision2;
  }

  public void setRevision2(Boolean revision2) {
    this.revision2 = revision2;
  }

  public String getRevisor1() {
    return revisor1;
  }

  public void setRevisor1(String revisor1) {
    this.revisor1 = revisor1;
  }

  public String getRevisor2() {
    return revisor2;
  }

  public void setRevisor2(String revisor2) {
    this.revisor2 = revisor2;
  }

  public Date getFechaPago() {
    return fechaPago;
  }

  public void setFechaPago(Date fechaPago) {
    this.fechaPago = fechaPago;
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

  public List<PagoFactura> getFacturas() {
    return facturas;
  }

  public void setFacturas(List<PagoFactura> facturas) {
    this.facturas = facturas;
  }

  public String getFileFormat() {
    return fileFormat;
  }

  public void setFileFormat(String fileFormat) {
    this.fileFormat = fileFormat;
  }

  @Override
  public String toString() {
    return "Pago [id="
        + id
        + ", moneda="
        + moneda
        + ", banco="
        + banco
        + ", cuenta="
        + cuenta
        + ", tipoDeCambio="
        + tipoDeCambio
        + ", formaPago="
        + formaPago
        + ", monto="
        + monto
        + ", statusPago="
        + statusPago
        + ", comentarioPago="
        + comentarioPago
        + ", acredor="
        + acredor
        + ", deudor="
        + deudor
        + ", solicitante="
        + solicitante
        + ", revision1="
        + revision1
        + ", revision2="
        + revision2
        + ", revisor1="
        + revisor1
        + ", revisor2="
        + revisor2
        + ", fechaPago="
        + fechaPago
        + ", fechaCreacion="
        + fechaCreacion
        + ", fechaActualizacion="
        + fechaActualizacion
        + ", facturas="
        + facturas
        + "]";
  }
}
