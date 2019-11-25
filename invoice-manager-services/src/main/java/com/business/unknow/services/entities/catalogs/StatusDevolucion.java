package com.business.unknow.services.entities.catalogs;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "STATUS_DEVOLUCION")
public class StatusDevolucion implements Serializable {

	private static final long serialVersionUID = 7785962664267962428L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID_STATUS_DEVOLUCION")
	private Integer id;

	@Column(name = "VALUE")
	private String value;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "StatusDevolucion [id=" + id + ", value=" + value + "]";
	}

}