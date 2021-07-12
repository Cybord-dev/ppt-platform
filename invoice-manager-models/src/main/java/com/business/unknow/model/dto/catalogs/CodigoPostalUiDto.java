package com.business.unknow.model.dto.catalogs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Jacksonized
@Builder
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CodigoPostalUiDto implements Serializable {

	private static final long serialVersionUID = 2379130470017878430L;

	private String codigo_postal;
	private String municipio;
	private String estado;
	@Builder.Default private List<String> colonias = new ArrayList<>();


}
