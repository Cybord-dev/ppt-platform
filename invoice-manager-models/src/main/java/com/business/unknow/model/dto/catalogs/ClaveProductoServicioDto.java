/**
 * 
 */
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

/**
 * @author ralfdemoledor
 *
 */
@Jacksonized
@Builder
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClaveProductoServicioDto implements Serializable {

	private static final long serialVersionUID = -8554744373606960846L;
	private Integer clave;
	private String descripcion;
	private String similares;
	private Date inicioVigencia;

}
