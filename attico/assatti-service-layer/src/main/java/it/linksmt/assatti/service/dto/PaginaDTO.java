package it.linksmt.assatti.service.dto;

import java.util.List;


public class PaginaDTO {
	private List<?> listaOggetti;
	private Long totaleElementi;
	
	public List<?> getListaOggetti() {
		return listaOggetti;
	}
	public void setListaOggetti(List<?> listaOggetti) {
		this.listaOggetti = listaOggetti;
	}
	public Long getTotaleElementi() {
		return totaleElementi;
	}
	public void setTotaleElementi(Long totaleElementi) {
		this.totaleElementi = totaleElementi;
	}
}
