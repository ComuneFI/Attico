package it.linksmt.assatti.cooperation.dto.contabilita;

import java.io.Serializable;
import java.util.List;

public class MovimentoLiquidazioneDto implements Serializable, Comparable<MovimentoLiquidazioneDto> {

	private static final long serialVersionUID = 3512322531823548907L;

	private String anno;
	private String numero;
	private String data;
	private String oggetto;
	private String importo;
	private List<MovimentoDocumentoDto> documento;
	
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
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
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
	public List<MovimentoDocumentoDto> getDocumento() {
		return documento;
	}
	public void setDocumento(List<MovimentoDocumentoDto> documento) {
		this.documento = documento;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((anno == null) ? 0 : anno.hashCode());
		result = prime * result + ((data == null) ? 0 : data.hashCode());
		result = prime * result + ((documento == null) ? 0 : documento.hashCode());
		result = prime * result + ((importo == null) ? 0 : importo.hashCode());
		result = prime * result + ((numero == null) ? 0 : numero.hashCode());
		result = prime * result + ((oggetto == null) ? 0 : oggetto.hashCode());
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
		MovimentoLiquidazioneDto other = (MovimentoLiquidazioneDto) obj;
		if (anno == null) {
			if (other.anno != null)
				return false;
		} else if (!anno.equals(other.anno))
			return false;
		if (data == null) {
			if (other.data != null)
				return false;
		} else if (!data.equals(other.data))
			return false;
		if (documento == null) {
			if (other.documento != null)
				return false;
		} else if (!documento.equals(other.documento))
			return false;
		if (importo == null) {
			if (other.importo != null)
				return false;
		} else if (!importo.equals(other.importo))
			return false;
		if (numero == null) {
			if (other.numero != null)
				return false;
		} else if (!numero.equals(other.numero))
			return false;
		if (oggetto == null) {
			if (other.oggetto != null)
				return false;
		} else if (!oggetto.equals(other.oggetto))
			return false;
		return true;
	}
	public int compareTo(MovimentoLiquidazioneDto o) {
		if(o.hashCode() == this.hashCode()) {
			return 0;
		}else if(o.hashCode() > this.hashCode()) {
			return 1;
		}else {
			return -1;
		}
	}
}
