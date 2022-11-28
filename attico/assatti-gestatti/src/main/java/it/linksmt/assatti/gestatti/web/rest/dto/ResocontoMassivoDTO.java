package it.linksmt.assatti.gestatti.web.rest.dto;

import it.linksmt.assatti.service.dto.ResocontoDTO;

import java.io.Serializable;
import java.util.List;


public class ResocontoMassivoDTO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ResocontoDTO resoconto;
	private List<Long> atti;
	
	public ResocontoDTO getResoconto() {
		return resoconto;
	}
	public void setResoconto(ResocontoDTO resoconto) {
		this.resoconto = resoconto;
	}
	public List<Long> getAtti() {
		return atti;
	}
	public void setAtti(List<Long> atti) {
		this.atti = atti;
	}

}
