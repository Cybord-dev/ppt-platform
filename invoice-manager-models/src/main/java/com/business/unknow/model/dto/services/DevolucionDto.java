/**
 * 
 */
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
public class DevolucionDto implements Serializable {

	private static final long serialVersionUID = 6166507072002168875L;

	private Integer id;
	private Integer idPagoOrigen;
	private String tipo;
	private String folio;
	private BigDecimal pagoMonto;
	private BigDecimal impuesto;
	private BigDecimal porcentaje;
	private BigDecimal monto;
	private String receptor;
	private String tipoReceptor;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.JSON_DATE_FORMAT)
	private Date fechaCreacion;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.JSON_DATE_FORMAT)
	private Date fechaActualizacion;

}
