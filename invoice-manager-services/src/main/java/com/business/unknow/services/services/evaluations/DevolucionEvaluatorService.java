package com.business.unknow.services.services.evaluations;

import org.jeasy.rules.api.Facts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.business.unknow.Constants;
import com.business.unknow.enums.ContactoDevolucionEnum;
import com.business.unknow.enums.DevolucionStatusEnum;
import com.business.unknow.enums.TipoDocumentoEnum;
import com.business.unknow.model.PagoDto;
import com.business.unknow.model.context.FacturaContext;
import com.business.unknow.model.error.InvoiceManagerException;
import com.business.unknow.model.factura.FacturaDto;
import com.business.unknow.rules.suites.DevolucionSuite;
import com.business.unknow.services.entities.Client;
import com.business.unknow.services.entities.Devolucion;

@Service
public class DevolucionEvaluatorService extends AbstractDevolucionesEvaluatorService {

	@Autowired
	private DevolucionSuite devolucionSuite;

	public void generarDevolucionesPorPago(FacturaDto facturaDto, PagoDto pagoDto) throws InvoiceManagerException {
		Client client = clientRepository.findByRfc(facturaDto.getRfcRemitente())
				.orElseThrow(() -> new InvoiceManagerException("The type of document not supported",
						String.format("The type of document %s not valid", facturaDto.getTipoDocumento()),
						HttpStatus.BAD_REQUEST.value()));
		switch (TipoDocumentoEnum.findByDesc(facturaDto.getTipoDocumento())) {
		case FACRTURA:
			generaDevolucionPue(facturaDto, pagoDto,client);
			break;
		case COMPLEMENTO:
			generaDevolucionComplemento(facturaDto, pagoDto,client);
			break;
		default:
			throw new InvoiceManagerException("The type of document not supported",
					String.format("The type of document %s not valid", facturaDto.getTipoDocumento()),
					HttpStatus.BAD_REQUEST.value());
		}

	}

	public void generaDevolucionComplemento(FacturaDto facturaDto, PagoDto pagoDto,Client client) throws InvoiceManagerException {
		FacturaContext context = buildFacturaContextForComplementoDevolution(facturaDto, pagoDto);
		Double porcentajeComisiones = (context.getFacturaPadreDto().getTotal()
				- context.getFacturaPadreDto().getSubtotal()) / context.getFacturaPadreDto().getTotal();
		Facts facts = new Facts();
		facts.put("facturaContext", context);
		rulesEngine.fire(devolucionSuite.getSuite(), facts);
		validateFacturaContext(context);
		devolucionRepository
				.save(buildDevolucion(context.getFacturaDto().getFolio(), context.getFacturaDto().getId(),
						numberHelper.assignPrecision(pagoDto.getMonto() * porcentajeComisiones,
								Constants.DEFAULT_SCALE),
						client.getPorcentajePromotor(), client.getCorreoPromotor(),
						ContactoDevolucionEnum.PROMOTOR.getDescripcion()));
		devolucionRepository
				.save(buildDevolucion(context.getFacturaDto().getFolioPadre(), context.getCurrentPago().getId(),
						numberHelper.assignPrecision(pagoDto.getMonto() * porcentajeComisiones,
								Constants.DEFAULT_SCALE),
						client.getPorcentajeDespacho(), "invoice-manager@gmail.com",
						ContactoDevolucionEnum.DESPACHO.getDescripcion()));
		if (client.getPorcentajeCliente() > 0) {
			Devolucion devolucion = buildDevolucion(context.getFacturaDto().getFolio(),
					context.getCurrentPago().getId(),
					numberHelper.assignPrecision(pagoDto.getMonto() * porcentajeComisiones, Constants.DEFAULT_SCALE),
					client.getPorcentajeCliente(),
					client.getInformacionFiscal().getRfc(),
					ContactoDevolucionEnum.CLIENTE.getDescripcion());
			devolucion.setMonto(numberHelper.assignPrecision((context.getCurrentPago().getMonto()
					- (context.getCurrentPago().getMonto() * porcentajeComisiones) + devolucion.getMonto()),
					Constants.DEFAULT_SCALE));
			devolucionRepository.save(devolucion);
		}
		if (client.getPorcentajeContacto() > 0) {
			devolucionRepository
					.save(buildDevolucion(context.getFacturaDto().getFolio(), context.getCurrentPago().getId(),
							numberHelper.assignPrecision(pagoDto.getMonto() * porcentajeComisiones,
									Constants.DEFAULT_SCALE),
							client.getPorcentajeContacto(), client.getCorreoContacto(),
							ContactoDevolucionEnum.CONTACTO.getDescripcion()));
		}
		pagoRepository.findById(context.getCurrentPago().getId())
				.orElseThrow(() -> new InvoiceManagerException("No se pueden generar devoluciones a pagos inexistentes",
						String.format("El pago %s  no existe", context.getCurrentPago().toString()),
						HttpStatus.CONFLICT.value()));
		context.getFacturaDto().setStatusDevolucion(DevolucionStatusEnum.DEVUELTA.getValor());
		repository.save(mapper.getEntityFromFacturaDto(context.getFacturaDto()));
		context.getFacturaPadreDto().setStatusDevolucion(DevolucionStatusEnum.PARCIALMENTE_DEVUELTA.getValor());
		repository.save(mapper.getEntityFromFacturaDto(context.getFacturaPadreDto()));
	}

	public void generaDevolucionPue(FacturaDto facturaDto, PagoDto pagoDto,Client client) throws InvoiceManagerException {
		Double baseComisiones = facturaDto.getTotal() - facturaDto.getSubtotal();
		FacturaContext context = buildFacturaContextForPueDevolution(facturaDto, pagoDto);
		Facts facts = new Facts();
		facts.put("facturaContext", context);
		rulesEngine.fire(devolucionSuite.getSuite(), facts);
		validateFacturaContext(context);
		devolucionRepository.save(buildDevolucion(context.getFacturaDto().getFolio(), context.getFacturaDto().getId(),
				baseComisiones, client.getPorcentajePromotor(),
				client.getCorreoPromotor(), ContactoDevolucionEnum.PROMOTOR.getDescripcion()));
		devolucionRepository.save(buildDevolucion(context.getFacturaDto().getFolio(), context.getCurrentPago().getId(),
				baseComisiones, client.getPorcentajeDespacho(), "invoice-manager@gmail.com",
				ContactoDevolucionEnum.DESPACHO.getDescripcion()));
		if (client.getPorcentajeCliente() > 0) {
			Devolucion devolucion = buildDevolucion(context.getFacturaDto().getFolio(),
					context.getCurrentPago().getId(), baseComisiones, client.getPorcentajeCliente(),
					client.getInformacionFiscal().getRfc(),
					ContactoDevolucionEnum.CLIENTE.getDescripcion());
			devolucion.setMonto(context.getFacturaDto().getSubtotal() + devolucion.getMonto());
			devolucionRepository.save(devolucion);
		}
		if (client.getPorcentajeContacto() > 0) {
			devolucionRepository.save(buildDevolucion(context.getFacturaDto().getFolio(),
					context.getCurrentPago().getId(), baseComisiones, client.getPorcentajeContacto(),
					client.getCorreoContacto(), ContactoDevolucionEnum.CONTACTO.getDescripcion()));
		}
		pagoRepository.findById(context.getCurrentPago().getId())
				.orElseThrow(() -> new InvoiceManagerException("No se pueden generar devoluciones a pagos inexistentes",
						String.format("El pago %s  no existe", context.getCurrentPago().toString()),
						HttpStatus.CONFLICT.value()));
		// payment.setStatusPago("DEVOLUCION");NOT UPDATE PAY STATUS ACEPTADO SHOULD BE
		// FINAL STATUS
		// pagoRepository.save(payment);
		context.getFacturaDto().setStatusDevolucion(DevolucionStatusEnum.DEVUELTA.getValor());
		repository.save(mapper.getEntityFromFacturaDto(context.getFacturaDto()));
	}

}
