package it.linksmt.assatti.service.dto;

public class CategoriaFaqSearchDTO {
	private Long id;
	private String descrizione;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	@Override
	public String toString() {
		return "CategoriaFaqSearchDTO [id=" + id + ", descrizione="
				+ descrizione + "]";
	}

}
