package com.business.unknow.services.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.business.unknow.services.entities.catalogs.StatusEvento;


public interface StatusEventoRepository extends JpaRepository<StatusEvento, Integer> {
	List<StatusEvento> findAll();
}