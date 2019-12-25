package com.business.unknow.commons.validator;

import com.business.unknow.Constants;
import com.business.unknow.model.PagoDto;
import com.business.unknow.model.error.InvoiceManagerException;

public class PagoValidator extends AbstractValidator{

	public void validatePagoCreator(PagoDto currentPagoDto,PagoDto pagoDto) throws InvoiceManagerException {
		if (currentPagoDto.getUltimoUsuario().equalsIgnoreCase(pagoDto.getUltimoUsuario())
				|| currentPagoDto.getCreateUser().equalsIgnoreCase(pagoDto.getUltimoUsuario())) {
			throw new InvoiceManagerException("Mismo usuario no puede actualizar el pago",
					"La actualizacion del pago no puede ser realizada por el mismo usuario de manera consecutiva",
					Constants.HTTP_SSTATUS_CONFLICT);
		}
	}
}