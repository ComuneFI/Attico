package it.linksmt.assatti.datalayer.domain.dto;

public class AooBasicDto{

    private Long id;

    private String codice;

    private String descrizione;
    
    public AooBasicDto() {
    	
    }
    
	public AooBasicDto(Long id, String codice, String descrizione) {
		super();
		this.id = id;
		this.codice = codice;
		this.descrizione = descrizione;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCodice() {
		return codice;
	}

	public void setCodice(String codice) {
		this.codice = codice;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

}
