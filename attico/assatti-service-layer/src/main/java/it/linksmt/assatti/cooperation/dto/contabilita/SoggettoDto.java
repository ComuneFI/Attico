package it.linksmt.assatti.cooperation.dto.contabilita;

import java.util.List;

public class SoggettoDto implements Comparable<SoggettoDto>{

	private String soggetto;
    private String descr;
    private String codiceFisc;
    private String pIva;
    private String modoPaga;
    private String importoTotSogg;
    private String notePagamento;
    private List<DocumentoDto> listaDocumenti;
	public String getSoggetto() {
		return soggetto;
	}
	public void setSoggetto(String soggetto) {
		this.soggetto = soggetto;
	}
	public String getDescr() {
		return descr;
	}
	public void setDescr(String descr) {
		this.descr = descr;
	}
	public String getCodiceFisc() {
		return codiceFisc;
	}
	public void setCodiceFisc(String codiceFisc) {
		this.codiceFisc = codiceFisc;
	}
	public String getpIva() {
		return pIva;
	}
	public void setpIva(String pIva) {
		this.pIva = pIva;
	}
	public String getModoPaga() {
		return modoPaga;
	}
	public void setModoPaga(String modoPaga) {
		this.modoPaga = modoPaga;
	}
	public String getImportoTotSogg() {
		return importoTotSogg;
	}
	public void setImportoTotSogg(String importoTotSogg) {
		this.importoTotSogg = importoTotSogg;
	}
	public String getNotePagamento() {
		return notePagamento;
	}
	public void setNotePagamento(String notePagamento) {
		this.notePagamento = notePagamento;
	}
	public List<DocumentoDto> getListaDocumenti() {
		return listaDocumenti;
	}
	public void setListaDocumenti(List<DocumentoDto> listaDocumenti) {
		this.listaDocumenti = listaDocumenti;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codiceFisc == null) ? 0 : codiceFisc.hashCode());
		result = prime * result + ((descr == null) ? 0 : descr.hashCode());
		result = prime * result + ((importoTotSogg == null) ? 0 : importoTotSogg.hashCode());
		result = prime * result + ((listaDocumenti == null) ? 0 : listaDocumenti.hashCode());
		result = prime * result + ((modoPaga == null) ? 0 : modoPaga.hashCode());
		result = prime * result + ((notePagamento == null) ? 0 : notePagamento.hashCode());
		result = prime * result + ((pIva == null) ? 0 : pIva.hashCode());
		result = prime * result + ((soggetto == null) ? 0 : soggetto.hashCode());
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
		SoggettoDto other = (SoggettoDto) obj;
		if (codiceFisc == null) {
			if (other.codiceFisc != null)
				return false;
		} else if (!codiceFisc.equals(other.codiceFisc))
			return false;
		if (descr == null) {
			if (other.descr != null)
				return false;
		} else if (!descr.equals(other.descr))
			return false;
		if (importoTotSogg == null) {
			if (other.importoTotSogg != null)
				return false;
		} else if (!importoTotSogg.equals(other.importoTotSogg))
			return false;
		if (listaDocumenti == null) {
			if (other.listaDocumenti != null)
				return false;
		} else if (!listaDocumenti.equals(other.listaDocumenti))
			return false;
		if (modoPaga == null) {
			if (other.modoPaga != null)
				return false;
		} else if (!modoPaga.equals(other.modoPaga))
			return false;
		if (notePagamento == null) {
			if (other.notePagamento != null)
				return false;
		} else if (!notePagamento.equals(other.notePagamento))
			return false;
		if (pIva == null) {
			if (other.pIva != null)
				return false;
		} else if (!pIva.equals(other.pIva))
			return false;
		if (soggetto == null) {
			if (other.soggetto != null)
				return false;
		} else if (!soggetto.equals(other.soggetto))
			return false;
		return true;
	}
    
	public int compareTo(SoggettoDto o) {
		if(o.hashCode() == this.hashCode()) {
			return 0;
		}else if(o.hashCode() > this.hashCode()) {
			return 1;
		}else {
			return -1;
		}
	}
}
