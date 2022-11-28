package it.linksmt.assatti.service.dto;

import java.io.Serializable;
import java.util.Objects;


/**
 * Domain class: Sezione.
 */
public class SezioneDto implements Serializable {
	
	private static final long serialVersionUID = 1L;

    private Long id;

    private String descrizione;
    
    private String codice;
    
    private Long idTipoAtto;
    
    private boolean visibile;

	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SezioneDto sezione = (SezioneDto) o;

        if ( ! Objects.equals(id, sezione.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Sezione{" +
                "id=" + id +
                ", denominazione='" + descrizione + "'" +
                ", codice='" + codice + "'" +
                '}';
    }

    /*
     * Get & Set
     */
    
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

	public String getCodice() {
		return codice;
	}

	public void setCodice(String codice) {
		this.codice = codice;
	}

	public Long getIdTipoAtto() {
		return idTipoAtto;
	}

	public void setIdTipoAtto(Long idTipoAtto) {
		this.idTipoAtto = idTipoAtto;
	}

	public boolean getVisibile() {
		return visibile;
	}

	public void setVisibile(boolean visibile) {
		this.visibile = visibile;
	}

}
