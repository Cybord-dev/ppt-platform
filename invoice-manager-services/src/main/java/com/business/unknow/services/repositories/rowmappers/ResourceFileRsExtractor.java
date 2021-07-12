/** */
package com.business.unknow.services.repositories.rowmappers;

import com.business.unknow.model.dto.files.ResourceFileDto;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.jdbc.support.lob.LobHandler;

/** @author ralfdemoledor */
public class ResourceFileRsExtractor implements ResultSetExtractor<Optional<ResourceFileDto>> {

  @Override
  public Optional<ResourceFileDto> extractData(ResultSet rs) throws SQLException {
    LobHandler lobHandler = new DefaultLobHandler();
    if (rs.next()) {
      ResourceFileDto result =
          ResourceFileDto.builder()
              .id(rs.getInt("FILE_ID"))
              .referencia(rs.getString("REFERENCIA"))
              .tipoArchivo(rs.getString("TIPO_ARCHIVO"))
              .tipoRecurso(rs.getString("TIPO_RECURSO"))
              .fechaCreacion(rs.getTimestamp("FECHA_CREACION"))
              .build();

      byte[] fileData = lobHandler.getBlobAsBytes(rs, "DATA");
      result.setData(new String(fileData, StandardCharsets.UTF_8));

      return Optional.of(result);
    } else {
      return Optional.empty();
    }
  }
}
