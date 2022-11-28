package it.linksmt.assatti.cooperation.dto.contabilita;

import java.util.List;


public class DettaglioLiquidazioneDto implements Comparable<DettaglioLiquidazioneDto> {

	private String id;
    private String idLiquidazione;
    private String annoLiq;
    private String numeroLiq;
    private String dataLiq;
    private String esercizio;
    private String codRproc;
    private String descRProc;
    private String nomeRProc;
    private String nomeRProc2;
    private String dirigente;
    private String tipoAttoLiq;
    private String organoAttoLiq;
    private String annoAttoLiq;
    private String numeroAttoLiq;
    private String dataAttoLiq;
    private String dataEsecAttoLiq;
    private String descLiquidazione;
    private String totaleLiq;
    private String totaleImponibileLiq;
    private String totaleRitenutaLiq;
    private String numeroElenco;
    private String numeroRighe;

    private List<RigaLiquidazioneDto> listaCapitoli;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIdLiquidazione() {
		return idLiquidazione;
	}

	public void setIdLiquidazione(String idLiquidazione) {
		this.idLiquidazione = idLiquidazione;
	}

	public String getAnnoLiq() {
		return annoLiq;
	}

	public void setAnnoLiq(String annoLiq) {
		this.annoLiq = annoLiq;
	}

	public String getNumeroLiq() {
		return numeroLiq;
	}

	public void setNumeroLiq(String numeroLiq) {
		this.numeroLiq = numeroLiq;
	}

	public String getDataLiq() {
		return dataLiq;
	}

	public void setDataLiq(String dataLiq) {
		this.dataLiq = dataLiq;
	}

	public String getEsercizio() {
		return esercizio;
	}

	public void setEsercizio(String esercizio) {
		this.esercizio = esercizio;
	}

	public String getCodRproc() {
		return codRproc;
	}

	public void setCodRproc(String codRproc) {
		this.codRproc = codRproc;
	}

	public String getDescRProc() {
		return descRProc;
	}

	public void setDescRProc(String descRProc) {
		this.descRProc = descRProc;
	}

	public String getNomeRProc() {
		return nomeRProc;
	}

	public void setNomeRProc(String nomeRProc) {
		this.nomeRProc = nomeRProc;
	}

	public String getNomeRProc2() {
		return nomeRProc2;
	}

	public void setNomeRProc2(String nomeRProc2) {
		this.nomeRProc2 = nomeRProc2;
	}

	public String getDirigente() {
		return dirigente;
	}

	public void setDirigente(String dirigente) {
		this.dirigente = dirigente;
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

	public String getDescLiquidazione() {
		return descLiquidazione;
	}

	public void setDescLiquidazione(String descLiquidazione) {
		this.descLiquidazione = descLiquidazione;
	}

	public String getTotaleLiq() {
		return totaleLiq;
	}

	public void setTotaleLiq(String totaleLiq) {
		this.totaleLiq = totaleLiq;
	}

	public String getTotaleImponibileLiq() {
		return totaleImponibileLiq;
	}

	public void setTotaleImponibileLiq(String totaleImponibileLiq) {
		this.totaleImponibileLiq = totaleImponibileLiq;
	}

	public String getTotaleRitenutaLiq() {
		return totaleRitenutaLiq;
	}

	public void setTotaleRitenutaLiq(String totaleRitenutaLiq) {
		this.totaleRitenutaLiq = totaleRitenutaLiq;
	}

	public String getNumeroElenco() {
		return numeroElenco;
	}

	public void setNumeroElenco(String numeroElenco) {
		this.numeroElenco = numeroElenco;
	}

	public String getNumeroRighe() {
		return numeroRighe;
	}

	public void setNumeroRighe(String numeroRighe) {
		this.numeroRighe = numeroRighe;
	}

	public List<RigaLiquidazioneDto> getListaCapitoli() {
		return listaCapitoli;
	}

	public void setListaCapitoli(List<RigaLiquidazioneDto> listaCapitoli) {
		this.listaCapitoli = listaCapitoli;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((annoAttoLiq == null) ? 0 : annoAttoLiq.hashCode());
		result = prime * result + ((annoLiq == null) ? 0 : annoLiq.hashCode());
		result = prime * result + ((codRproc == null) ? 0 : codRproc.hashCode());
		result = prime * result + ((dataAttoLiq == null) ? 0 : dataAttoLiq.hashCode());
		result = prime * result + ((dataEsecAttoLiq == null) ? 0 : dataEsecAttoLiq.hashCode());
		result = prime * result + ((dataLiq == null) ? 0 : dataLiq.hashCode());
		result = prime * result + ((descLiquidazione == null) ? 0 : descLiquidazione.hashCode());
		result = prime * result + ((descRProc == null) ? 0 : descRProc.hashCode());
		result = prime * result + ((dirigente == null) ? 0 : dirigente.hashCode());
		result = prime * result + ((esercizio == null) ? 0 : esercizio.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((idLiquidazione == null) ? 0 : idLiquidazione.hashCode());
		result = prime * result + ((listaCapitoli == null) ? 0 : listaCapitoli.hashCode());
		result = prime * result + ((nomeRProc == null) ? 0 : nomeRProc.hashCode());
		result = prime * result + ((nomeRProc2 == null) ? 0 : nomeRProc2.hashCode());
		result = prime * result + ((numeroAttoLiq == null) ? 0 : numeroAttoLiq.hashCode());
		result = prime * result + ((numeroElenco == null) ? 0 : numeroElenco.hashCode());
		result = prime * result + ((numeroLiq == null) ? 0 : numeroLiq.hashCode());
		result = prime * result + ((numeroRighe == null) ? 0 : numeroRighe.hashCode());
		result = prime * result + ((organoAttoLiq == null) ? 0 : organoAttoLiq.hashCode());
		result = prime * result + ((tipoAttoLiq == null) ? 0 : tipoAttoLiq.hashCode());
		result = prime * result + ((totaleImponibileLiq == null) ? 0 : totaleImponibileLiq.hashCode());
		result = prime * result + ((totaleLiq == null) ? 0 : totaleLiq.hashCode());
		result = prime * result + ((totaleRitenutaLiq == null) ? 0 : totaleRitenutaLiq.hashCode());
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
		DettaglioLiquidazioneDto other = (DettaglioLiquidazioneDto) obj;
		if (annoAttoLiq == null) {
			if (other.annoAttoLiq != null)
				return false;
		} else if (!annoAttoLiq.equals(other.annoAttoLiq))
			return false;
		if (annoLiq == null) {
			if (other.annoLiq != null)
				return false;
		} else if (!annoLiq.equals(other.annoLiq))
			return false;
		if (codRproc == null) {
			if (other.codRproc != null)
				return false;
		} else if (!codRproc.equals(other.codRproc))
			return false;
		if (dataAttoLiq == null) {
			if (other.dataAttoLiq != null)
				return false;
		} else if (!dataAttoLiq.equals(other.dataAttoLiq))
			return false;
		if (dataEsecAttoLiq == null) {
			if (other.dataEsecAttoLiq != null)
				return false;
		} else if (!dataEsecAttoLiq.equals(other.dataEsecAttoLiq))
			return false;
		if (dataLiq == null) {
			if (other.dataLiq != null)
				return false;
		} else if (!dataLiq.equals(other.dataLiq))
			return false;
		if (descLiquidazione == null) {
			if (other.descLiquidazione != null)
				return false;
		} else if (!descLiquidazione.equals(other.descLiquidazione))
			return false;
		if (descRProc == null) {
			if (other.descRProc != null)
				return false;
		} else if (!descRProc.equals(other.descRProc))
			return false;
		if (dirigente == null) {
			if (other.dirigente != null)
				return false;
		} else if (!dirigente.equals(other.dirigente))
			return false;
		if (esercizio == null) {
			if (other.esercizio != null)
				return false;
		} else if (!esercizio.equals(other.esercizio))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (idLiquidazione == null) {
			if (other.idLiquidazione != null)
				return false;
		} else if (!idLiquidazione.equals(other.idLiquidazione))
			return false;
		if (listaCapitoli == null) {
			if (other.listaCapitoli != null)
				return false;
		} else if (!listaCapitoli.equals(other.listaCapitoli))
			return false;
		if (nomeRProc == null) {
			if (other.nomeRProc != null)
				return false;
		} else if (!nomeRProc.equals(other.nomeRProc))
			return false;
		if (nomeRProc2 == null) {
			if (other.nomeRProc2 != null)
				return false;
		} else if (!nomeRProc2.equals(other.nomeRProc2))
			return false;
		if (numeroAttoLiq == null) {
			if (other.numeroAttoLiq != null)
				return false;
		} else if (!numeroAttoLiq.equals(other.numeroAttoLiq))
			return false;
		if (numeroElenco == null) {
			if (other.numeroElenco != null)
				return false;
		} else if (!numeroElenco.equals(other.numeroElenco))
			return false;
		if (numeroLiq == null) {
			if (other.numeroLiq != null)
				return false;
		} else if (!numeroLiq.equals(other.numeroLiq))
			return false;
		if (numeroRighe == null) {
			if (other.numeroRighe != null)
				return false;
		} else if (!numeroRighe.equals(other.numeroRighe))
			return false;
		if (organoAttoLiq == null) {
			if (other.organoAttoLiq != null)
				return false;
		} else if (!organoAttoLiq.equals(other.organoAttoLiq))
			return false;
		if (tipoAttoLiq == null) {
			if (other.tipoAttoLiq != null)
				return false;
		} else if (!tipoAttoLiq.equals(other.tipoAttoLiq))
			return false;
		if (totaleImponibileLiq == null) {
			if (other.totaleImponibileLiq != null)
				return false;
		} else if (!totaleImponibileLiq.equals(other.totaleImponibileLiq))
			return false;
		if (totaleLiq == null) {
			if (other.totaleLiq != null)
				return false;
		} else if (!totaleLiq.equals(other.totaleLiq))
			return false;
		if (totaleRitenutaLiq == null) {
			if (other.totaleRitenutaLiq != null)
				return false;
		} else if (!totaleRitenutaLiq.equals(other.totaleRitenutaLiq))
			return false;
		return true;
	}
	public int compareTo(DettaglioLiquidazioneDto o) {
		if(o.hashCode() == this.hashCode()) {
			return 0;
		}else if(o.hashCode() > this.hashCode()) {
			return 1;
		}else {
			return -1;
		}
	}
}
