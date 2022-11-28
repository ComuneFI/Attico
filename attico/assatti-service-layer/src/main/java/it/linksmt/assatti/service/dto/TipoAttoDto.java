package it.linksmt.assatti.service.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import it.linksmt.assatti.datalayer.domain.Annullamento;
import it.linksmt.assatti.datalayer.domain.ModelloHtml;
import it.linksmt.assatti.datalayer.domain.TipoAttoHasFaseRicerca;
import it.linksmt.assatti.datalayer.domain.TipoIter;
import it.linksmt.assatti.datalayer.domain.TipoProgressivo;

public class TipoAttoDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;

	private String codice;

	private String descrizione;

	private String processoBpmName;
	
	private Integer giorniPubblicazioneAlbo;
	
	private Boolean progressivoPropostaAoo = Boolean.FALSE;
	
	private Boolean progressivoAdozioneAoo = Boolean.FALSE;
	
	private Boolean enabled;
	
	private Set<Annullamento> statiAnnullamento = new HashSet<Annullamento>();
	
	private TipoProgressivo tipoProgressivo;
	
	private ModelloHtml modelloHtmlCopiaNonConforme;
	
	private Set<TipoIter> tipiIter = new HashSet<TipoIter>();
	
	private List<SezioneTipoAttoDto> sezioni = new ArrayList<>();
	
	private List<CampoTipoAttoDto> campi = new ArrayList<>();

	private Boolean atti;
	
	private Boolean proponente;
	
	private Boolean consiglio;
	
	private Boolean giunta;
	
	private Boolean attoRevocatoHidden;
	
	private Boolean tipoIterHidden;
	
	private Boolean codiceCupHidden;
	private Boolean codiceCigHidden;
	private Boolean tipoFinanziamentoHidden;
	
	private Set<TipoAttoHasFaseRicerca> fasiRicerca = new HashSet<TipoAttoHasFaseRicerca>();
	
	private Boolean statoConclusoCompleto;
	private Boolean statoConclusoRitirato;
	private Boolean statoConclusoAnnullato;
	private Boolean statoConclusoRespinto;
	private Boolean statoConclusoDecaduto;
	private Boolean statoConclusoDataEsecutivita;
	private Boolean statoConclusoAttesaRelata;
	private Boolean statoConclusoArchiviato;

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		TipoAttoDto tipoAtto = (TipoAttoDto) o;

		if (!Objects.equals(id, tipoAtto.id)) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}
	

	/*
	 * Get & Set
	 */

	public Long getId() {
		return id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public String getCodice() {
		return codice;
	}

	public void setCodice(final String codice) {
		this.codice = codice;
	}

	public String getProcessoBpmName() {
		return processoBpmName;
	}

	public void setProcessoBpmName(final String processoBpmName) {
		this.processoBpmName = processoBpmName;
	}

	public Integer getGiorniPubblicazioneAlbo() {
		return giorniPubblicazioneAlbo;
	}

	public void setGiorniPubblicazioneAlbo(Integer giorniPubblicazioneAlbo) {
		this.giorniPubblicazioneAlbo = giorniPubblicazioneAlbo;
	}

	public Boolean getProgressivoPropostaAoo() {
		return progressivoPropostaAoo;
	}

	public void setProgressivoPropostaAoo(Boolean progressivoPropostaAoo) {
		this.progressivoPropostaAoo = progressivoPropostaAoo;
	}

	public Boolean getProgressivoAdozioneAoo() {
		return progressivoAdozioneAoo;
	}

	public void setProgressivoAdozioneAoo(Boolean progressivoAdozioneAoo) {
		this.progressivoAdozioneAoo = progressivoAdozioneAoo;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(final String descrizione) {
		this.descrizione = descrizione;
	}

	public Set<TipoIter> getTipiIter() {
		return tipiIter;
	}

	public void setTipiIter(final Set<TipoIter> tipiIter) {
		this.tipiIter = tipiIter;
	}

	public Set<Annullamento> getStatiAnnullamento() {
		return statiAnnullamento;
	}
	public void setStatiAnnullamento(Set<Annullamento> statiAnnullamento) {
		this.statiAnnullamento = statiAnnullamento;
	}
	
	public TipoProgressivo getTipoProgressivo() {
		return tipoProgressivo;
	}

	public void setTipoProgressivo(TipoProgressivo tipoProgressivo) {
		this.tipoProgressivo = tipoProgressivo;
	}
	
	public ModelloHtml getModelloHtmlCopiaNonConforme() {
		return modelloHtmlCopiaNonConforme;
	}

	public void setModelloHtmlCopiaNonConforme(ModelloHtml modelloHtmlCopiaNonConforme) {
		this.modelloHtmlCopiaNonConforme = modelloHtmlCopiaNonConforme;
	}

	public Boolean getAtti() {
		return atti;
	}
	
	public void setAtti(Boolean atti) {
		this.atti = atti;
	}

	public List<SezioneTipoAttoDto> getSezioni() {
		return sezioni;
	}

	public void setSezioni(List<SezioneTipoAttoDto> sezioni) {
		this.sezioni = sezioni;
	}
	
	public Boolean getProponente() {
		return proponente;
	}

	public void setProponente(Boolean proponente) {
		this.proponente = proponente;
	}

	public Boolean getConsiglio() {
		return consiglio;
	}

	public void setConsiglio(Boolean consiglio) {
		this.consiglio = consiglio;
	}

	public Boolean getGiunta() {
		return giunta;
	}

	public void setGiunta(Boolean giunta) {
		this.giunta = giunta;
	}

	public Set<TipoAttoHasFaseRicerca> getFasiRicerca() {
		return fasiRicerca;
	}

	public void setFasiRicerca(Set<TipoAttoHasFaseRicerca> fasiRicerca) {
		this.fasiRicerca = fasiRicerca;
	}

	public Boolean getAttoRevocatoHidden() {
		return attoRevocatoHidden;
	}

	public void setAttoRevocatoHidden(Boolean attoRevocatoHidden) {
		this.attoRevocatoHidden = attoRevocatoHidden;
	}

	public Boolean getTipoIterHidden() {
		return tipoIterHidden;
	}

	public void setTipoIterHidden(Boolean tipoIterHidden) {
		this.tipoIterHidden = tipoIterHidden;
	}

	public Boolean getStatoConclusoCompleto() {
		return statoConclusoCompleto;
	}

	public void setStatoConclusoCompleto(Boolean statoConclusoCompleto) {
		this.statoConclusoCompleto = statoConclusoCompleto;
	}

	public Boolean getStatoConclusoRitirato() {
		return statoConclusoRitirato;
	}

	public void setStatoConclusoRitirato(Boolean statoConclusoRitirato) {
		this.statoConclusoRitirato = statoConclusoRitirato;
	}

	public Boolean getStatoConclusoAnnullato() {
		return statoConclusoAnnullato;
	}

	public void setStatoConclusoAnnullato(Boolean statoConclusoAnnullato) {
		this.statoConclusoAnnullato = statoConclusoAnnullato;
	}

	public Boolean getStatoConclusoRespinto() {
		return statoConclusoRespinto;
	}

	public void setStatoConclusoRespinto(Boolean statoConclusoRespinto) {
		this.statoConclusoRespinto = statoConclusoRespinto;
	}

	public Boolean getStatoConclusoDecaduto() {
		return statoConclusoDecaduto;
	}

	public void setStatoConclusoDecaduto(Boolean statoConclusoDecaduto) {
		this.statoConclusoDecaduto = statoConclusoDecaduto;
	}

	public Boolean getStatoConclusoDataEsecutivita() {
		return statoConclusoDataEsecutivita;
	}

	public void setStatoConclusoDataEsecutivita(Boolean statoConclusoDataEsecutivita) {
		this.statoConclusoDataEsecutivita = statoConclusoDataEsecutivita;
	}

	public Boolean getStatoConclusoAttesaRelata() {
		return statoConclusoAttesaRelata;
	}

	public void setStatoConclusoAttesaRelata(Boolean statoConclusoAttesaRelata) {
		this.statoConclusoAttesaRelata = statoConclusoAttesaRelata;
	}

	public Boolean getCodiceCupHidden() {
		return codiceCupHidden;
	}

	public void setCodiceCupHidden(Boolean codiceCupHidden) {
		this.codiceCupHidden = codiceCupHidden;
	}

	public Boolean getCodiceCigHidden() {
		return codiceCigHidden;
	}

	public void setCodiceCigHidden(Boolean codiceCigHidden) {
		this.codiceCigHidden = codiceCigHidden;
	}

	public Boolean getTipoFinanziamentoHidden() {
		return tipoFinanziamentoHidden;
	}

	public void setTipoFinanziamentoHidden(Boolean tipoFinanziamentoHidden) {
		this.tipoFinanziamentoHidden = tipoFinanziamentoHidden;
	}

	public List<CampoTipoAttoDto> getCampi() {
		return campi;
	}

	public void setCampi(List<CampoTipoAttoDto> campi) {
		this.campi = campi;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public Boolean getStatoConclusoArchiviato() {
		return statoConclusoArchiviato;
	}

	public void setStatoConclusoArchiviato(Boolean statoConclusoArchiviato) {
		this.statoConclusoArchiviato = statoConclusoArchiviato;
	}

}
