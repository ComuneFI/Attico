package it.linksmt.assatti.datalayer.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A Indirizzo.
 */
@Entity
@Table(name = "INDIRIZZO")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Indirizzo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "dug")
    private String dug;

    @Column(name = "toponimo")
    private String toponimo;

    @Column(name = "civico")
    private String civico;

    @Column(name = "cap")
    private String cap;

    @Column(name = "comune")
    private String comune;

    @Column(name = "provincia")
    private String provincia;

	@Column(name ="attivo")
    private Boolean attivo;
	
	@Transient
	@JsonProperty
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
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Indirizzo indirizzo = (Indirizzo) o;

        if ( ! Objects.equals(id, indirizzo.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "id=" + id +
                ", dug='" + dug + "'" +
                ", toponimo='" + toponimo + "'" +
                ", civico='" + civico + "'" +
                ", cap='" + cap + "'" +
                ", comune='" + comune + "'" +
                ", provincia='" + provincia + "'" +
                '}';
    }
    
    public String getLabel() {
    	String toString = "";
//    	if(id!=null){
//    		toString += id + " - ";
//    	}
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
}
