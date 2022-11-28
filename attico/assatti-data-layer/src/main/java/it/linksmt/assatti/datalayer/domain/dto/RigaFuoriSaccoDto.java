package it.linksmt.assatti.datalayer.domain.dto;


public class RigaFuoriSaccoDto {
	private String testo;
	private int tipo; //1 Su relazione di //2 aoo //3 elenco
	public String getTesto() {
		return testo;
	}
	public void setTesto(String testo) {
		this.testo = testo;
	}
	public int getTipo() {
		return tipo;
	}
	public void setTipo(int tipo) {
		this.tipo = tipo;
	}
	
	
}
