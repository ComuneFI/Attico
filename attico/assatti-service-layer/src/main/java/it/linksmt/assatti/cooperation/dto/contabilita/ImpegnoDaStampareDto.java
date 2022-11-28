package it.linksmt.assatti.cooperation.dto.contabilita;

import java.io.Serializable;
import java.util.List;

public class ImpegnoDaStampareDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4748698372537625243L;
	private String codiceCapitolo;
	private String meccanograficoCapitolo;
	private String descrizioneCapitolo;
	private String importoCapitolo;
	private String codiceImpegno;
	private String impegnoFormattato;
	private String datiAtto;
	private String tipoProvImp;
	private String organoProvImp;
	private String annoProvImp;
	private String numeroProvImp;
	private String dataEsecProvImp;
	private String descrizioneImpegno;
	private String importoImpegno;
	private String cupImpegno;
	private String cigImpegno;
	private String numeroLiq;
	private String annoLiq;
	
	private List<SoggettoDto> listaSoggetti;
	
	public String getCodiceCapitolo() {
		return codiceCapitolo;
	}
	public void setCodiceCapitolo(String codiceCapitolo) {
		this.codiceCapitolo = codiceCapitolo;
	}
	public String getMeccanograficoCapitolo() {
		return meccanograficoCapitolo;
	}
	public void setMeccanograficoCapitolo(String meccanograficoCapitolo) {
		this.meccanograficoCapitolo = meccanograficoCapitolo;
	}
	public String getDescrizioneCapitolo() {
		return descrizioneCapitolo;
	}
	public void setDescrizioneCapitolo(String descrizioneCapitolo) {
		this.descrizioneCapitolo = descrizioneCapitolo;
	}
	public String getImportoCapitolo() {
		return importoCapitolo;
	}
	public void setImportoCapitolo(String importoCapitolo) {
		this.importoCapitolo = importoCapitolo;
	}
	public String getCodiceImpegno() {
		return codiceImpegno;
	}
	public void setCodiceImpegno(String codiceImpegno) {
		this.codiceImpegno = codiceImpegno;
	}
	public String getImpegnoFormattato() {
		return impegnoFormattato;
	}
	public void setImpegnoFormattato(String impegnoFormattato) {
		this.impegnoFormattato = impegnoFormattato;
	}
	public String getDescrizioneImpegno() {
		return descrizioneImpegno;
	}
	public void setDescrizioneImpegno(String descrizioneImpegno) {
		this.descrizioneImpegno = descrizioneImpegno;
	}
	public String getImportoImpegno() {
		return importoImpegno;
	}
	public void setImportoImpegno(String importoImpegno) {
		this.importoImpegno = importoImpegno;
	}
	public String getCupImpegno() {
		return cupImpegno;
	}
	public void setCupImpegno(String cupImpegno) {
		this.cupImpegno = cupImpegno;
	}
	public String getCigImpegno() {
		return cigImpegno;
	}
	public void setCigImpegno(String cigImpegno) {
		this.cigImpegno = cigImpegno;
	}
	public List<SoggettoDto> getListaSoggetti() {
		return listaSoggetti;
	}
	public void setListaSoggetti(List<SoggettoDto> listaSoggetti) {
		this.listaSoggetti = listaSoggetti;
	}
	public String getDatiAtto() {
		return datiAtto;
	}
	public void setDatiAtto(String datiAtto) {
		this.datiAtto = datiAtto;
	}
	public String getTipoProvImp() {
		return tipoProvImp;
	}
	public void setTipoProvImp(String tipoProvImp) {
		this.tipoProvImp = tipoProvImp;
	}
	public String getOrganoProvImp() {
		return organoProvImp;
	}
	public void setOrganoProvImp(String organoProvImp) {
		this.organoProvImp = organoProvImp;
	}
	public String getAnnoProvImp() {
		return annoProvImp;
	}
	public void setAnnoProvImp(String annoProvImp) {
		this.annoProvImp = annoProvImp;
	}
	public String getNumeroProvImp() {
		return numeroProvImp;
	}
	public void setNumeroProvImp(String numeroProvImp) {
		this.numeroProvImp = numeroProvImp;
	}
	public String getDataEsecProvImp() {
		return dataEsecProvImp;
	}
	public void setDataEsecProvImp(String dataEsecProvImp) {
		this.dataEsecProvImp = dataEsecProvImp;
	}
	public String getNumeroLiq() {
		return numeroLiq;
	}
	public void setNumeroLiq(String numeroLiq) {
		this.numeroLiq = numeroLiq;
	}
	public String getAnnoLiq() {
		return annoLiq;
	}
	public void setAnnoLiq(String annoLiq) {
		this.annoLiq = annoLiq;
	}
}
