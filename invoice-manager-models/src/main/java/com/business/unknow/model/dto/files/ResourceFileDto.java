/**
 * 
 */
package com.business.unknow.model.dto.files;

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

/**
 * @author ralfdemoledor
 *
 * ResourceFileDto is a class created to saves metadata information related to S3 bucket files,
 * where tipoArchivo  stores the file type(ex: CERT,KEY,LOGO,IMAGEN), tipoRecurso describe the
 * origin of the resource (ex: EMPRESA,PAGO, FACTURA, DOC, etc), referencia links the resource
 * with an unique identifier of other tables(ex: 3_AME1RASE20200420093307PUE, AME140512D80)
 *
 *
 */
@Jacksonized
@Builder
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResourceFileDto implements Serializable {

	private static final long serialVersionUID = -8750055024664848580L;
	private Integer id;
	private String tipoArchivo;
	private String referencia;
	private String tipoRecurso;
	private String filename;
	@Deprecated// dont use data, this field will be removed once S3 refactor will be completed
	private String data;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.JSON_DATE_FORMAT)
	private Date fechaCreacion;

}
