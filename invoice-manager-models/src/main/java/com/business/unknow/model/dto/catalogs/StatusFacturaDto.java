package com.business.unknow.model.dto.catalogs;

import java.io.Serializable;
import java.util.Date;

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
public class StatusFacturaDto implements Serializable {

	private static final long serialVersionUID = 6605485841161408524L;

	private Integer id;
	private String statusEvento;
	private String statusPago;
	private String statusDevolucion;
	private Date fechaCreacion;
	private Date fechaActualizacion;


}
