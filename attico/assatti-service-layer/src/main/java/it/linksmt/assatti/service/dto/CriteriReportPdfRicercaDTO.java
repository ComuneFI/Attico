package it.linksmt.assatti.service.dto;

import java.io.Serializable;

public class CriteriReportPdfRicercaDTO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3825954539918961171L;

	private String tipoAtto;
	private String viewtype;
	private String anno;
	
	public String getTipoAtto() {
		return tipoAtto;
	}
	public String getViewtype() {
		return viewtype;
	}
	public String getAnno() {
		return anno;
	}
	public void setTipoAtto(String tipoAtto) {
		this.tipoAtto = tipoAtto;
	}
	public void setViewtype(String viewtype) {
		this.viewtype = viewtype;
	}
	public void setAnno(String anno) {
		this.anno = anno;
	}
}
