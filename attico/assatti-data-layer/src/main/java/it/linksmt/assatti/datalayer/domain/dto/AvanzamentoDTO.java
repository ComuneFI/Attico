package it.linksmt.assatti.datalayer.domain.dto;

import java.io.Serializable;
import java.util.List;

import org.joda.time.DateTime;

import it.linksmt.assatti.datalayer.domain.SistemaAccreditato;
import it.linksmt.assatti.datalayer.domain.dto.AttivitaDTO;

/**
 * Avanzamento DTO used to process pdf.
 */
public class AvanzamentoDTO implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
    private DateTime dataAttivita = new DateTime();
    private String note;
    private SistemaAccreditato sistemaAccreditato;
    private String stato;
    private String attivita;
    private List<AttivitaDTO> listaAttivita;
    private String createdBy;
    private String warningType;
    
	public Long getId() {
		return id;
	}
	public DateTime getDataAttivita() {
		return dataAttivita;
	}
	public String getNote() {
		return note;
	}
	public SistemaAccreditato getSistemaAccreditato() {
		return sistemaAccreditato;
	}
	public String getStato() {
		return stato;
	}
	public String getAttivita() {
		return attivita;
	}
	public List<AttivitaDTO> getListaAttivita() {
		return listaAttivita;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public void setDataAttivita(DateTime dataAttivita) {
		this.dataAttivita = dataAttivita;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public void setSistemaAccreditato(SistemaAccreditato sistemaAccreditato) {
		this.sistemaAccreditato = sistemaAccreditato;
	}
	public void setStato(String stato) {
		this.stato = stato;
	}
	public void setAttivita(String attivita) {
		this.attivita = attivita;
	}
	public void setListaAttivita(List<AttivitaDTO> listaAttivita) {
		this.listaAttivita = listaAttivita;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((attivita == null) ? 0 : attivita.hashCode());
		result = prime * result + ((dataAttivita == null) ? 0 : dataAttivita.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((listaAttivita == null) ? 0 : listaAttivita.hashCode());
		result = prime * result + ((note == null) ? 0 : note.hashCode());
		result = prime * result + ((sistemaAccreditato == null) ? 0 : sistemaAccreditato.hashCode());
		result = prime * result + ((stato == null) ? 0 : stato.hashCode());
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
		AvanzamentoDTO other = (AvanzamentoDTO) obj;
		if (attivita == null) {
			if (other.attivita != null)
				return false;
		} else if (!attivita.equals(other.attivita))
			return false;
		if (dataAttivita == null) {
			if (other.dataAttivita != null)
				return false;
		} else if (!dataAttivita.equals(other.dataAttivita))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (listaAttivita == null) {
			if (other.listaAttivita != null)
				return false;
		} else if (!listaAttivita.equals(other.listaAttivita))
			return false;
		if (note == null) {
			if (other.note != null)
				return false;
		} else if (!note.equals(other.note))
			return false;
		if (sistemaAccreditato == null) {
			if (other.sistemaAccreditato != null)
				return false;
		} else if (!sistemaAccreditato.equals(other.sistemaAccreditato))
			return false;
		if (stato == null) {
			if (other.stato != null)
				return false;
		} else if (!stato.equals(other.stato))
			return false;
		return true;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public String getWarningType() {
		return warningType;
	}
	public void setWarningType(String warningType) {
		this.warningType = warningType;
	}
	
}
