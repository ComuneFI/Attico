package it.linksmt.assatti.datalayer.domain.dto;

import it.linksmt.assatti.datalayer.domain.AttiOdg;

public class AttiOdgWithIEDto extends AttiOdg {

	private static final long serialVersionUID = 1L;

	private Integer numFavorevoliIE;
	private Integer numContrariIE;
	private Integer numAstenutiIE;
	private Integer numNpvIE;
	private Integer numPresentiIE;
	
	public Integer getNumFavorevoliIE() {
		return numFavorevoliIE;
	}
	public void setNumFavorevoliIE(Integer numFavorevoliIE) {
		this.numFavorevoliIE = numFavorevoliIE;
	}
	public Integer getNumContrariIE() {
		return numContrariIE;
	}
	public void setNumContrariIE(Integer numContrariIE) {
		this.numContrariIE = numContrariIE;
	}
	public Integer getNumAstenutiIE() {
		return numAstenutiIE;
	}
	public void setNumAstenutiIE(Integer numAstenutiIE) {
		this.numAstenutiIE = numAstenutiIE;
	}
	public Integer getNumNpvIE() {
		return numNpvIE;
	}
	public void setNumNpvIE(Integer numNpvIE) {
		this.numNpvIE = numNpvIE;
	}
	public Integer getNumPresentiIE() {
		return numPresentiIE;
	}
	public void setNumPresentiIE(Integer numPresentiIE) {
		this.numPresentiIE = numPresentiIE;
	}
}
