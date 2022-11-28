package it.linksmt.assatti.datalayer.domain.dto;

import org.joda.time.DateTime;

public class AttivitaDTO {
	private String nome;
	private DateTime data;
	
	public String getNome() {
		return nome;
	}
	public DateTime getData() {
		return data;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public void setData(DateTime data) {
		this.data = data;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((data == null) ? 0 : data.hashCode());
		result = prime * result + ((nome == null) ? 0 : nome.hashCode());
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
		AttivitaDTO other = (AttivitaDTO) obj;
		if (data == null) {
			if (other.data != null)
				return false;
		} else if (!data.equals(other.data))
			return false;
		if (nome == null) {
			if (other.nome != null)
				return false;
		} else if (!nome.equals(other.nome))
			return false;
		return true;
	}
}
