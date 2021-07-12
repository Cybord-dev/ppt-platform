package com.business.unknow.model.dto.services;


import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.business.unknow.Constants;
import com.business.unknow.model.dto.files.ResourceFileDto;
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
public class EmpresaDto implements Serializable {

	private static final long serialVersionUID = -5354660274346579595L;



	private int id;

	private Boolean activo;
	private String estatusEmpresa;
	private String tipo;
	private Integer giro;
	private String regimenFiscal;


	private String rfc;
	private String nombre;
	private String razonSocial;

	private String calle;
	private String noExterior;
	private String noInterior;
	private String municipio;
	private String estado;
	private String pais;
	private String cp;


	private Integer anioAlta;
	private String registroPatronal;
	private String estatusJuridico;
	private String estatusJuridico2;
	public String representanteLegal;
	public String ciec;  //  Clave de Identificación Electrónica Confidencial (CIEC). Ahora llamada solo Contraseña del SAT



	private String web;

	private String correo;
	private String pwCorreo;
	private String dominioCorreo;
	private String fiel; // FIEL (Firma Electrónica Avanzada), que es una contraseña para timbrado
	private String noCertificado;
	private LocalDate expiracionCertificado;


	private String actividadSAT;

	@Builder.Default private List<DatoAnualEmpresaDto> ingresos = new ArrayList<>();
	@Builder.Default private List<ResourceFileDto> documentos = new ArrayList<>();
	@Builder.Default private List<ObservacionDto> observaciones = new ArrayList<>();



	// TODO remove certificate and key dependencies
	@Deprecated // this field will be removed after S3 migration is completed
	private String certificado;
	@Deprecated
	private String llavePrivada;


	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.JSON_DATE_FORMAT)
	private Date fechaCreacion;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.JSON_DATE_FORMAT)
	private Date fechaActualizacion;
}
