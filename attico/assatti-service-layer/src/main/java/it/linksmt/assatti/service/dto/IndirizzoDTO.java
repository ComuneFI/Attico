package it.linksmt.assatti.service.dto;

import java.util.Objects;


public class IndirizzoDTO {
	  private Long id;
	  private String dug;
	  private String toponimo;
	  private String civico;
	  private String cap;
	  private String comune;
	  private String provincia;
	  private Boolean attivo;
	  private String label;
	  
	  public Boolean getAttivo() {
			return attivo;
		}

		public void setAttivo(Boolean attivo) {
			this.attivo = attivo;
		}

		public Long getId() {
	        return id;
	    }

	    public void setId(Long id) {
	        this.id = id;
	    }

	    public String getDug() {
	        return dug;
	    }

	    public void setDug(String dug) {
	        this.dug = dug;
	    }

	    public String getToponimo() {
	        return toponimo;
	    }

	    public void setToponimo(String toponimo) {
	        this.toponimo = toponimo;
	    }

	    public String getCivico() {
	        return civico;
	    }

	    public void setCivico(String civico) {
	        this.civico = civico;
	    }

	    public String getCap() {
	        return cap;
	    }

	    public void setCap(String cap) {
	        this.cap = cap;
	    }

	    public String getComune() {
	        return comune;
	    }

	    public void setComune(String comune) {
	        this.comune = comune;
	    }

	    public String getProvincia() {
	        return provincia;
	    }

	    public void setProvincia(String provincia) {
	        this.provincia = provincia;
	    }

	    @Override
	    public int hashCode() {
	        return Objects.hashCode(id);
	    }

	    @Override
	    public String toString() {
	    	String toString = "";
//	    	if(id!=null){
//	    		toString += id + " - ";
//	    	}
	    	if(dug!=null && !"".equals(dug.trim())){
	    		toString += dug + " ";
	    	}
	    	if(toponimo!=null && !"".equals(toponimo.trim())){
	    		toString += toponimo + " ";
	    	}
	    	if(civico!=null && !"".equals(civico.trim())){
	    		toString += civico + " ";
	    	}
	    	if(cap!=null && !"".equals(cap.trim())){
	    		toString += cap + " ";
	    	}
	    	if(comune!=null && !"".equals(comune.trim())){
	    		toString += comune + " ";
	    	}
	    	if(provincia!=null && !"".equals(provincia.trim())){
	    		toString += "(" + provincia + ")";
	    	}
	    	return toString;
	    }

		public String getLabel() {
			return this.toString();
		}
}
