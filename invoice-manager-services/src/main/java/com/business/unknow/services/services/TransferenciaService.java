/**
 * 
 */
package com.business.unknow.services.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.business.unknow.model.TransferenciaDto;
import com.business.unknow.services.entities.Transferencia;
import com.business.unknow.services.mapper.TransferenciaMapper;
import com.business.unknow.services.repositories.TransferenciaRepository;

/**
 * @author hha0009
 *
 */
@Service
public class TransferenciaService {
	
	@Autowired
	private TransferenciaRepository repository;
	
	@Autowired
	private TransferenciaMapper mapper;
	
	
	public Page<TransferenciaDto> getTransferenciasPaginated(String from, String to, int page, int size){
		Page<Transferencia> result = repository.findTransfersFromTo(String.format("%%%s%%", from), String.format("%%%s%%", to), PageRequest.of(page, size));
		return new PageImpl<>(mapper.getTransferenciasDtoFromEntities(result.getContent()), result.getPageable(), result.getTotalElements());
	}
	
	public List<TransferenciaDto> saveTransferencias(List<TransferenciaDto> transferencias) {
		List<Transferencia> transfers = mapper.getEntitiesFromTransferenciasDto(transferencias);
		return mapper.getTransferenciasDtoFromEntities(repository.saveAll(transfers));
	}

}
