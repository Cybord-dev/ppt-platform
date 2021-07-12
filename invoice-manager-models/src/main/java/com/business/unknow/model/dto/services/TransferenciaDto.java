package com.business.unknow.model.dto.services;

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
public class TransferenciaDto implements Serializable {

	private static final long serialVersionUID = 39453424147826945L;
	private Integer id;
	private String bancoRetiro;
	private String rfcRetiro;
	private String cuentaRetiro;
	private String lineaRetiro;
	private String bancoDeposito;
	private String rfcDeposito;
	private String cuentaDeposito;
	private String lineaDeposito;
	private String folio;
	private Double importe;
	private Date fechaCreacion;

}
