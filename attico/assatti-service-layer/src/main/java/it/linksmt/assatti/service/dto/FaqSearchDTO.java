package it.linksmt.assatti.service.dto;

public class FaqSearchDTO {
	private String domanda;
	private String risposta;
	private Long categoria;
	private Long aoo;

	public String getDomanda() {
		return domanda;
	}

	public void setDomanda(String domanda) {
		this.domanda = domanda;
	}

	public String getRisposta() {
		return risposta;
	}

	public void setRisposta(String risposta) {
		this.risposta = risposta;
	}

	public Long getCategoria() {
		return categoria;
	}

	public void setCategoria(Long categoria) {
		this.categoria = categoria;
	}

	public Long getAoo() {
		return aoo;
	}

	public void setAoo(Long aoo) {
		this.aoo = aoo;
	}

	@Override
	public String toString() {
		return "FaqSearchDTO [domanda=" + domanda + ", risposta=" + risposta
				+ ", categoria=" + categoria + ", aoo=" + aoo + "]";
	}

}
