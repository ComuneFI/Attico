package it.linksmt.assatti.cooperation.dto.contabilita;

import java.util.List;

public class LiquidazioneImpegnoDto implements Comparable<LiquidazioneImpegnoDto>{

	private String anno;
    private String numero;
    private String sub;
    private String impFormattato;
    private String descr;
    private String tipoProvImp;
    private String organoProvImp;
    private String annoProvImp;
    private String numeroProvImp;
    private String dataProvImp;
    private String dataEsecProvImp;
    private String importoTotaleImpegno;
    private String importoDisponibileImp;
    private String importoAssestatoImp;
    private String dataEsecAttoImpegno;
    private String codiceCUP;
    private String codiceCIG;
    private String codiceCGU;
    private String campoLibero1;
    private String campoLibero2;
    private String campoLibero3;
    private String fondo;
    private String tipoFinanziamento;
    private String pianoFinanziario;
    private String descPianoFinanziario;
    private String cdc;
    
    private List<SoggettoDto> listaSogg;

	public String getAnno() {
		return anno;
	}

	public void setAnno(String anno) {
		this.anno = anno;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getSub() {
		return sub;
	}

	public void setSub(String sub) {
		this.sub = sub;
	}

	public String getImpFormattato() {
		return impFormattato;
	}

	public void setImpFormattato(String impFormattato) {
		this.impFormattato = impFormattato;
	}

	public String getDescr() {
		return descr;
	}

	public void setDescr(String descr) {
		this.descr = descr;
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

	public String getDataProvImp() {
		return dataProvImp;
	}

	public void setDataProvImp(String dataProvImp) {
		this.dataProvImp = dataProvImp;
	}

	public String getDataEsecProvImp() {
		return dataEsecProvImp;
	}

	public void setDataEsecProvImp(String dataEsecProvImp) {
		this.dataEsecProvImp = dataEsecProvImp;
	}

	public String getImportoTotaleImpegno() {
		return importoTotaleImpegno;
	}

	public void setImportoTotaleImpegno(String importoTotaleImpegno) {
		this.importoTotaleImpegno = importoTotaleImpegno;
	}

	public String getImportoDisponibileImp() {
		return importoDisponibileImp;
	}

	public void setImportoDisponibileImp(String importoDisponibileImp) {
		this.importoDisponibileImp = importoDisponibileImp;
	}

	public String getImportoAssestatoImp() {
		return importoAssestatoImp;
	}

	public void setImportoAssestatoImp(String importoAssestatoImp) {
		this.importoAssestatoImp = importoAssestatoImp;
	}

	public String getDataEsecAttoImpegno() {
		return dataEsecAttoImpegno;
	}

	public void setDataEsecAttoImpegno(String dataEsecAttoImpegno) {
		this.dataEsecAttoImpegno = dataEsecAttoImpegno;
	}

	public String getCodiceCUP() {
		return codiceCUP;
	}

	public void setCodiceCUP(String codiceCUP) {
		this.codiceCUP = codiceCUP;
	}

	public String getCodiceCIG() {
		return codiceCIG;
	}

	public void setCodiceCIG(String codiceCIG) {
		this.codiceCIG = codiceCIG;
	}

	public String getCodiceCGU() {
		return codiceCGU;
	}

	public void setCodiceCGU(String codiceCGU) {
		this.codiceCGU = codiceCGU;
	}

	public String getCampoLibero1() {
		return campoLibero1;
	}

	public void setCampoLibero1(String campoLibero1) {
		this.campoLibero1 = campoLibero1;
	}

	public String getCampoLibero2() {
		return campoLibero2;
	}

	public void setCampoLibero2(String campoLibero2) {
		this.campoLibero2 = campoLibero2;
	}

	public String getCampoLibero3() {
		return campoLibero3;
	}

	public void setCampoLibero3(String campoLibero3) {
		this.campoLibero3 = campoLibero3;
	}

	public String getFondo() {
		return fondo;
	}

	public void setFondo(String fondo) {
		this.fondo = fondo;
	}

	public String getTipoFinanziamento() {
		return tipoFinanziamento;
	}

	public void setTipoFinanziamento(String tipoFinanziamento) {
		this.tipoFinanziamento = tipoFinanziamento;
	}

	public String getPianoFinanziario() {
		return pianoFinanziario;
	}

	public void setPianoFinanziario(String pianoFinanziario) {
		this.pianoFinanziario = pianoFinanziario;
	}

	public String getDescPianoFinanziario() {
		return descPianoFinanziario;
	}

	public void setDescPianoFinanziario(String descPianoFinanziario) {
		this.descPianoFinanziario = descPianoFinanziario;
	}

	public String getCdc() {
		return cdc;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((anno == null) ? 0 : anno.hashCode());
		result = prime * result + ((annoProvImp == null) ? 0 : annoProvImp.hashCode());
		result = prime * result + ((campoLibero1 == null) ? 0 : campoLibero1.hashCode());
		result = prime * result + ((campoLibero2 == null) ? 0 : campoLibero2.hashCode());
		result = prime * result + ((campoLibero3 == null) ? 0 : campoLibero3.hashCode());
		result = prime * result + ((cdc == null) ? 0 : cdc.hashCode());
		result = prime * result + ((codiceCGU == null) ? 0 : codiceCGU.hashCode());
		result = prime * result + ((codiceCIG == null) ? 0 : codiceCIG.hashCode());
		result = prime * result + ((codiceCUP == null) ? 0 : codiceCUP.hashCode());
		result = prime * result + ((dataEsecAttoImpegno == null) ? 0 : dataEsecAttoImpegno.hashCode());
		result = prime * result + ((dataEsecProvImp == null) ? 0 : dataEsecProvImp.hashCode());
		result = prime * result + ((dataProvImp == null) ? 0 : dataProvImp.hashCode());
		result = prime * result + ((descPianoFinanziario == null) ? 0 : descPianoFinanziario.hashCode());
		result = prime * result + ((descr == null) ? 0 : descr.hashCode());
		result = prime * result + ((fondo == null) ? 0 : fondo.hashCode());
		result = prime * result + ((impFormattato == null) ? 0 : impFormattato.hashCode());
		result = prime * result + ((importoAssestatoImp == null) ? 0 : importoAssestatoImp.hashCode());
		result = prime * result + ((importoDisponibileImp == null) ? 0 : importoDisponibileImp.hashCode());
		result = prime * result + ((importoTotaleImpegno == null) ? 0 : importoTotaleImpegno.hashCode());
		result = prime * result + ((listaSogg == null) ? 0 : listaSogg.hashCode());
		result = prime * result + ((numero == null) ? 0 : numero.hashCode());
		result = prime * result + ((numeroProvImp == null) ? 0 : numeroProvImp.hashCode());
		result = prime * result + ((organoProvImp == null) ? 0 : organoProvImp.hashCode());
		result = prime * result + ((pianoFinanziario == null) ? 0 : pianoFinanziario.hashCode());
		result = prime * result + ((sub == null) ? 0 : sub.hashCode());
		result = prime * result + ((tipoFinanziamento == null) ? 0 : tipoFinanziamento.hashCode());
		result = prime * result + ((tipoProvImp == null) ? 0 : tipoProvImp.hashCode());
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
		LiquidazioneImpegnoDto other = (LiquidazioneImpegnoDto) obj;
		if (anno == null) {
			if (other.anno != null)
				return false;
		} else if (!anno.equals(other.anno))
			return false;
		if (annoProvImp == null) {
			if (other.annoProvImp != null)
				return false;
		} else if (!annoProvImp.equals(other.annoProvImp))
			return false;
		if (campoLibero1 == null) {
			if (other.campoLibero1 != null)
				return false;
		} else if (!campoLibero1.equals(other.campoLibero1))
			return false;
		if (campoLibero2 == null) {
			if (other.campoLibero2 != null)
				return false;
		} else if (!campoLibero2.equals(other.campoLibero2))
			return false;
		if (campoLibero3 == null) {
			if (other.campoLibero3 != null)
				return false;
		} else if (!campoLibero3.equals(other.campoLibero3))
			return false;
		if (cdc == null) {
			if (other.cdc != null)
				return false;
		} else if (!cdc.equals(other.cdc))
			return false;
		if (codiceCGU == null) {
			if (other.codiceCGU != null)
				return false;
		} else if (!codiceCGU.equals(other.codiceCGU))
			return false;
		if (codiceCIG == null) {
			if (other.codiceCIG != null)
				return false;
		} else if (!codiceCIG.equals(other.codiceCIG))
			return false;
		if (codiceCUP == null) {
			if (other.codiceCUP != null)
				return false;
		} else if (!codiceCUP.equals(other.codiceCUP))
			return false;
		if (dataEsecAttoImpegno == null) {
			if (other.dataEsecAttoImpegno != null)
				return false;
		} else if (!dataEsecAttoImpegno.equals(other.dataEsecAttoImpegno))
			return false;
		if (dataEsecProvImp == null) {
			if (other.dataEsecProvImp != null)
				return false;
		} else if (!dataEsecProvImp.equals(other.dataEsecProvImp))
			return false;
		if (dataProvImp == null) {
			if (other.dataProvImp != null)
				return false;
		} else if (!dataProvImp.equals(other.dataProvImp))
			return false;
		if (descPianoFinanziario == null) {
			if (other.descPianoFinanziario != null)
				return false;
		} else if (!descPianoFinanziario.equals(other.descPianoFinanziario))
			return false;
		if (descr == null) {
			if (other.descr != null)
				return false;
		} else if (!descr.equals(other.descr))
			return false;
		if (fondo == null) {
			if (other.fondo != null)
				return false;
		} else if (!fondo.equals(other.fondo))
			return false;
		if (impFormattato == null) {
			if (other.impFormattato != null)
				return false;
		} else if (!impFormattato.equals(other.impFormattato))
			return false;
		if (importoAssestatoImp == null) {
			if (other.importoAssestatoImp != null)
				return false;
		} else if (!importoAssestatoImp.equals(other.importoAssestatoImp))
			return false;
		if (importoDisponibileImp == null) {
			if (other.importoDisponibileImp != null)
				return false;
		} else if (!importoDisponibileImp.equals(other.importoDisponibileImp))
			return false;
		if (importoTotaleImpegno == null) {
			if (other.importoTotaleImpegno != null)
				return false;
		} else if (!importoTotaleImpegno.equals(other.importoTotaleImpegno))
			return false;
		if (listaSogg == null) {
			if (other.listaSogg != null)
				return false;
		} else if (!listaSogg.equals(other.listaSogg))
			return false;
		if (numero == null) {
			if (other.numero != null)
				return false;
		} else if (!numero.equals(other.numero))
			return false;
		if (numeroProvImp == null) {
			if (other.numeroProvImp != null)
				return false;
		} else if (!numeroProvImp.equals(other.numeroProvImp))
			return false;
		if (organoProvImp == null) {
			if (other.organoProvImp != null)
				return false;
		} else if (!organoProvImp.equals(other.organoProvImp))
			return false;
		if (pianoFinanziario == null) {
			if (other.pianoFinanziario != null)
				return false;
		} else if (!pianoFinanziario.equals(other.pianoFinanziario))
			return false;
		if (sub == null) {
			if (other.sub != null)
				return false;
		} else if (!sub.equals(other.sub))
			return false;
		if (tipoFinanziamento == null) {
			if (other.tipoFinanziamento != null)
				return false;
		} else if (!tipoFinanziamento.equals(other.tipoFinanziamento))
			return false;
		if (tipoProvImp == null) {
			if (other.tipoProvImp != null)
				return false;
		} else if (!tipoProvImp.equals(other.tipoProvImp))
			return false;
		return true;
	}

	public void setCdc(String cdc) {
		this.cdc = cdc;
	}

	public List<SoggettoDto> getListaSogg() {
		return listaSogg;
	}

	public void setListaSogg(List<SoggettoDto> listaSogg) {
		this.listaSogg = listaSogg;
	}
	public int compareTo(LiquidazioneImpegnoDto o) {
		if(o.hashCode() == this.hashCode()) {
			return 0;
		}else if(o.hashCode() > this.hashCode()) {
			return 1;
		}else {
			return -1;
		}
	}
    
}
