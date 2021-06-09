package com.business.unknow.services.services.executor;

import com.business.unknow.Constants.FacturaConstants;
import com.business.unknow.client.ntlink.model.NtlinkCancelRequestModel;
import com.business.unknow.client.ntlink.model.NtlinkClientException;
import com.business.unknow.client.ntlink.model.NtlinkRequestModel;
import com.business.unknow.client.ntlink.model.NtlinkResponseModel;
import com.business.unknow.enums.FacturaStatusEnum;
import com.business.unknow.enums.TipoArchivoEnum;
import com.business.unknow.model.cfdi.Cfdi;
import com.business.unknow.model.context.FacturaContext;
import com.business.unknow.model.dto.files.FacturaFileDto;
import com.business.unknow.model.error.InvoiceCommonException;
import com.business.unknow.model.error.InvoiceManagerException;
import com.business.unknow.services.client.NtlinkClient;
import com.business.unknow.services.config.properties.GlocalConfigs;
import com.business.unknow.services.config.properties.NtlinkProperties;
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
public class NtinkExecutorService extends AbstractPackExecutor {

  @Autowired private NtlinkClient client;

  @Autowired private FacturaHelper facturaHelper;

  @Autowired private FileHelper fileHelper;

  @Autowired private DateHelper dateHelper;

  @Autowired private NtlinkProperties ntlinkProperties;

  @Autowired private GlocalConfigs glocalConfigs;

  private static final String EXP = "&";

  public FacturaContext cancelarFactura(FacturaContext context) throws InvoiceManagerException {
    try {
      String expresion =
          String.format(
              "https://verificacfdi.facturaelectronica.sat.gob.mx/default.aspx?%sid=%s%sre=%s%srr=%s%stt=%s%sfe=%s",
              EXP,
              context.getFacturaDto().getUuid(),
              EXP,
              context.getFacturaDto().getRfcEmisor(),
              EXP,
              context.getFacturaDto().getRfcRemitente(),
              EXP,
              context.getFacturaDto().getCfdi().getTotal().toString(),
              EXP,
              context.getFacturaDto().getSelloCfd());
      NtlinkCancelRequestModel requestModel =
          new NtlinkCancelRequestModel(
              ntlinkProperties.getUser(),
              ntlinkProperties.getPassword(),
              context.getFacturaDto().getUuid(),
              context.getFacturaDto().getRfcEmisor(),
              context.getFacturaDto().getRfcRemitente(),
              expresion);
      if (glocalConfigs.getEnvironment().equals("prod")) {
        client
            .getNtlinkClient(ntlinkProperties.getHost(), ntlinkProperties.getContext())
            .cancelar(requestModel);
      }
      context.getFacturaDto().setStatusFactura(FacturaStatusEnum.CANCELADA.getValor());
      context.getFacturaDto().setFechaCancelacion(new Date());
      return context;
    } catch (NtlinkClientException e) {
      e.printStackTrace();
      throw new InvoiceManagerException(
          String.format(
              "Error durante el Cancelado de :%s error:%s detail:%s",
              context.getFacturaDto().getUuid(),
              e.getMessage(),
              e.getErrorMessage().getMessageDetail()),
          e.getMessage(),
          e.getHttpStatus());
    }
  }

  public FacturaContext stamp(FacturaContext context) throws InvoiceManagerException {
    try {
      NtlinkRequestModel requestModel =
          new NtlinkRequestModel(
              ntlinkProperties.getUser(), ntlinkProperties.getPassword(), context.getXml());
      NtlinkResponseModel response =
          client
              .getNtlinkClient(ntlinkProperties.getHost(), ntlinkProperties.getContext())
              .stamp(requestModel);
      String cfdi = response.getCfdi();
      context.getFacturaDto().setStatusFactura(FacturaStatusEnum.TIMBRADA.getValor());
      Cfdi currentCfdi = facturaHelper.getFacturaFromString(cfdi);
      context
          .getFacturaDto()
          .setUuid(currentCfdi.getComplemento().getTimbreFiscalDigital().getUuid());
      context.getFacturaDto().getCfdi().setSello(currentCfdi.getSello());
      context
          .getFacturaDto()
          .setFechaTimbrado(
              dateHelper.getDateFromString(
                  currentCfdi.getComplemento().getTimbreFiscalDigital().getFechaTimbrado(),
                  FacturaConstants.FACTURA_DATE_FORMAT));
      context.getFacturaDto().setCadenaOriginalTimbrado(getCadenaOriginalTimbrado(currentCfdi));
      String selloSat = currentCfdi.getComplemento().getTimbreFiscalDigital().getSelloSAT();
      context
          .getFacturaDto()
          .setSelloCfd(selloSat.substring(selloSat.length() - 8, selloSat.length()));
      List<FacturaFileDto> files = new ArrayList<>();
      if (response.getCfdi() != null) {
        FacturaFileDto xml = new FacturaFileDto();
        xml.setFolio(context.getFacturaDto().getFolio());
        xml.setTipoArchivo(TipoArchivoEnum.XML.name());
        xml.setFileFormat(TipoArchivoEnum.XML);
        xml.setData(fileHelper.stringEncodeBase64(response.getCfdi()));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(
            Base64.getDecoder().decode(xml.getData().getBytes(StandardCharsets.UTF_8)));
        xml.setOutputStream(outputStream);
        files.add(xml);
      }
      context.setFacturaFilesDto(files);
      FacturaFileDto qr = new FacturaFileDto();
      qr.setFolio(context.getFacturaDto().getFolio());
      qr.setTipoArchivo(TipoArchivoEnum.QR.name());
      qr.setFileFormat(TipoArchivoEnum.QR);
      qr.setData(response.getQrCodeBase64());
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      outputStream.write(Base64.getDecoder().decode(qr.getData().getBytes(StandardCharsets.UTF_8)));
      qr.setOutputStream(outputStream);
      files.add(qr);
    } catch (IOException i) {
      throw new InvoiceManagerException(
          String.format("Error exporting s3 file %s", i.getMessage()),
          i.getMessage(),
          HttpStatus.SC_CONFLICT);
    } catch (NtlinkClientException e) {
      e.printStackTrace();
      throw new InvoiceManagerException(
          String.format(
              "Error Stamping in facturacion moderna:  Error:%s detail:%s",
              e.getMessage(), e.getErrorMessage().getMessageDetail()),
          e.getMessage(),
          HttpStatus.SC_CONFLICT);
    } catch (InvoiceCommonException e) {
      throw new InvoiceManagerException(
          String.format(
              "Error Stamping in facturacion moderna: Error:%s detail:%s",
              e.getMessage(), e.getErrorMessage().getDeveloperMessage()),
          e.getMessage(),
          HttpStatus.SC_CONFLICT);
    }
    return context;
  }
}
