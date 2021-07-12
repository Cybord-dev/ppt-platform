package com.business.unknow.model.dto.catalogs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;

@Jacksonized
@Builder
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CodigoPostalDto implements Serializable {

	private static final long serialVersionUID = -2907308795601544511L;
	private int id;
	private String codigoPostal;
	private String estado;
	private String municipio;
	private String ciudad;
	private String colonia;

}
