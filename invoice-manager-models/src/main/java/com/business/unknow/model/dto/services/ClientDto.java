package com.business.unknow.model.dto.services;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.business.unknow.Constants;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@Builder
@Getter
@Setter
@ToString
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

}
