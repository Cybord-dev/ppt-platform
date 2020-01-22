package com.business.unknow.commons.validator;

import com.business.unknow.Constants;
import com.business.unknow.model.dto.FacturaDto;
import com.business.unknow.model.dto.cfdi.CfdiDto;
import com.business.unknow.model.dto.services.PagoDto;
import com.business.unknow.model.error.InvoiceManagerException;

public class FacturaValidator extends AbstractValidator{

	public void validatePostFactura(FacturaDto dto) throws InvoiceManagerException {
		checkNotNull(dto.getRfcEmisor(), "Rfc Emisor");
		checkNotNull(dto.getRazonSocialEmisor(), "Razon Social Emisor");
		checkNotNull(dto.getRfcRemitente(), "Rfc Remitente");
		checkNotNull(dto.getRazonSocialRemitente(), "Razon Social Remitente");
	}
	
	public void validatePosComplementoDto(FacturaDto dto,String folio) throws InvoiceManagerException {
		checkNotNull(dto.getFolioPadre(), "folio padre");
		if(!folio.equals(dto.getFolioPadre())) {
			throw new InvoiceManagerException("Error al crear Complemento","Los folios son diferentes",
					Constants.BAD_REQUEST);
		}
		if(folio.equals(dto.getFolio())) {
			throw new InvoiceManagerException("Error al crear Complemento","El folio del padre no puede ser igual al del hijo",
					Constants.BAD_REQUEST);
		}
		checkNotNull(dto.getRfcEmisor(), "Rfc Emisor");
		checkNotNull(dto.getRazonSocialEmisor(), "Razon Social Emisor");
		checkNotNull(dto.getRfcRemitente(), "Rfc Remitente");
		checkNotNull(dto.getRazonSocialRemitente(), "Razon Social Remitente");
	}
	
	public void validateTimbrado(FacturaDto dto,String folio) throws InvoiceManagerException {
		checkNotNull(dto.getFolio(), "folio ");
		if(!folio.equals(dto.getFolio())) {
			throw new InvoiceManagerException("Error en folio","Los folios son diferentes",
					Constants.BAD_REQUEST);
		}
	}
	
	public void validatePostFacturaWithDetail(FacturaDto dto) throws InvoiceManagerException {
		checkNotNull(dto.getRfcEmisor(), "Rfc Emisor");
		checkNotNull(dto.getRazonSocialEmisor(), "Razon Social Emisor");
		checkNotNull(dto.getRfcRemitente(), "Rfc Remitente");
		checkNotNull(dto.getRazonSocialRemitente(), "Razon Social Remitente");
		checkNotNull(dto.getCfdi(), "cfdi");
		checkNotNull(dto.getCfdi().getReceptor(), "RFC receptor");
		checkNotNull(dto.getCfdi().getEmisor(), "RFC Emisor");
		//TODO validate all mandatory fields CFDI here or make a CFDI validator
		
	}
	
	public void validatePostCfdi(CfdiDto dto,String folio) throws InvoiceManagerException {
		if(!folio.equals(dto.getFolio())) {
			throw new InvoiceManagerException("Error al crear Cfdi","Los folios son diferentes",
					Constants.BAD_REQUEST);
		}
	}
	
	public void validatePago(PagoDto dto,String folio) throws InvoiceManagerException {
		if(!folio.equals(dto.getFolio())) {
			throw new InvoiceManagerException("Error al crear pago","Los folios son diferentes",
					Constants.BAD_REQUEST);
		}
	}

	

}
