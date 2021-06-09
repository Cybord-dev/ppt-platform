package com.business.unknow.services.services.executor;

import com.business.unknow.Constants.FacturaConstants;
import com.business.unknow.client.facturacionmoderna.model.FacturaModernaRequestModel;
import com.business.unknow.client.facturacionmoderna.model.FacturaModernaResponseModel;
import com.business.unknow.client.facturacionmoderna.util.FacturaModernaClientException;
import com.business.unknow.enums.FacturaStatusEnum;
import com.business.unknow.enums.TipoArchivoEnum;
import com.business.unknow.enums.TipoDocumentoEnum;
import com.business.unknow.model.cfdi.Cfdi;
import com.business.unknow.model.context.FacturaContext;
import com.business.unknow.model.dto.files.FacturaFileDto;
import com.business.unknow.model.error.InvoiceCommonException;
import com.business.unknow.model.error.InvoiceManagerException;
import com.business.unknow.services.client.FacturacionModernaClient;
import com.business.unknow.services.config.properties.FacturacionModernaProperties;
import com.business.unknow.services.config.properties.GlocalConfigs;
import com.business.unknow.services.util.helpers.DateHelper;
import com.business.unknow.services.util.helpers.FacturaHelper;
import com.business.unknow.services.util.helpers.FileHelper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FacturacionModernaExecutor extends AbstractPackExecutor {

  @Autowired private FacturacionModernaClient client;

  @Autowired private FileHelper fileHelper;

  @Autowired private FacturaHelper facturaHelper;

  @Autowired private DateHelper dateHelper;

  @Autowired private FacturacionModernaProperties fmProperties;

  @Autowired private GlocalConfigs glocalConfigs;

  public FacturaContext stamp(FacturaContext context) throws InvoiceManagerException {
    try {
      if (context.getTipoDocumento().equals(TipoDocumentoEnum.FACTURA.getDescripcion())) {
        context.setXml(
            context.getXml().replace("xmlns:pago10=\"http://www.sat.gob.mx/Pagos\"", ""));
      }
      FacturaModernaRequestModel requestModel =
          new FacturaModernaRequestModel(
              fmProperties.getUser(),
              fmProperties.getPassword(),
              context.getFacturaDto().getRfcEmisor(),
              fileHelper.stringEncodeBase64(context.getXml()),
              true,
              true,
              true);
      FacturaModernaResponseModel response =
          client.getFacturacionModernaClient(fmProperties.getHost(), "").stamp(requestModel);
      String cfdi = fileHelper.stringDecodeBase64(response.getXml());
      context.getFacturaDto().setStatusFactura(FacturaStatusEnum.TIMBRADA.getValor());
      Cfdi currentCfdi = facturaHelper.getFacturaFromString(cfdi);
      context
          .getFacturaDto()
          .setUuid(currentCfdi.getComplemento().getTimbreFiscalDigital().getUuid());
      context
          .getFacturaDto()
          .setFechaTimbrado(
              dateHelper.getDateFromString(
                  currentCfdi.getComplemento().getTimbreFiscalDigital().getFechaTimbrado(),
                  FacturaConstants.FACTURA_DATE_FORMAT));
      context.getFacturaDto().setCadenaOriginalTimbrado(getCadenaOriginalTimbrado(currentCfdi));
      context.getFacturaDto().getCfdi().setSello(currentCfdi.getSello());
      List<FacturaFileDto> files = new ArrayList<>();
      if (response.getPng() != null) {
        FacturaFileDto qr = new FacturaFileDto();
        qr.setFolio(context.getFacturaDto().getFolio());
        qr.setTipoArchivo(TipoArchivoEnum.QR.name());
        qr.setFileFormat(TipoArchivoEnum.QR);
        qr.setData(response.getPng());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(
            Base64.getDecoder().decode(response.getPng().getBytes(StandardCharsets.UTF_8)));
        qr.setOutputStream(outputStream);
        files.add(qr);
      }
      if (response.getXml() != null) {
        FacturaFileDto xml = new FacturaFileDto();
        xml.setFolio(context.getFacturaDto().getFolio());
        xml.setTipoArchivo(TipoArchivoEnum.XML.name());
        xml.setFileFormat(TipoArchivoEnum.XML);
        xml.setData(response.getXml());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(
            Base64.getDecoder().decode(response.getXml().getBytes(StandardCharsets.UTF_8)));
        xml.setOutputStream(outputStream);
        files.add(xml);
      }
      context.setFacturaFilesDto(files);

    } catch (IOException i) {
      throw new InvoiceManagerException(
          String.format("Error exporting s3 file %s", i.getMessage()),
          i.getMessage(),
          HttpStatus.SC_CONFLICT);
    } catch (FacturaModernaClientException e) {
      e.printStackTrace();
      throw new InvoiceManagerException(
          String.format(
              "Error Stamping in facturacion moderna: %s Detail:%s",
              e.getMessage(), e.getErrorMessage().getMessageDetail()),
          e.getMessage(),
          HttpStatus.SC_CONFLICT);
    } catch (InvoiceCommonException e) {
      throw new InvoiceManagerException(
          String.format(
              "Error Stamping in facturacion moderna: %s Detail:%s",
              e.getMessage(), e.getErrorMessage().getDeveloperMessage()),
          e.getMessage(),
          HttpStatus.SC_CONFLICT);
    }
    return context;
  }

  public FacturaContext cancelarFactura(FacturaContext context) throws InvoiceManagerException {
    try {
      FacturaModernaRequestModel requestModel =
          new FacturaModernaRequestModel(
              fmProperties.getUser(),
              fmProperties.getPassword(),
              context.getFacturaDto().getRfcEmisor(),
              context.getFacturaDto().getUuid());
      if (glocalConfigs.getEnvironment().equals("prod")) {
        client.getFacturacionModernaClient(fmProperties.getHost(), "").cancelar(requestModel);
      }
      context.getFacturaDto().setStatusFactura(FacturaStatusEnum.CANCELADA.getValor());
      context.getFacturaDto().setFechaCancelacion(new Date());
      return context;
    } catch (FacturaModernaClientException e) {
      e.printStackTrace();
      throw new InvoiceManagerException(
          String.format(
              "Error durante el Cancelado de :%s Error:%s Detail:%s",
              context.getFacturaDto().getUuid(),
              e.getMessage(),
              e.getErrorMessage().getMessageDetail()),
          e.getMessage(),
          e.getHttpStatus());
    }
  }
}
