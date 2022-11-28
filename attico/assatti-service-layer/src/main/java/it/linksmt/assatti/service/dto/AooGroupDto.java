package it.linksmt.assatti.service.dto;

import it.linksmt.assatti.datalayer.domain.dto.AooBasicDto;

public class AooGroupDto extends AooBasicDto{
	public AooGroupDto(Long id, String codice, String descrizione) {
		super(id, codice, descrizione);
	}
	public AooGroupDto(Long id, String codice, String descrizione, Long count) {
		super(id, codice, descrizione);
		this.count = count;
	}
	public AooGroupDto() {}
	
	private Long count;
	
	private boolean disabilitata;
	
	public Long getCount() {
		return count;
	}
	public void setCount(Long count) {
		this.count = count;
	}
	public boolean isDisabilitata() {
		return disabilitata;
	}
	public void setDisabilitata(boolean disabilitata) {
		this.disabilitata = disabilitata;
	}

}
