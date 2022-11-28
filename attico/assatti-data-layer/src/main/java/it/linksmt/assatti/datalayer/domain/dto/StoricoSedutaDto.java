package it.linksmt.assatti.datalayer.domain.dto;

public class StoricoSedutaDto {
	private int id;
	private String dataRiunioneDB;
	private String dataRiunione;
	private String oraRiunione;
	private String numRiunione;
	private String tipoRiunione;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getDataRiunione() {
		return dataRiunione;
	}
	public void setDataRiunione(String dataRiunione) {
		this.dataRiunione = dataRiunione;
	}
	public String getOraRiunione() {
		return oraRiunione;
	}
	public void setOraRiunione(String oraRiunione) {
		this.oraRiunione = oraRiunione;
	}
	public String getNumRiunione() {
		return numRiunione;
	}
	public void setNumRiunione(String numRiunione) {
		this.numRiunione = numRiunione;
	}
	public String getTipoRiunione() {
		return tipoRiunione;
	}
	public void setTipoRiunione(String tipoRiunione) {
		this.tipoRiunione = tipoRiunione;
	}
	public String getDataRiunioneDB() {
		return dataRiunioneDB;
	}
	public void setDataRiunioneDB(String dataRiunioneDB) {
		this.dataRiunioneDB = dataRiunioneDB;
	}
	
	
}
