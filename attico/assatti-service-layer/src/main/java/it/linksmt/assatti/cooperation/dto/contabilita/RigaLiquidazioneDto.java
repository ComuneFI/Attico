package it.linksmt.assatti.cooperation.dto.contabilita;

import java.util.List;


public class RigaLiquidazioneDto implements Comparable<RigaLiquidazioneDto>{

	private String capitolo;
    private String articolo;
    private String descr;
    private String meccanografico;
    private String importoTotaleCapitolo;
    private String assestatoCapit;
    private List<LiquidazioneImpegnoDto> listaImpegni;
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
	public String getDescr() {
		return descr;
	}
	public void setDescr(String descr) {
		this.descr = descr;
	}
	public String getMeccanografico() {
		return meccanografico;
	}
	public void setMeccanografico(String meccanografico) {
		this.meccanografico = meccanografico;
	}
	public String getImportoTotaleCapitolo() {
		return importoTotaleCapitolo;
	}
	public void setImportoTotaleCapitolo(String importoTotaleCapitolo) {
		this.importoTotaleCapitolo = importoTotaleCapitolo;
	}
	public String getAssestatoCapit() {
		return assestatoCapit;
	}
	public void setAssestatoCapit(String assestatoCapit) {
		this.assestatoCapit = assestatoCapit;
	}
	public List<LiquidazioneImpegnoDto> getListaImpegni() {
		return listaImpegni;
	}
	public void setListaImpegni(List<LiquidazioneImpegnoDto> listaImpegni) {
		this.listaImpegni = listaImpegni;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((articolo == null) ? 0 : articolo.hashCode());
		result = prime * result + ((assestatoCapit == null) ? 0 : assestatoCapit.hashCode());
		result = prime * result + ((capitolo == null) ? 0 : capitolo.hashCode());
		result = prime * result + ((descr == null) ? 0 : descr.hashCode());
		result = prime * result + ((importoTotaleCapitolo == null) ? 0 : importoTotaleCapitolo.hashCode());
		result = prime * result + ((listaImpegni == null) ? 0 : listaImpegni.hashCode());
		result = prime * result + ((meccanografico == null) ? 0 : meccanografico.hashCode());
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
		RigaLiquidazioneDto other = (RigaLiquidazioneDto) obj;
		if (articolo == null) {
			if (other.articolo != null)
				return false;
		} else if (!articolo.equals(other.articolo))
			return false;
		if (assestatoCapit == null) {
			if (other.assestatoCapit != null)
				return false;
		} else if (!assestatoCapit.equals(other.assestatoCapit))
			return false;
		if (capitolo == null) {
			if (other.capitolo != null)
				return false;
		} else if (!capitolo.equals(other.capitolo))
			return false;
		if (descr == null) {
			if (other.descr != null)
				return false;
		} else if (!descr.equals(other.descr))
			return false;
		if (importoTotaleCapitolo == null) {
			if (other.importoTotaleCapitolo != null)
				return false;
		} else if (!importoTotaleCapitolo.equals(other.importoTotaleCapitolo))
			return false;
		if (listaImpegni == null) {
			if (other.listaImpegni != null)
				return false;
		} else if (!listaImpegni.equals(other.listaImpegni))
			return false;
		if (meccanografico == null) {
			if (other.meccanografico != null)
				return false;
		} else if (!meccanografico.equals(other.meccanografico))
			return false;
		return true;
	}
	public int compareTo(RigaLiquidazioneDto o) {
		if(o.hashCode() == this.hashCode()) {
			return 0;
		}else if(o.hashCode() > this.hashCode()) {
			return 1;
		}else {
			return -1;
		}
	}
}
