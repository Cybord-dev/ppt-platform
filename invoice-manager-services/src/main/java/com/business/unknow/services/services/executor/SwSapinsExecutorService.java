package com.business.unknow.services.services.executor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.business.unknow.Constants.FacturaConstants;
import com.business.unknow.client.swsapiens.model.SwSapiensVersionEnum;
import com.business.unknow.client.swsapiens.util.SwSapiensClientException;
import com.business.unknow.client.swsapiens.util.SwSapiensConfig;
import com.business.unknow.commons.util.DateHelper;
import com.business.unknow.commons.util.FacturaHelper;
import com.business.unknow.commons.util.FileHelper;
import com.business.unknow.enums.FacturaStatusEnum;
import com.business.unknow.enums.TipoArchivoEnum;
import com.business.unknow.model.cfdi.Cfdi;
import com.business.unknow.model.context.FacturaContext;
import com.business.unknow.model.dto.files.FacturaFileDto;
import com.business.unknow.model.error.InvoiceCommonException;
import com.business.unknow.model.error.InvoiceManagerException;
import com.business.unknow.services.client.SwSapiensClient;
import com.business.unknow.services.config.properties.GlocalConfigs;
import com.business.unknow.services.config.properties.SwProperties;

@Service
public class SwSapinsExecutorService extends AbstractPackExecutor {

	@Autowired
	private SwSapiensClient swSapiensClient;

	@Autowired
	private FacturaHelper facturaHelper;

	@Autowired
	private DateHelper dateHelper;

	@Autowired
	private FileHelper fileHelper;

	@Autowired
	private SwProperties swProperties;

	@Autowired
	private GlocalConfigs glocalConfigs;

	public FacturaContext stamp(FacturaContext context) throws InvoiceManagerException {
		SwSapiensClient swSapiensClient = new SwSapiensClient();
		try {
			SwSapiensConfig swSapiensConfig = swSapiensClient
					.getSwSapiensClient(swProperties.getHost(), "", swProperties.getUser(), swProperties.getPassword())
					.stamp(context.getXml(), SwSapiensVersionEnum.V4.getValue());

			context.getFacturaDto().setStatusFactura(FacturaStatusEnum.TIMBRADA.getValor());
			context.getFacturaDto().setUuid(swSapiensConfig.getData().getUuid());
			context.getFacturaDto().getCfdi().setSello(swSapiensConfig.getData().getSelloCFDI());
			String cfdi = swSapiensConfig.getData().getCfdi();
			Cfdi currentCfdi = facturaHelper.getFacturaFromString(cfdi);
			context.getFacturaDto()
					.setFechaTimbrado(dateHelper.getDateFromString(
							currentCfdi.getComplemento().getTimbreFiscalDigital().getFechaTimbrado(),
							FacturaConstants.FACTURA_DATE_FORMAT));
			context.getFacturaDto().setCadenaOriginalTimbrado(getCadenaOriginalTimbrado(currentCfdi));
			List<FacturaFileDto> files = new ArrayList<>();
			FacturaFileDto qr = new FacturaFileDto();
			qr.setFolio(context.getFacturaDto().getFolio());
			qr.setTipoArchivo(TipoArchivoEnum.QR.name());
			qr.setData(swSapiensConfig.getData().getQrCode());
			FacturaFileDto xml = new FacturaFileDto();
			xml.setFolio(context.getFacturaDto().getFolio());
			xml.setTipoArchivo(TipoArchivoEnum.XML.name());
			xml.setData(fileHelper.stringEncodeBase64(swSapiensConfig.getData().getCfdi()));
			files.add(qr);
			files.add(xml);
			context.setFacturaFilesDto(files);
		} catch (SwSapiensClientException e) {
			e.printStackTrace();
			throw new InvoiceManagerException(
					String.format("Error_%s Detail:%s", e.getMessage(), e.getErrorMessage().getMessageDetail()),
					e.getErrorMessage().toString(), HttpStatus.SC_CONFLICT);
		} catch (InvoiceCommonException e) {
			e.printStackTrace();
			throw new InvoiceManagerException(
					String.format("Error_%s Detail:%s", e.getMessage(), e.getErrorMessage().getDeveloperMessage()),
					e.getErrorMessage().toString(), HttpStatus.SC_CONFLICT);
		}
		return context;
	}

	public SwSapiensConfig validateRfc(String rfc) throws SwSapiensClientException {
		return swSapiensClient
				.getSwSapiensClient(swProperties.getHost(), "", swProperties.getUser(), swProperties.getPassword())
				.validateRfc(rfc);
	}

	public SwSapiensConfig validateLco(String noCertificado) throws SwSapiensClientException {
		return swSapiensClient
				.getSwSapiensClient(swProperties.getHost(), "", swProperties.getUser(), swProperties.getPassword())
				.validateLco(noCertificado);
	}

	public FacturaContext cancelarFactura(FacturaContext context) throws InvoiceManagerException {
		try {
			if (glocalConfigs.getEnvironment().equals("prod")) {
				swSapiensClient.getSwSapiensClient(swProperties.getHost(), "", swProperties.getUser(),
						swProperties.getPassword()).cancel(context.getFacturaDto().getUuid(),
								context.getEmpresaDto().getPwSat(),
								context.getEmpresaDto().getInformacionFiscal().getRfc(),
								fileHelper.stringEncodeBase64(context.getEmpresaDto().getCertificado()),
								fileHelper.stringEncodeBase64(context.getEmpresaDto().getLlavePrivada()));
			}
			context.getFacturaDto().setStatusFactura(FacturaStatusEnum.CANCELADA.getValor());
			context.getFacturaDto().setFechaCancelacion(new Date());
			return context;
		} catch (SwSapiensClientException e) {
			e.printStackTrace();
			throw new InvoiceManagerException(
					String.format("Error durante el Cancelado de :%s Error:%s Detail:%s",
							context.getFacturaDto().getUuid(), e.getMessage(), e.getErrorMessage().getMessageDetail()),
					e.getMessage(), HttpStatus.SC_CONFLICT);
		}
	}
}
