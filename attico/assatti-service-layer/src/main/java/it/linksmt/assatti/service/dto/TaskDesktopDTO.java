package it.linksmt.assatti.service.dto;

import it.linksmt.assatti.bpm.dto.TipoTaskDTO;
import it.linksmt.assatti.datalayer.domain.Atto;


public class TaskDesktopDTO implements Comparable<TaskDesktopDTO> {
	private TipoTaskDTO taskBpm;
	private Atto atto;
	private Boolean highLighted;
	private Boolean trasformazioneWarning;
	private Integer numArriviRagioneria;
	private String noteStepPrecedente;
	
	public TaskDesktopDTO() {
	}
		
	public TaskDesktopDTO(TipoTaskDTO taskBpm, Atto atto) {
		super();
		this.taskBpm = taskBpm;
		this.atto = atto;
	}

	public TipoTaskDTO getTaskBpm() {
		return taskBpm;
	}
	public void setTaskBpm(TipoTaskDTO taskBpm) {
		this.taskBpm = taskBpm;
	}
	public Atto getAtto() {
		return atto;
	}
	public void setAtto(Atto atto) {
		this.atto = atto;
	}
	
	public Boolean getHighLighted() {
		return highLighted;
	}

	public void setHighLighted(Boolean highLighted) {
		this.highLighted = highLighted;
	}
	
	public Integer getNumArriviRagioneria() {
		return numArriviRagioneria;
	}

	public void setNumArriviRagioneria(Integer numArriviRagioneria) {
		this.numArriviRagioneria = numArriviRagioneria;
	}

	public String getNoteStepPrecedente() {
		return noteStepPrecedente;
	}

	public void setNoteStepPrecedente(String noteStepPrecedente) {
		this.noteStepPrecedente = noteStepPrecedente;
	}

	@Override
	public int compareTo(TaskDesktopDTO s1) {
        return s1.getTaskBpm().getDataAvvioTask().toGregorianCalendar().getTime().compareTo( 
        		this.taskBpm.getDataAvvioTask().toGregorianCalendar().getTime());
    }

	public Boolean getTrasformazioneWarning() {
		return trasformazioneWarning;
	}

	public void setTrasformazioneWarning(Boolean trasformazioneWarning) {
		this.trasformazioneWarning = trasformazioneWarning;
	}
	 
}

