/** */
package com.business.unknow.services.services;

import com.business.unknow.enums.S3BucketsEnum;
import com.business.unknow.enums.TipoArchivoEnum;
import com.business.unknow.model.dto.files.FacturaFileDto;
import com.business.unknow.model.dto.files.ResourceFileDto;
import com.business.unknow.model.error.InvoiceManagerException;
import com.business.unknow.services.repositories.files.FilesDao;
import java.io.ByteArrayOutputStream;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class FilesService {

  @Autowired private FilesDao filesDao;

  @Autowired private S3FileService s3FileService;

  public FacturaFileDto getFacturaFileByFolioAndType(String folio, String type)
      throws InvoiceManagerException {
    try {
      String data =
          s3FileService.getS3File(
              S3BucketsEnum.FACTURAS, TipoArchivoEnum.valueOf(type).getFormat(), folio);
      FacturaFileDto fileDto = new FacturaFileDto();
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
        S3BucketsEnum.FACTURAS, TipoArchivoEnum.valueOf(type).getFormat(), folio);
  }

  public ResourceFileDto getResourceFileByResourceReferenceAndType(
      String resource, String referencia, String type) throws InvoiceManagerException {
    return filesDao
        .findResourceFileByResourceTypeAndReference(resource, referencia, type)
        .orElseThrow(
            () ->
                new InvoiceManagerException(
                    "El recurso solicitado no existe.", HttpStatus.NOT_FOUND.value()));
  }

  public Optional<ResourceFileDto> findResourceFileByResourceReferenceAndType(
      String resource, String referencia, String type) {
    return filesDao.findResourceFileByResourceTypeAndReference(resource, type, referencia);
  }

  public void upsertResourceFile(ResourceFileDto resourceFile) {
    Optional<ResourceFileDto> resource =
        filesDao.findResourceFileByResourceTypeAndReference(
            resourceFile.getTipoRecurso(),
            resourceFile.getReferencia(),
            resourceFile.getTipoArchivo());
    if (resource.isPresent()) {
      resourceFile.setId(resource.get().getId());
      filesDao.updateResourceFile(resource.get().getId(), resourceFile);
    } else {
      filesDao.insertResourceFile(resourceFile);
    }
  }

  public void deleteResourceFile(Integer id) {
    filesDao.deletResourceFileById(id);
  }

  public void deleteResourceFileByResourceReferenceAndType(
      String resource, String referencia, String type) {
    filesDao.deleteResourceFileByResourceTypeAndReference(resource, type, referencia);
  }
}
