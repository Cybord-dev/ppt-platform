package com.business.unknow.services.services.evaluations;

import java.math.BigDecimal;
import java.util.Optional;

import org.apache.http.HttpStatus;
import org.jeasy.rules.api.RulesEngine;
import org.springframework.beans.factory.annotation.Autowired;

import com.business.unknow.Constants;
import com.business.unknow.Constants.FacturaComplemento;
import com.business.unknow.commons.builder.DevolucionDtoBuilder;
import com.business.unknow.commons.builder.FacturaContextBuilder;
import com.business.unknow.commons.util.FacturaCalculator;
import com.business.unknow.commons.util.FileHelper;
import com.business.unknow.commons.util.NumberHelper;
import com.business.unknow.commons.validator.FacturaValidator;
import com.business.unknow.enums.ResourceFileEnum;
import com.business.unknow.enums.TipoRecursoEnum;
import com.business.unknow.model.context.FacturaContext;
import com.business.unknow.model.dto.FacturaDto;
import com.business.unknow.model.dto.files.FacturaFileDto;
import com.business.unknow.model.dto.services.EmpresaDto;
import com.business.unknow.model.error.InvoiceManagerException;
import com.business.unknow.services.entities.Contribuyente;
import com.business.unknow.services.entities.Devolucion;
import com.business.unknow.services.entities.Empresa;
import com.business.unknow.services.entities.Pago;
import com.business.unknow.services.entities.cfdi.Cfdi;
import com.business.unknow.services.entities.factura.Factura;
import com.business.unknow.services.entities.files.ResourceFile;
import com.business.unknow.services.mapper.ClientMapper;
import com.business.unknow.services.mapper.ContribuyenteMapper;
import com.business.unknow.services.mapper.DevolucionMapper;
import com.business.unknow.services.mapper.EmpresaMapper;
import com.business.unknow.services.mapper.FilesMapper;
import com.business.unknow.services.mapper.PagoMapper;
import com.business.unknow.services.mapper.factura.CfdiMapper;
import com.business.unknow.services.repositories.ClientRepository;
import com.business.unknow.services.repositories.ContribuyenteRepository;
import com.business.unknow.services.repositories.EmpresaRepository;
import com.business.unknow.services.repositories.PagoRepository;
import com.business.unknow.services.repositories.facturas.CfdiRepository;
import com.business.unknow.services.repositories.facturas.ConceptoRepository;
import com.business.unknow.services.repositories.facturas.ImpuestoRepository;
import com.business.unknow.services.services.AbstractService;
import com.business.unknow.services.util.FacturaDefaultValues;;

//TODO Evaluations should be only evaluate conditions and not save on DB, follow SRP principle
public class AbstractEvaluatorService extends AbstractService {

	@Autowired
	protected CfdiRepository cfdiRepository;

	@Autowired
	protected ConceptoRepository conceptoRepository;

	@Autowired
	protected ImpuestoRepository impuestoRepository;

	@Autowired
	protected PagoRepository pagoRepository;

	@Autowired
	protected EmpresaRepository empresaRepository;

	@Autowired
	protected ClientRepository clientRepository;

	@Autowired
	protected ContribuyenteRepository contribuyenteRepository;

	@Autowired
	protected FilesMapper filesMapper;

	@Autowired
	protected CfdiMapper cfdiMapper;

	@Autowired
	protected PagoMapper pagoMapper;

	@Autowired
	protected EmpresaMapper empresaMapper;

	@Autowired
	protected ClientMapper clientMapper;

	@Autowired
	private DevolucionMapper devolucionMapper;

	@Autowired
	private ContribuyenteMapper contribuyenteMapper;

	@Autowired
	protected RulesEngine rulesEngine;

	protected FacturaValidator validator = new FacturaValidator();

	protected FacturaCalculator facturaCalculator = new FacturaCalculator();

	protected FacturaDefaultValues facturaDefaultValues = new FacturaDefaultValues();

	protected NumberHelper numberHelper = new NumberHelper();

	protected FileHelper fileHelper = new FileHelper();

	protected void validateFacturaContext(FacturaContext facturaContexrt) throws InvoiceManagerException {
		if (!facturaContexrt.isValid()) {
			throw new InvoiceManagerException(facturaContexrt.getRuleErrorDesc(), facturaContexrt.getSuiteError(),
					HttpStatus.SC_BAD_REQUEST);
		}
	}

	protected void updateFacturaValues(FacturaContext context) {
		repository.save(mapper.getEntityFromFacturaDto(context.getFacturaDto()));
	}

	protected void updateFacturaAndCfdiValues(FacturaContext context) {
		repository.save(mapper.getEntityFromFacturaDto(context.getFacturaDto()));
		cfdiRepository.save(cfdiMapper.getEntityFromCfdiDto(context.getFacturaDto().getCfdi()));
		for (FacturaFileDto facturaFileDto : context.getFacturaFilesDto())
			if (facturaFileDto != null) {
				facturaFileRepository.save(filesMapper.getFacturaFileFromDto(facturaFileDto));
			}
	}

	protected void updateCanceladoValues(FacturaContext context) {
		repository.save(mapper.getEntityFromFacturaDto(context.getFacturaDto()));
	}

	protected void getEmpresaFiles(EmpresaDto empresaDto, FacturaDto facturaDto) throws InvoiceManagerException {
		ResourceFile certFile = resourceFileRepository
				.findByTipoRecursoAndReferenciaAndTipoArchivo(TipoRecursoEnum.EMPRESA.name(), facturaDto.getRfcEmisor(),
						ResourceFileEnum.CERT.name())
				.orElseThrow(() -> new InvoiceManagerException("Empresa certificate not found",
						String.format("La empresa con el rfc no tiene certificado", facturaDto.getRfcEmisor()),
						HttpStatus.SC_NOT_FOUND));
		ResourceFile keyFile = resourceFileRepository
				.findByTipoRecursoAndReferenciaAndTipoArchivo(TipoRecursoEnum.EMPRESA.name(), facturaDto.getRfcEmisor(),
						ResourceFileEnum.KEY.name())
				.orElseThrow(() -> new InvoiceManagerException("Empresa certificate not found",
						String.format("La empresa con el rfc no tiene certificado", facturaDto.getRfcEmisor()),
						HttpStatus.SC_NOT_FOUND));
		empresaDto.setCertificado(fileHelper.getStringFileSource((certFile.getData())));
		empresaDto.setLlavePrivada(fileHelper.getStringFileSource((keyFile.getData())));
	}

	protected FacturaContext buildFacturaContextTimbrado(FacturaDto facturaDto, String folio)
			throws InvoiceManagerException {
		Optional<Factura> folioPadreEntity = repository.findByFolio(facturaDto.getFolioPadre());
		Factura folioEnity = repository.findByFolio(folio)
				.orElseThrow(() -> new InvoiceManagerException("Folio not found",
						String.format("Folio with the name %s not found", facturaDto.getFolio()),
						HttpStatus.SC_NOT_FOUND));
		validatePackFacturacion(facturaDto, folioPadreEntity);
		Optional<Cfdi> cfdi = cfdiRepository.findByFolio(folio);
		EmpresaDto empresaDto = empresaMapper
				.getEmpresaDtoFromEntity(empresaRepository.findByRfc(facturaDto.getRfcEmisor())
						.orElseThrow(() -> new InvoiceManagerException("Empresa not found",
								String.format("La empresa con el rfc no existe", facturaDto.getRfcEmisor()),
								HttpStatus.SC_NOT_FOUND)));
		Optional<Pago> pagoCredito = pagoRepository.findByFolioAndFormaPagoAndComentarioPago(facturaDto.getFolioPadre(),
				FacturaComplemento.FORMA_PAGO, FacturaComplemento.PAGO_COMENTARIO);
		FacturaDto currentFacturaDto = mapper.getFacturaDtoFromEntity(folioEnity);
		getEmpresaFiles(empresaDto, currentFacturaDto);
		currentFacturaDto.setPackFacturacion(facturaDto.getPackFacturacion());
		return new FacturaContextBuilder().setFacturaDto(currentFacturaDto)
				.setPagos(mapper.getPagosDtoFromEntity(pagoRepository.findByFolio(folio)))
				.setCfdi(cfdi.isPresent() ? cfdiMapper.getCfdiDtoFromEntity(cfdi.get()) : null)
				.setEmpresaDto(empresaDto)
				.setPagoCredito(pagoCredito.isPresent() ? mapper.getPagoDtoFromEntity(pagoCredito.get()) : null)
				.setFacturaPadreDto(
						folioPadreEntity.isPresent() ? mapper.getFacturaDtoFromEntity(folioPadreEntity.get()) : null)
				.setTipoFactura(facturaDto.getCfdi().getMetodoPago()).setTipoDocumento(facturaDto.getTipoDocumento())
				.setCtdadComplementos(repository
						.findByFolioPadre(
								facturaDto.getFolioPadre() != null ? facturaDto.getFolioPadre() : facturaDto.getFolio())
						.size())
				.build();
	}

	protected FacturaContext buildFacturaContextCancelado(FacturaDto facturaDto, String folio)
			throws InvoiceManagerException {
		Optional<Factura> folioPadreEntity = repository.findByFolio(facturaDto.getFolioPadre());
		Factura folioEnity = repository.findByFolio(folio)
				.orElseThrow(() -> new InvoiceManagerException("Folio not found",
						String.format("Folio with the name %s not found", facturaDto.getFolio()),
						HttpStatus.SC_NOT_FOUND));
		EmpresaDto empresaDto = empresaMapper
				.getEmpresaDtoFromEntity(empresaRepository.findByRfc(facturaDto.getRfcEmisor())
						.orElseThrow(() -> new InvoiceManagerException("La empresa no existe",
								String.format("La empresa con el rfc no existe %s", facturaDto.getRfcEmisor()),
								HttpStatus.SC_NOT_FOUND)));
		return new FacturaContextBuilder().setFacturaDto(mapper.getFacturaDtoFromEntity(folioEnity))
				.setPagos(mapper.getPagosDtoFromEntity(pagoRepository.findByFolio(folio))).setEmpresaDto(empresaDto)
				.setFacturaPadreDto(
						folioPadreEntity.isPresent() ? mapper.getFacturaDtoFromEntity(folioPadreEntity.get()) : null)
				.setTipoFactura(facturaDto.getCfdi().getMetodoPago()).setTipoDocumento(facturaDto.getTipoDocumento())
				.build();
	}

	protected FacturaContext buildFacturaContextCreateFactura(FacturaDto facturaDto) throws InvoiceManagerException {
		Empresa empresa = empresaRepository.findByRfc(facturaDto.getRfcEmisor())
				.orElseThrow(() -> new InvoiceManagerException("Emisor de factura no existen en el sistema",
						String.format("No se encuentra el RFC %s en el sistema", facturaDto.getRfcEmisor()),
						Constants.BAD_REQUEST));
		Contribuyente contribuyente = contribuyenteRepository.findByRfc(facturaDto.getRfcRemitente())
				.orElseThrow(() -> new InvoiceManagerException("Error al crear factura", "El receptor no exite",
						Constants.BAD_REQUEST));
		return new FacturaContextBuilder().setFacturaDto(facturaDto)
				.setEmpresaDto(empresaMapper.getEmpresaDtoFromEntity(empresa))
				.setContribuyenteDto(contribuyenteMapper.getContribuyenteToFromEntity(contribuyente)).build();
	}

	private void validatePackFacturacion(FacturaDto currentFacturaDto, Optional<Factura> facturaPadre)
			throws InvoiceManagerException {
		if (facturaPadre.isPresent()
				&& !facturaPadre.get().getPackFacturacion().equals(currentFacturaDto.getPackFacturacion())) {
			throw new InvoiceManagerException("El pack del complemento debe ser el mismo",
					String.format("El pack de facturacion del complemento %s no es el correcto",
							currentFacturaDto.getPackFacturacion()),
					HttpStatus.SC_BAD_REQUEST);
		}
	}

	protected Devolucion buildDevolucion(String foliofFact, Integer idPago, BigDecimal montoBase, Integer porcentaje,
			String receptor, String tipoReceptor) {
		return devolucionMapper.getEntityFromDevolucionDto(new DevolucionDtoBuilder()
				.setMonto((montoBase.multiply(new BigDecimal(porcentaje)).divide(new BigDecimal(16))))
				.setFolio(foliofFact).setIdPagoOrigen(idPago).setReceptor(receptor).setTipoReceptor(tipoReceptor)
				.setStatusDevolucion("POR SOLICITAR").build());
	}

}
