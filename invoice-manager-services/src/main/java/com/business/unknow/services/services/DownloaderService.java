package com.business.unknow.services.services;

import com.business.unknow.model.dto.files.ResourceFileDto;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.dhatim.fastexcel.Workbook;
import org.dhatim.fastexcel.Worksheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class DownloaderService {

  private static final Logger log = LoggerFactory.getLogger(DownloaderService.class);

  public ResourceFileDto generateBase64Report(
      String reportName, List<Map<String, Object>> data, List<String> headers) throws IOException {
    if (!data.isEmpty()) {

      log.info("Generating Excel report for {} data", data.size());

      try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {

        Workbook wb = new Workbook(os, "InvManagerReport", "1.0");
        Worksheet ws = wb.newWorksheet(reportName);

        int column = 0;
        int row = 0;
        // Building headers
        for (String header : headers) {
          ws.value(0, column, header);
          column++;
        }
        row++;
        // Building table values

        for (Map<String, Object> map : data) {
          column = 0;
          for (String header : headers) {
            String value = (map.get(header) != null ? map.get(header).toString() : " ");
            ws.value(row, column, value);
            column++;
          }
          row++;
        }

        wb.finish();

        String reportData = Base64.getEncoder().encodeToString(os.toByteArray());

        return ResourceFileDto.builder()
            .data(reportData)
            .fechaCreacion(new Date())
            .referencia(reportName)
            .tipoArchivo("XLS")
            .tipoRecurso("AUTOGENERADO")
            .build();
      }
    } else {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND, " No es posible generar el roporte, no hay datos en el sistema.");
    }
  }
}
