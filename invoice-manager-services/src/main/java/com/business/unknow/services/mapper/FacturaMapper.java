package com.business.unknow.services.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import com.business.unknow.model.PagoDto;
import com.business.unknow.model.catalogs.StatusFacturaDto;
import com.business.unknow.model.factura.FacturaDto;
import com.business.unknow.model.factura.FacturaFileDto;
import com.business.unknow.services.entities.Pago;
import com.business.unknow.services.entities.catalogs.StatusFactura;
import com.business.unknow.services.entities.factura.Factura;
import com.business.unknow.services.entities.factura.FacturaFile;

@Mapper(config = IgnoreUnmappedMapperConfig.class)
public interface FacturaMapper {
	
	@Mappings({ @Mapping(target = "cfdi", ignore = true)})
	FacturaDto getFacturaDtoFromEntity(Factura entity);
	List<FacturaDto> getFacturaDtosFromEntities(List<Factura> entities);

	Factura getEntityFromFacturaDto(FacturaDto dto);
	List<Factura> getEntitiesFromFacturaDtos(List<FacturaDto> dto);
	
	FacturaFile getEntityFromFacturaFileDto(FacturaFileDto dto);
	FacturaFileDto getFacturaFileDtoFromEntity(FacturaFile dto);
	
	Pago getEntityFromPagoDto(PagoDto dto);
	PagoDto getPagoDtoFromEntity(Pago dto);
	List<PagoDto> getPagosDtoFromEntity(List<Pago> dto);
	
	StatusFactura getEntityFromStatusFacturaDto(StatusFacturaDto dto);

}
