package it.linksmt.assatti.cooperation.dto.contabilita;

import java.io.Serializable;
import java.util.List;

public class ImpAccertamentoDto implements Serializable, Comparable<ImpAccertamentoDto> {

	private static final long serialVersionUID = 3512322531823548907L;
	
	private String archivio;
    private String tipoMovimento;
    private String esercizio;
    private String eu;
    private String capitolo;
    private String articolo;
    private String descCapitolo;
    private String annoImpacc;
    private String numeroImpacc;
    private String subImpacc;
    private String descImpacc;
    private String importoImpacc;
    private String liquidatoImpacc;
    private String ordinatoImpacc;
    private String oggetto;
    private String annoFinanziamento;
    private String respProc;
    private String descRespProc;
    private String tipologiaMovimento;
    private String importo;
    private String codiceObiettivo;
    private String descObiettivo;
    private String causaleObiettivo;
    private String descCausaleObiettivo;
    private String voceEconomica;
    private String descVoceEconomica;
    private String centroCosto;
    private String descCentroCosto;
    private String codDebBen;
    private String descCodDebBen;
    private String naturaCoge;
    private String descNaturaCoge;
    private String codiceCup;
    private String codiceCig;
    private String cespite;
    private String descCespite;
    private String codFondo;
    private String descFondo;
    private String codTipoFinanz;
    private String descTipoFinanz;
    private String siope;
    private String descSiope;
    private String mutuo;
    private String descMutuo;
    private String perfezionamento;
    private String descPerfezionamento;
    private String vincolo;
    private String descVincolo;
    private String programma;
    private String descProgramma;
    private String progetto;
    private String descProgetto;
    private String codMeccanografico;
    private String codArmonizzato;
    private String codLibroIva;
    private String descLibroIva;
    private String pianoFinanziario;
    private String pianoFinanziarioDesc;
    private String missioneCapitolo;
    private String programmaMissioneCapitolo;
    
    private List<AttributoDto> attributi;
    
	public String getArchivio() {
		return archivio;
	}
	public void setArchivio(String archivio) {
		this.archivio = archivio;
	}
	public String getTipoMovimento() {
		return tipoMovimento;
	}
	public void setTipoMovimento(String tipoMovimento) {
		this.tipoMovimento = tipoMovimento;
	}
	public String getEsercizio() {
		return esercizio;
	}
	public void setEsercizio(String esercizio) {
		this.esercizio = esercizio;
	}
	public String getEu() {
		return eu;
	}
	public void setEu(String eu) {
		this.eu = eu;
	}
	public String getCapitolo() {
		return capitolo;
	}
	public void setCapitolo(String capitolo) {
		this.capitolo = capitolo;
	}
	public String getArticolo() {
		return articolo;
	}
	public void setArticolo(String articolo) {
		this.articolo = articolo;
	}
	public String getDescCapitolo() {
		return descCapitolo;
	}
	public void setDescCapitolo(String descCapitolo) {
		this.descCapitolo = descCapitolo;
	}
	public String getAnnoImpacc() {
		return annoImpacc;
	}
	public void setAnnoImpacc(String annoImpacc) {
		this.annoImpacc = annoImpacc;
	}
	public String getNumeroImpacc() {
		return numeroImpacc;
	}
	public void setNumeroImpacc(String numeroImpacc) {
		this.numeroImpacc = numeroImpacc;
	}
	public String getSubImpacc() {
		return subImpacc;
	}
	public void setSubImpacc(String subImpacc) {
		this.subImpacc = subImpacc;
	}
	public String getDescImpacc() {
		return descImpacc;
	}
	public void setDescImpacc(String descImpacc) {
		this.descImpacc = descImpacc;
	}
	public String getImportoImpacc() {
		return importoImpacc;
	}
	public void setImportoImpacc(String importoImpacc) {
		this.importoImpacc = importoImpacc;
	}
	public String getLiquidatoImpacc() {
		return liquidatoImpacc;
	}
	public void setLiquidatoImpacc(String liquidatoImpacc) {
		this.liquidatoImpacc = liquidatoImpacc;
	}
	public String getOrdinatoImpacc() {
		return ordinatoImpacc;
	}
	public void setOrdinatoImpacc(String ordinatoImpacc) {
		this.ordinatoImpacc = ordinatoImpacc;
	}
	public String getOggetto() {
		return oggetto;
	}
	public void setOggetto(String oggetto) {
		this.oggetto = oggetto;
	}
	public String getAnnoFinanziamento() {
		return annoFinanziamento;
	}
	public void setAnnoFinanziamento(String annoFinanziamento) {
		this.annoFinanziamento = annoFinanziamento;
	}
	public String getRespProc() {
		return respProc;
	}
	public void setRespProc(String respProc) {
		this.respProc = respProc;
	}
	public String getDescRespProc() {
		return descRespProc;
	}
	public void setDescRespProc(String descRespProc) {
		this.descRespProc = descRespProc;
	}
	public String getTipologiaMovimento() {
		return tipologiaMovimento;
	}
	public void setTipologiaMovimento(String tipologiaMovimento) {
		this.tipologiaMovimento = tipologiaMovimento;
	}
	public String getImporto() {
		return importo;
	}
	public void setImporto(String importo) {
		this.importo = importo;
	}
	public String getCodiceObiettivo() {
		return codiceObiettivo;
	}
	public void setCodiceObiettivo(String codiceObiettivo) {
		this.codiceObiettivo = codiceObiettivo;
	}
	public String getDescObiettivo() {
		return descObiettivo;
	}
	public void setDescObiettivo(String descObiettivo) {
		this.descObiettivo = descObiettivo;
	}
	public String getCausaleObiettivo() {
		return causaleObiettivo;
	}
	public void setCausaleObiettivo(String causaleObiettivo) {
		this.causaleObiettivo = causaleObiettivo;
	}
	public String getDescCausaleObiettivo() {
		return descCausaleObiettivo;
	}
	public void setDescCausaleObiettivo(String descCausaleObiettivo) {
		this.descCausaleObiettivo = descCausaleObiettivo;
	}
	public String getVoceEconomica() {
		return voceEconomica;
	}
	public void setVoceEconomica(String voceEconomica) {
		this.voceEconomica = voceEconomica;
	}
	public String getDescVoceEconomica() {
		return descVoceEconomica;
	}
	public void setDescVoceEconomica(String descVoceEconomica) {
		this.descVoceEconomica = descVoceEconomica;
	}
	public String getCentroCosto() {
		return centroCosto;
	}
	public void setCentroCosto(String centroCosto) {
		this.centroCosto = centroCosto;
	}
	public String getDescCentroCosto() {
		return descCentroCosto;
	}
	public void setDescCentroCosto(String descCentroCosto) {
		this.descCentroCosto = descCentroCosto;
	}
	public String getCodDebBen() {
		return codDebBen;
	}
	public void setCodDebBen(String codDebBen) {
		this.codDebBen = codDebBen;
	}
	public String getDescCodDebBen() {
		return descCodDebBen;
	}
	public void setDescCodDebBen(String descCodDebBen) {
		this.descCodDebBen = descCodDebBen;
	}
	public String getNaturaCoge() {
		return naturaCoge;
	}
	public void setNaturaCoge(String naturaCoge) {
		this.naturaCoge = naturaCoge;
	}
	public String getDescNaturaCoge() {
		return descNaturaCoge;
	}
	public void setDescNaturaCoge(String descNaturaCoge) {
		this.descNaturaCoge = descNaturaCoge;
	}
	public String getCodiceCup() {
		return codiceCup;
	}
	public void setCodiceCup(String codiceCup) {
		this.codiceCup = codiceCup;
	}
	public String getCodiceCig() {
		return codiceCig;
	}
	public void setCodiceCig(String codiceCig) {
		this.codiceCig = codiceCig;
	}
	public String getCespite() {
		return cespite;
	}
	public void setCespite(String cespite) {
		this.cespite = cespite;
	}
	public String getDescCespite() {
		return descCespite;
	}
	public void setDescCespite(String descCespite) {
		this.descCespite = descCespite;
	}
	public String getCodFondo() {
		return codFondo;
	}
	public void setCodFondo(String codFondo) {
		this.codFondo = codFondo;
	}
	public String getDescFondo() {
		return descFondo;
	}
	public void setDescFondo(String descFondo) {
		this.descFondo = descFondo;
	}
	public String getCodTipoFinanz() {
		return codTipoFinanz;
	}
	public void setCodTipoFinanz(String codTipoFinanz) {
		this.codTipoFinanz = codTipoFinanz;
	}
	public String getDescTipoFinanz() {
		return descTipoFinanz;
	}
	public void setDescTipoFinanz(String descTipoFinanz) {
		this.descTipoFinanz = descTipoFinanz;
	}
	public String getSiope() {
		return siope;
	}
	public void setSiope(String siope) {
		this.siope = siope;
	}
	public String getDescSiope() {
		return descSiope;
	}
	public void setDescSiope(String descSiope) {
		this.descSiope = descSiope;
	}
	public String getMutuo() {
		return mutuo;
	}
	public void setMutuo(String mutuo) {
		this.mutuo = mutuo;
	}
	public String getDescMutuo() {
		return descMutuo;
	}
	public void setDescMutuo(String descMutuo) {
		this.descMutuo = descMutuo;
	}
	public String getPerfezionamento() {
		return perfezionamento;
	}
	public void setPerfezionamento(String perfezionamento) {
		this.perfezionamento = perfezionamento;
	}
	public String getDescPerfezionamento() {
		return descPerfezionamento;
	}
	public void setDescPerfezionamento(String descPerfezionamento) {
		this.descPerfezionamento = descPerfezionamento;
	}
	public String getVincolo() {
		return vincolo;
	}
	public void setVincolo(String vincolo) {
		this.vincolo = vincolo;
	}
	public String getDescVincolo() {
		return descVincolo;
	}
	public void setDescVincolo(String descVincolo) {
		this.descVincolo = descVincolo;
	}
	public String getProgramma() {
		return programma;
	}
	public void setProgramma(String programma) {
		this.programma = programma;
	}
	public String getDescProgramma() {
		return descProgramma;
	}
	public void setDescProgramma(String descProgramma) {
		this.descProgramma = descProgramma;
	}
	public String getProgetto() {
		return progetto;
	}
	public void setProgetto(String progetto) {
		this.progetto = progetto;
	}
	public String getDescProgetto() {
		return descProgetto;
	}
	public void setDescProgetto(String descProgetto) {
		this.descProgetto = descProgetto;
	}
	public String getCodMeccanografico() {
		return codMeccanografico;
	}
	public void setCodMeccanografico(String codMeccanografico) {
		this.codMeccanografico = codMeccanografico;
	}
	public String getCodArmonizzato() {
		return codArmonizzato;
	}
	public void setCodArmonizzato(String codArmonizzato) {
		this.codArmonizzato = codArmonizzato;
	}
	public String getCodLibroIva() {
		return codLibroIva;
	}
	public void setCodLibroIva(String codLibroIva) {
		this.codLibroIva = codLibroIva;
	}
	public String getDescLibroIva() {
		return descLibroIva;
	}
	public void setDescLibroIva(String descLibroIva) {
		this.descLibroIva = descLibroIva;
	}
	public String getPianoFinanziario() {
		return pianoFinanziario;
	}
	public void setPianoFinanziario(String pianoFinanziario) {
		this.pianoFinanziario = pianoFinanziario;
	}
	public String getPianoFinanziarioDesc() {
		return pianoFinanziarioDesc;
	}
	public void setPianoFinanziarioDesc(String pianoFinanziarioDesc) {
		this.pianoFinanziarioDesc = pianoFinanziarioDesc;
	}
	public String getMissioneCapitolo() {
		return missioneCapitolo;
	}
	public void setMissioneCapitolo(String missioneCapitolo) {
		this.missioneCapitolo = missioneCapitolo;
	}
	public String getProgrammaMissioneCapitolo() {
		return programmaMissioneCapitolo;
	}
	public void setProgrammaMissioneCapitolo(String programmaMissioneCapitolo) {
		this.programmaMissioneCapitolo = programmaMissioneCapitolo;
	}
	public List<AttributoDto> getAttributi() {
		return attributi;
	}
	public void setAttributi(List<AttributoDto> attributi) {
		this.attributi = attributi;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((annoFinanziamento == null) ? 0 : annoFinanziamento.hashCode());
		result = prime * result + ((annoImpacc == null) ? 0 : annoImpacc.hashCode());
		result = prime * result + ((archivio == null) ? 0 : archivio.hashCode());
		result = prime * result + ((articolo == null) ? 0 : articolo.hashCode());
		result = prime * result + ((attributi == null) ? 0 : attributi.hashCode());
		result = prime * result + ((capitolo == null) ? 0 : capitolo.hashCode());
		result = prime * result + ((causaleObiettivo == null) ? 0 : causaleObiettivo.hashCode());
		result = prime * result + ((centroCosto == null) ? 0 : centroCosto.hashCode());
		result = prime * result + ((cespite == null) ? 0 : cespite.hashCode());
		result = prime * result + ((codArmonizzato == null) ? 0 : codArmonizzato.hashCode());
		result = prime * result + ((codDebBen == null) ? 0 : codDebBen.hashCode());
		result = prime * result + ((codFondo == null) ? 0 : codFondo.hashCode());
		result = prime * result + ((codLibroIva == null) ? 0 : codLibroIva.hashCode());
		result = prime * result + ((codMeccanografico == null) ? 0 : codMeccanografico.hashCode());
		result = prime * result + ((codTipoFinanz == null) ? 0 : codTipoFinanz.hashCode());
		result = prime * result + ((codiceCig == null) ? 0 : codiceCig.hashCode());
		result = prime * result + ((codiceCup == null) ? 0 : codiceCup.hashCode());
		result = prime * result + ((codiceObiettivo == null) ? 0 : codiceObiettivo.hashCode());
		result = prime * result + ((descCapitolo == null) ? 0 : descCapitolo.hashCode());
		result = prime * result + ((descCausaleObiettivo == null) ? 0 : descCausaleObiettivo.hashCode());
		result = prime * result + ((descCentroCosto == null) ? 0 : descCentroCosto.hashCode());
		result = prime * result + ((descCespite == null) ? 0 : descCespite.hashCode());
		result = prime * result + ((descCodDebBen == null) ? 0 : descCodDebBen.hashCode());
		result = prime * result + ((descFondo == null) ? 0 : descFondo.hashCode());
		result = prime * result + ((descImpacc == null) ? 0 : descImpacc.hashCode());
		result = prime * result + ((descLibroIva == null) ? 0 : descLibroIva.hashCode());
		result = prime * result + ((descMutuo == null) ? 0 : descMutuo.hashCode());
		result = prime * result + ((descNaturaCoge == null) ? 0 : descNaturaCoge.hashCode());
		result = prime * result + ((descObiettivo == null) ? 0 : descObiettivo.hashCode());
		result = prime * result + ((descPerfezionamento == null) ? 0 : descPerfezionamento.hashCode());
		result = prime * result + ((descProgetto == null) ? 0 : descProgetto.hashCode());
		result = prime * result + ((descProgramma == null) ? 0 : descProgramma.hashCode());
		result = prime * result + ((descRespProc == null) ? 0 : descRespProc.hashCode());
		result = prime * result + ((descSiope == null) ? 0 : descSiope.hashCode());
		result = prime * result + ((descTipoFinanz == null) ? 0 : descTipoFinanz.hashCode());
		result = prime * result + ((descVincolo == null) ? 0 : descVincolo.hashCode());
		result = prime * result + ((descVoceEconomica == null) ? 0 : descVoceEconomica.hashCode());
		result = prime * result + ((esercizio == null) ? 0 : esercizio.hashCode());
		result = prime * result + ((eu == null) ? 0 : eu.hashCode());
		result = prime * result + ((importo == null) ? 0 : importo.hashCode());
		result = prime * result + ((importoImpacc == null) ? 0 : importoImpacc.hashCode());
		result = prime * result + ((liquidatoImpacc == null) ? 0 : liquidatoImpacc.hashCode());
		result = prime * result + ((missioneCapitolo == null) ? 0 : missioneCapitolo.hashCode());
		result = prime * result + ((mutuo == null) ? 0 : mutuo.hashCode());
		result = prime * result + ((naturaCoge == null) ? 0 : naturaCoge.hashCode());
		result = prime * result + ((numeroImpacc == null) ? 0 : numeroImpacc.hashCode());
		result = prime * result + ((oggetto == null) ? 0 : oggetto.hashCode());
		result = prime * result + ((ordinatoImpacc == null) ? 0 : ordinatoImpacc.hashCode());
		result = prime * result + ((perfezionamento == null) ? 0 : perfezionamento.hashCode());
		result = prime * result + ((pianoFinanziario == null) ? 0 : pianoFinanziario.hashCode());
		result = prime * result + ((pianoFinanziarioDesc == null) ? 0 : pianoFinanziarioDesc.hashCode());
		result = prime * result + ((progetto == null) ? 0 : progetto.hashCode());
		result = prime * result + ((programma == null) ? 0 : programma.hashCode());
		result = prime * result + ((programmaMissioneCapitolo == null) ? 0 : programmaMissioneCapitolo.hashCode());
		result = prime * result + ((respProc == null) ? 0 : respProc.hashCode());
		result = prime * result + ((siope == null) ? 0 : siope.hashCode());
		result = prime * result + ((subImpacc == null) ? 0 : subImpacc.hashCode());
		result = prime * result + ((tipoMovimento == null) ? 0 : tipoMovimento.hashCode());
		result = prime * result + ((tipologiaMovimento == null) ? 0 : tipologiaMovimento.hashCode());
		result = prime * result + ((vincolo == null) ? 0 : vincolo.hashCode());
		result = prime * result + ((voceEconomica == null) ? 0 : voceEconomica.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ImpAccertamentoDto other = (ImpAccertamentoDto) obj;
		if (annoFinanziamento == null) {
			if (other.annoFinanziamento != null)
				return false;
		} else if (!annoFinanziamento.equals(other.annoFinanziamento))
			return false;
		if (annoImpacc == null) {
			if (other.annoImpacc != null)
				return false;
		} else if (!annoImpacc.equals(other.annoImpacc))
			return false;
		if (archivio == null) {
			if (other.archivio != null)
				return false;
		} else if (!archivio.equals(other.archivio))
			return false;
		if (articolo == null) {
			if (other.articolo != null)
				return false;
		} else if (!articolo.equals(other.articolo))
			return false;
		if (attributi == null) {
			if (other.attributi != null)
				return false;
		} else if (!attributi.equals(other.attributi))
			return false;
		if (capitolo == null) {
			if (other.capitolo != null)
				return false;
		} else if (!capitolo.equals(other.capitolo))
			return false;
		if (causaleObiettivo == null) {
			if (other.causaleObiettivo != null)
				return false;
		} else if (!causaleObiettivo.equals(other.causaleObiettivo))
			return false;
		if (centroCosto == null) {
			if (other.centroCosto != null)
				return false;
		} else if (!centroCosto.equals(other.centroCosto))
			return false;
		if (cespite == null) {
			if (other.cespite != null)
				return false;
		} else if (!cespite.equals(other.cespite))
			return false;
		if (codArmonizzato == null) {
			if (other.codArmonizzato != null)
				return false;
		} else if (!codArmonizzato.equals(other.codArmonizzato))
			return false;
		if (codDebBen == null) {
			if (other.codDebBen != null)
				return false;
		} else if (!codDebBen.equals(other.codDebBen))
			return false;
		if (codFondo == null) {
			if (other.codFondo != null)
				return false;
		} else if (!codFondo.equals(other.codFondo))
			return false;
		if (codLibroIva == null) {
			if (other.codLibroIva != null)
				return false;
		} else if (!codLibroIva.equals(other.codLibroIva))
			return false;
		if (codMeccanografico == null) {
			if (other.codMeccanografico != null)
				return false;
		} else if (!codMeccanografico.equals(other.codMeccanografico))
			return false;
		if (codTipoFinanz == null) {
			if (other.codTipoFinanz != null)
				return false;
		} else if (!codTipoFinanz.equals(other.codTipoFinanz))
			return false;
		if (codiceCig == null) {
			if (other.codiceCig != null)
				return false;
		} else if (!codiceCig.equals(other.codiceCig))
			return false;
		if (codiceCup == null) {
			if (other.codiceCup != null)
				return false;
		} else if (!codiceCup.equals(other.codiceCup))
			return false;
		if (codiceObiettivo == null) {
			if (other.codiceObiettivo != null)
				return false;
		} else if (!codiceObiettivo.equals(other.codiceObiettivo))
			return false;
		if (descCapitolo == null) {
			if (other.descCapitolo != null)
				return false;
		} else if (!descCapitolo.equals(other.descCapitolo))
			return false;
		if (descCausaleObiettivo == null) {
			if (other.descCausaleObiettivo != null)
				return false;
		} else if (!descCausaleObiettivo.equals(other.descCausaleObiettivo))
			return false;
		if (descCentroCosto == null) {
			if (other.descCentroCosto != null)
				return false;
		} else if (!descCentroCosto.equals(other.descCentroCosto))
			return false;
		if (descCespite == null) {
			if (other.descCespite != null)
				return false;
		} else if (!descCespite.equals(other.descCespite))
			return false;
		if (descCodDebBen == null) {
			if (other.descCodDebBen != null)
				return false;
		} else if (!descCodDebBen.equals(other.descCodDebBen))
			return false;
		if (descFondo == null) {
			if (other.descFondo != null)
				return false;
		} else if (!descFondo.equals(other.descFondo))
			return false;
		if (descImpacc == null) {
			if (other.descImpacc != null)
				return false;
		} else if (!descImpacc.equals(other.descImpacc))
			return false;
		if (descLibroIva == null) {
			if (other.descLibroIva != null)
				return false;
		} else if (!descLibroIva.equals(other.descLibroIva))
			return false;
		if (descMutuo == null) {
			if (other.descMutuo != null)
				return false;
		} else if (!descMutuo.equals(other.descMutuo))
			return false;
		if (descNaturaCoge == null) {
			if (other.descNaturaCoge != null)
				return false;
		} else if (!descNaturaCoge.equals(other.descNaturaCoge))
			return false;
		if (descObiettivo == null) {
			if (other.descObiettivo != null)
				return false;
		} else if (!descObiettivo.equals(other.descObiettivo))
			return false;
		if (descPerfezionamento == null) {
			if (other.descPerfezionamento != null)
				return false;
		} else if (!descPerfezionamento.equals(other.descPerfezionamento))
			return false;
		if (descProgetto == null) {
			if (other.descProgetto != null)
				return false;
		} else if (!descProgetto.equals(other.descProgetto))
			return false;
		if (descProgramma == null) {
			if (other.descProgramma != null)
				return false;
		} else if (!descProgramma.equals(other.descProgramma))
			return false;
		if (descRespProc == null) {
			if (other.descRespProc != null)
				return false;
		} else if (!descRespProc.equals(other.descRespProc))
			return false;
		if (descSiope == null) {
			if (other.descSiope != null)
				return false;
		} else if (!descSiope.equals(other.descSiope))
			return false;
		if (descTipoFinanz == null) {
			if (other.descTipoFinanz != null)
				return false;
		} else if (!descTipoFinanz.equals(other.descTipoFinanz))
			return false;
		if (descVincolo == null) {
			if (other.descVincolo != null)
				return false;
		} else if (!descVincolo.equals(other.descVincolo))
			return false;
		if (descVoceEconomica == null) {
			if (other.descVoceEconomica != null)
				return false;
		} else if (!descVoceEconomica.equals(other.descVoceEconomica))
			return false;
		if (esercizio == null) {
			if (other.esercizio != null)
				return false;
		} else if (!esercizio.equals(other.esercizio))
			return false;
		if (eu == null) {
			if (other.eu != null)
				return false;
		} else if (!eu.equals(other.eu))
			return false;
		if (importo == null) {
			if (other.importo != null)
				return false;
		} else if (!importo.equals(other.importo))
			return false;
		if (importoImpacc == null) {
			if (other.importoImpacc != null)
				return false;
		} else if (!importoImpacc.equals(other.importoImpacc))
			return false;
		if (liquidatoImpacc == null) {
			if (other.liquidatoImpacc != null)
				return false;
		} else if (!liquidatoImpacc.equals(other.liquidatoImpacc))
			return false;
		if (missioneCapitolo == null) {
			if (other.missioneCapitolo != null)
				return false;
		} else if (!missioneCapitolo.equals(other.missioneCapitolo))
			return false;
		if (mutuo == null) {
			if (other.mutuo != null)
				return false;
		} else if (!mutuo.equals(other.mutuo))
			return false;
		if (naturaCoge == null) {
			if (other.naturaCoge != null)
				return false;
		} else if (!naturaCoge.equals(other.naturaCoge))
			return false;
		if (numeroImpacc == null) {
			if (other.numeroImpacc != null)
				return false;
		} else if (!numeroImpacc.equals(other.numeroImpacc))
			return false;
		if (oggetto == null) {
			if (other.oggetto != null)
				return false;
		} else if (!oggetto.equals(other.oggetto))
			return false;
		if (ordinatoImpacc == null) {
			if (other.ordinatoImpacc != null)
				return false;
		} else if (!ordinatoImpacc.equals(other.ordinatoImpacc))
			return false;
		if (perfezionamento == null) {
			if (other.perfezionamento != null)
				return false;
		} else if (!perfezionamento.equals(other.perfezionamento))
			return false;
		if (pianoFinanziario == null) {
			if (other.pianoFinanziario != null)
				return false;
		} else if (!pianoFinanziario.equals(other.pianoFinanziario))
			return false;
		if (pianoFinanziarioDesc == null) {
			if (other.pianoFinanziarioDesc != null)
				return false;
		} else if (!pianoFinanziarioDesc.equals(other.pianoFinanziarioDesc))
			return false;
		if (progetto == null) {
			if (other.progetto != null)
				return false;
		} else if (!progetto.equals(other.progetto))
			return false;
		if (programma == null) {
			if (other.programma != null)
				return false;
		} else if (!programma.equals(other.programma))
			return false;
		if (programmaMissioneCapitolo == null) {
			if (other.programmaMissioneCapitolo != null)
				return false;
		} else if (!programmaMissioneCapitolo.equals(other.programmaMissioneCapitolo))
			return false;
		if (respProc == null) {
			if (other.respProc != null)
				return false;
		} else if (!respProc.equals(other.respProc))
			return false;
		if (siope == null) {
			if (other.siope != null)
				return false;
		} else if (!siope.equals(other.siope))
			return false;
		if (subImpacc == null) {
			if (other.subImpacc != null)
				return false;
		} else if (!subImpacc.equals(other.subImpacc))
			return false;
		if (tipoMovimento == null) {
			if (other.tipoMovimento != null)
				return false;
		} else if (!tipoMovimento.equals(other.tipoMovimento))
			return false;
		if (tipologiaMovimento == null) {
			if (other.tipologiaMovimento != null)
				return false;
		} else if (!tipologiaMovimento.equals(other.tipologiaMovimento))
			return false;
		if (vincolo == null) {
			if (other.vincolo != null)
				return false;
		} else if (!vincolo.equals(other.vincolo))
			return false;
		if (voceEconomica == null) {
			if (other.voceEconomica != null)
				return false;
		} else if (!voceEconomica.equals(other.voceEconomica))
			return false;
		return true;
	}
	@Override
	public int compareTo(ImpAccertamentoDto o) {
		if(o.hashCode() == this.hashCode()) {
			return 0;
		}else if(o.hashCode() > this.hashCode()) {
			return 1;
		}else {
			return -1;
		}
	}
}
