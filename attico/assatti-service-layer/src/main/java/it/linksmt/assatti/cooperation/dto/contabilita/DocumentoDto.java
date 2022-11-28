package it.linksmt.assatti.cooperation.dto.contabilita;

public class DocumentoDto implements Comparable<DocumentoDto>{

	private String annoDoc;
    private String numeroDoc;
    private String tipoDoc;
    private String descrTipoDoc;
    private String dataDoc;
    private String oggetto;
    private String importo;
    private String dataRegistrDoc;
    private String dataScadDoc;
    private String importoTotaleDoc;
    private String numeroRata;
    private String tipoAttoLiq;
    private String organoAttoLiq;
    private String annoAttoLiq;
    private String numeroAttoLiq;
    private String dataAttoLiq;
    private String dataEsecAttoLiq;
    private String numeroRegistrazione;
    private String ritenute;
    private String codiceCig;
    private String codiceCup;
    private String cdc;
    protected String cdcRiga;
	public String getAnnoDoc() {
		return annoDoc;
	}
	public void setAnnoDoc(String annoDoc) {
		this.annoDoc = annoDoc;
	}
	public String getNumeroDoc() {
		return numeroDoc;
	}
	public void setNumeroDoc(String numeroDoc) {
		this.numeroDoc = numeroDoc;
	}
	public String getTipoDoc() {
		return tipoDoc;
	}
	public void setTipoDoc(String tipoDoc) {
		this.tipoDoc = tipoDoc;
	}
	public String getDescrTipoDoc() {
		return descrTipoDoc;
	}
	public void setDescrTipoDoc(String descrTipoDoc) {
		this.descrTipoDoc = descrTipoDoc;
	}
	public String getDataDoc() {
		return dataDoc;
	}
	public void setDataDoc(String dataDoc) {
		this.dataDoc = dataDoc;
	}
	public String getOggetto() {
		return oggetto;
	}
	public void setOggetto(String oggetto) {
		this.oggetto = oggetto;
	}
	public String getImporto() {
		return importo;
	}
	public void setImporto(String importo) {
		this.importo = importo;
	}
	public String getDataRegistrDoc() {
		return dataRegistrDoc;
	}
	public void setDataRegistrDoc(String dataRegistrDoc) {
		this.dataRegistrDoc = dataRegistrDoc;
	}
	public String getDataScadDoc() {
		return dataScadDoc;
	}
	public void setDataScadDoc(String dataScadDoc) {
		this.dataScadDoc = dataScadDoc;
	}
	public String getImportoTotaleDoc() {
		return importoTotaleDoc;
	}
	public void setImportoTotaleDoc(String importoTotaleDoc) {
		this.importoTotaleDoc = importoTotaleDoc;
	}
	public String getNumeroRata() {
		return numeroRata;
	}
	public void setNumeroRata(String numeroRata) {
		this.numeroRata = numeroRata;
	}
	public String getTipoAttoLiq() {
		return tipoAttoLiq;
	}
	public void setTipoAttoLiq(String tipoAttoLiq) {
		this.tipoAttoLiq = tipoAttoLiq;
	}
	public String getOrganoAttoLiq() {
		return organoAttoLiq;
	}
	public void setOrganoAttoLiq(String organoAttoLiq) {
		this.organoAttoLiq = organoAttoLiq;
	}
	public String getAnnoAttoLiq() {
		return annoAttoLiq;
	}
	public void setAnnoAttoLiq(String annoAttoLiq) {
		this.annoAttoLiq = annoAttoLiq;
	}
	public String getNumeroAttoLiq() {
		return numeroAttoLiq;
	}
	public void setNumeroAttoLiq(String numeroAttoLiq) {
		this.numeroAttoLiq = numeroAttoLiq;
	}
	public String getDataAttoLiq() {
		return dataAttoLiq;
	}
	public void setDataAttoLiq(String dataAttoLiq) {
		this.dataAttoLiq = dataAttoLiq;
	}
	public String getDataEsecAttoLiq() {
		return dataEsecAttoLiq;
	}
	public void setDataEsecAttoLiq(String dataEsecAttoLiq) {
		this.dataEsecAttoLiq = dataEsecAttoLiq;
	}
	public String getNumeroRegistrazione() {
		return numeroRegistrazione;
	}
	public void setNumeroRegistrazione(String numeroRegistrazione) {
		this.numeroRegistrazione = numeroRegistrazione;
	}
	public String getRitenute() {
		return ritenute;
	}
	public void setRitenute(String ritenute) {
		this.ritenute = ritenute;
	}
	public String getCodiceCig() {
		return codiceCig;
	}
	public void setCodiceCig(String codiceCig) {
		this.codiceCig = codiceCig;
	}
	public String getCodiceCup() {
		return codiceCup;
	}
	public void setCodiceCup(String codiceCup) {
		this.codiceCup = codiceCup;
	}
	public String getCdc() {
		return cdc;
	}
	public void setCdc(String cdc) {
		this.cdc = cdc;
	}
	public String getCdcRiga() {
		return cdcRiga;
	}
	public void setCdcRiga(String cdcRiga) {
		this.cdcRiga = cdcRiga;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((annoAttoLiq == null) ? 0 : annoAttoLiq.hashCode());
		result = prime * result + ((annoDoc == null) ? 0 : annoDoc.hashCode());
		result = prime * result + ((cdc == null) ? 0 : cdc.hashCode());
		result = prime * result + ((cdcRiga == null) ? 0 : cdcRiga.hashCode());
		result = prime * result + ((codiceCig == null) ? 0 : codiceCig.hashCode());
		result = prime * result + ((codiceCup == null) ? 0 : codiceCup.hashCode());
		result = prime * result + ((dataAttoLiq == null) ? 0 : dataAttoLiq.hashCode());
		result = prime * result + ((dataDoc == null) ? 0 : dataDoc.hashCode());
		result = prime * result + ((dataEsecAttoLiq == null) ? 0 : dataEsecAttoLiq.hashCode());
		result = prime * result + ((dataRegistrDoc == null) ? 0 : dataRegistrDoc.hashCode());
		result = prime * result + ((dataScadDoc == null) ? 0 : dataScadDoc.hashCode());
		result = prime * result + ((descrTipoDoc == null) ? 0 : descrTipoDoc.hashCode());
		result = prime * result + ((importo == null) ? 0 : importo.hashCode());
		result = prime * result + ((importoTotaleDoc == null) ? 0 : importoTotaleDoc.hashCode());
		result = prime * result + ((numeroAttoLiq == null) ? 0 : numeroAttoLiq.hashCode());
		result = prime * result + ((numeroDoc == null) ? 0 : numeroDoc.hashCode());
		result = prime * result + ((numeroRata == null) ? 0 : numeroRata.hashCode());
		result = prime * result + ((numeroRegistrazione == null) ? 0 : numeroRegistrazione.hashCode());
		result = prime * result + ((oggetto == null) ? 0 : oggetto.hashCode());
		result = prime * result + ((organoAttoLiq == null) ? 0 : organoAttoLiq.hashCode());
		result = prime * result + ((ritenute == null) ? 0 : ritenute.hashCode());
		result = prime * result + ((tipoAttoLiq == null) ? 0 : tipoAttoLiq.hashCode());
		result = prime * result + ((tipoDoc == null) ? 0 : tipoDoc.hashCode());
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
		DocumentoDto other = (DocumentoDto) obj;
		if (annoAttoLiq == null) {
			if (other.annoAttoLiq != null)
				return false;
		} else if (!annoAttoLiq.equals(other.annoAttoLiq))
			return false;
		if (annoDoc == null) {
			if (other.annoDoc != null)
				return false;
		} else if (!annoDoc.equals(other.annoDoc))
			return false;
		if (cdc == null) {
			if (other.cdc != null)
				return false;
		} else if (!cdc.equals(other.cdc))
			return false;
		if (cdcRiga == null) {
			if (other.cdcRiga != null)
				return false;
		} else if (!cdcRiga.equals(other.cdcRiga))
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
		if (dataAttoLiq == null) {
			if (other.dataAttoLiq != null)
				return false;
		} else if (!dataAttoLiq.equals(other.dataAttoLiq))
			return false;
		if (dataDoc == null) {
			if (other.dataDoc != null)
				return false;
		} else if (!dataDoc.equals(other.dataDoc))
			return false;
		if (dataEsecAttoLiq == null) {
			if (other.dataEsecAttoLiq != null)
				return false;
		} else if (!dataEsecAttoLiq.equals(other.dataEsecAttoLiq))
			return false;
		if (dataRegistrDoc == null) {
			if (other.dataRegistrDoc != null)
				return false;
		} else if (!dataRegistrDoc.equals(other.dataRegistrDoc))
			return false;
		if (dataScadDoc == null) {
			if (other.dataScadDoc != null)
				return false;
		} else if (!dataScadDoc.equals(other.dataScadDoc))
			return false;
		if (descrTipoDoc == null) {
			if (other.descrTipoDoc != null)
				return false;
		} else if (!descrTipoDoc.equals(other.descrTipoDoc))
			return false;
		if (importo == null) {
			if (other.importo != null)
				return false;
		} else if (!importo.equals(other.importo))
			return false;
		if (importoTotaleDoc == null) {
			if (other.importoTotaleDoc != null)
				return false;
		} else if (!importoTotaleDoc.equals(other.importoTotaleDoc))
			return false;
		if (numeroAttoLiq == null) {
			if (other.numeroAttoLiq != null)
				return false;
		} else if (!numeroAttoLiq.equals(other.numeroAttoLiq))
			return false;
		if (numeroDoc == null) {
			if (other.numeroDoc != null)
				return false;
		} else if (!numeroDoc.equals(other.numeroDoc))
			return false;
		if (numeroRata == null) {
			if (other.numeroRata != null)
				return false;
		} else if (!numeroRata.equals(other.numeroRata))
			return false;
		if (numeroRegistrazione == null) {
			if (other.numeroRegistrazione != null)
				return false;
		} else if (!numeroRegistrazione.equals(other.numeroRegistrazione))
			return false;
		if (oggetto == null) {
			if (other.oggetto != null)
				return false;
		} else if (!oggetto.equals(other.oggetto))
			return false;
		if (organoAttoLiq == null) {
			if (other.organoAttoLiq != null)
				return false;
		} else if (!organoAttoLiq.equals(other.organoAttoLiq))
			return false;
		if (ritenute == null) {
			if (other.ritenute != null)
				return false;
		} else if (!ritenute.equals(other.ritenute))
			return false;
		if (tipoAttoLiq == null) {
			if (other.tipoAttoLiq != null)
				return false;
		} else if (!tipoAttoLiq.equals(other.tipoAttoLiq))
			return false;
		if (tipoDoc == null) {
			if (other.tipoDoc != null)
				return false;
		} else if (!tipoDoc.equals(other.tipoDoc))
			return false;
		return true;
	}
	public int compareTo(DocumentoDto o) {
		if(o.hashCode() == this.hashCode()) {
			return 0;
		}else if(o.hashCode() > this.hashCode()) {
			return 1;
		}else {
			return -1;
		}
	}
	
}
