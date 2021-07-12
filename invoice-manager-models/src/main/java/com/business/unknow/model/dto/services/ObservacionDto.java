package com.business.unknow.model.dto.services;

import com.business.unknow.Constants;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;
import java.util.Date;


@Jacksonized
@Builder
@Getter
@Setter
@ToString
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ObservacionDto implements Serializable {

    private static final long serialVersionUID = -5306687061683976517L;

    private Integer id;
    private String rfc;
    private String area;
    private String observacion;
    private String detalles;
    private String notificante;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.JSON_DATE_FORMAT)
    private Date fechaCreacion;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.JSON_DATE_FORMAT)
    private Date fechaActualizacion;
}
