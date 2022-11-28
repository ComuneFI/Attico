package it.linksmt.assatti.datalayer.domain.dto;

import java.util.List;

public class RilevazioneAssenzaDto {

	private String progressivoDiInizio;
	private String progressivoDiFine;
	private List<RigaAssentiDto> righeAssenti;
	public String getProgressivoDiInizio() {
		return progressivoDiInizio;
	}
	public void setProgressivoDiInizio(String progressivoDiInizio) {
		this.progressivoDiInizio = progressivoDiInizio;
	}
	public String getProgressivoDiFine() {
		return progressivoDiFine;
	}
	public void setProgressivoDiFine(String progressivoDiFine) {
		this.progressivoDiFine = progressivoDiFine;
	}
	public List<RigaAssentiDto> getRigheAssenti() {
		return righeAssenti;
	}
	public void setRigheAssenti(List<RigaAssentiDto> righeAssenti) {
		this.righeAssenti = righeAssenti;
	}
	
	
}
