package com.business.unknow.model.dto.services;

import java.io.Serializable;
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
public class ContribuyenteDto implements Serializable {

	private static final long serialVersionUID = 963194607439661498L;
	private Integer id;
	private String rfc;
	private String giro;
	private String nombre;
	private boolean moral;
	private String curp;
	private String razonSocial;
	private String calle;
	private String noExterior;
	private String noInterior;
	private String municipio;
	private String localidad;
	private String estado;
	private String pais;
	private String coo;
	private String cp;
	private String correo;
	private String telefono;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.JSON_DATE_FORMAT)
	private Date fechaCreacion;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.JSON_DATE_FORMAT)
	private Date fechaActualizacion;


}
