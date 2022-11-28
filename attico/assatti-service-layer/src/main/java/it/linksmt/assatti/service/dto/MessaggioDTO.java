package it.linksmt.assatti.service.dto;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class MessaggioDTO {

    private String testo;
    
    private String level;
    
    private Set<String> destinatari = new HashSet<String>();

    private String identifier;
    
    private List<Long> aoos;

    public List<Long> getAoos() {
		return aoos;
	}

	public void setAoos(List<Long> aoos) {
		this.aoos = aoos;
	}

	public String getTesto() {
        return testo;
    }

    public void setTesto(String testo) {
        this.testo = testo;
    }
    
	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	@Override
	public String toString() {
		return "MessaggioDTO [testo=" + testo + ", level=" + level
				+ ", destinatari=" + destinatari + "]";
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public Set<String> getDestinatari() {
		return destinatari;
	}

	public void setDestinatari(Set<String> destinatari) {
		this.destinatari = destinatari;
	}


    
}
