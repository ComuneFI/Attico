package it.linksmt.assatti.datalayer.domain.dto;

import org.joda.time.LocalDate;

public class AooBasicRicercaDto{

    private Long id;

    private String codice;

    private String descrizione;
    
    private LocalDate validoal;
    
    public AooBasicRicercaDto() {
    	
    }
    
	public AooBasicRicercaDto(Long id, String codice, String descrizione, LocalDate validoal) {
		super();
		this.id = id;
		this.codice = codice;
		this.descrizione = descrizione;
		this.validoal = validoal;
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

	public LocalDate getValidoal() {
		return validoal;
	}

	public void setValidoal(LocalDate validoal) {
		this.validoal = validoal;
	}
	
	

}
