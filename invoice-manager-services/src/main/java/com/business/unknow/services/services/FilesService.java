package com.business.unknow.services.services;

import com.business.unknow.enums.S3BucketsEnum;
import com.business.unknow.enums.TipoArchivoEnum;
import com.business.unknow.model.dto.files.FacturaFileDto;
import com.business.unknow.model.dto.files.ResourceFileDto;
import com.business.unknow.model.error.InvoiceManagerException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class FilesService {

  @Autowired private S3FileService s3FileService;

  public FacturaFileDto getFacturaFileByFolioAndType(String folio, String type)
      throws InvoiceManagerException {
    try {
      String data =
          s3FileService.getS3File(
              S3BucketsEnum.CFDIS, TipoArchivoEnum.valueOf(type).getFormat(), folio);
      var fileDto = new FacturaFileDto();
      fileDto.setFolio(folio);
      fileDto.setData(data);
      fileDto.setTipoArchivo(type);
      return fileDto;
    } catch (Exception e) {
      throw new InvoiceManagerException(e.getMessage(), HttpStatus.CONFLICT.value());
    }
  }

  public void upsertS3File(
      S3BucketsEnum bucket, String fileFormat, String name, ByteArrayOutputStream file)
      throws InvoiceManagerException {
    s3FileService.upsertS3File(bucket, fileFormat, name, file);
  }

  public void deleteS3File(String folio, String type) throws InvoiceManagerException {
    s3FileService.deleteS3File(
        S3BucketsEnum.CFDIS, TipoArchivoEnum.valueOf(type).getFormat(), folio);
  }

  public ResourceFileDto getResourceFileByResourceReferenceAndType(
      String resource, String referencia, String type) throws InvoiceManagerException {

    try {
      String data =
          s3FileService.getS3File(
              S3BucketsEnum.findByNombre(resource),
              TipoArchivoEnum.valueOf(type).getFormat(),
              referencia);
      var fileDto = new ResourceFileDto();
      fileDto.setData(data);
      fileDto.setTipoArchivo(type);
      fileDto.setReferencia(referencia);
      return fileDto;
    } catch (Exception e) {
      throw new InvoiceManagerException(e.getMessage(), HttpStatus.CONFLICT.value());
    }
  }

  public void upsertResourceFile(ResourceFileDto resourceFile) throws InvoiceManagerException {
    try {
      var outputStream = new ByteArrayOutputStream();
      outputStream.write(Base64.getDecoder().decode(resourceFile.getData()));
      s3FileService.upsertS3File(
          S3BucketsEnum.findByNombre(resourceFile.getTipoRecurso()),
              TipoArchivoEnum.valueOf(resourceFile.getTipoArchivo()).getFormat(),
          resourceFile.getReferencia(),
          outputStream);
      outputStream.close();
    } catch (IOException e) {
      throw new InvoiceManagerException(
          String.format("Error creating the file %s", resourceFile.getReferencia()),
          HttpStatus.CONFLICT.value());
    }
  }

  public void deleteResourceFileByResourceReferenceAndType(
      String resource, String referencia, String type) throws InvoiceManagerException {
    s3FileService.deleteS3File(S3BucketsEnum.findByNombre(resource),  TipoArchivoEnum.valueOf(type).getFormat(), referencia);
  }
}
