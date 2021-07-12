package com.business.unknow.services.repositories.rowmappers;

import com.business.unknow.model.dto.services.CuentaBancariaDto;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class CuentaBancariaDtoRowMapper implements RowMapper<CuentaBancariaDto> {

  @Override
  public CuentaBancariaDto mapRow(ResultSet rs, int rowNum) throws SQLException {
    return CuentaBancariaDto.builder()
        .total(rs.getInt("TOTAL"))
        .id(rs.getInt("ID_CUENTA_BANCARIA"))
        .banco(rs.getString("BANCO"))
        .linea(rs.getString("LINEA"))
        .giro(rs.getString("GIRO"))
        .razonSocial(rs.getString("RAZON_SOCIAL"))
        .empresa(rs.getString("RFC"))
        .cuenta(rs.getString("NO_CUENTA"))
        .clabe(rs.getString("CLABE"))
        .fechaCreacion(rs.getTimestamp("FECHA_CREACION"))
        .fechaActualizacion(rs.getTimestamp("FECHA_ACTUALIZACION"))
        .build();
  }
}
