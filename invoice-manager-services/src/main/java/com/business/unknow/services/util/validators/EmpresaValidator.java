package com.business.unknow.services.util.validators;

import com.business.unknow.model.dto.services.EmpresaDto;
import com.business.unknow.model.error.InvoiceManagerException;

public class EmpresaValidator extends AbstractValidator {

  /*
   *
   * TODO validate not null only next values
   *
     private Boolean activo;
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
   * */
  public void validatePostEmpresa(EmpresaDto dto) throws InvoiceManagerException {
    checkNotNull(dto.getRfc(), "Rfc");
    checkNotNull(dto.getRazonSocial(), "Razon social");
    checkValidString(dto.getRazonSocial());
    checkNotEmpty(dto.getRazonSocial(), "Razon social");
    checkNotNull(dto.getRegimenFiscal(), "Regimen fiscal");
    checkNotNull(dto.getCp(), "Codigo postal");
    checkNotNull(dto.getMunicipio(), "Municipio");
    checkNotNull(dto.getEstado(), "Estado");
    checkNotNull(dto.getCalle(), "Calle");
    checkNotNull(dto.getGiro(), "Giro");
    checkNotNull(dto.getTipo(), "Tipo");
    checkNotEquals(dto.getTipo(), "*");
    checkNotNull(dto.getCorreo(), "Correo");
    checkValidEmail(dto.getCorreo());
    checkNotNull(dto.getCertificado(), "Certificado");
    checkNotNull(dto.getLlavePrivada(), "Llave privada");
  }
}
