package it.linksmt.assatti.datalayer.domain;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * A RubricaDestinatarioEsterno.
 */
@Entity
@Table(name = "RUBRICADESTINATARIOESTERNO")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class RubricaDestinatarioEsterno implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Transient
    @JsonSerialize
    private String nameCerca;
   
    @Transient
    @JsonSerialize
    private String image;
    
    public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getNameCerca() {
		return nameCerca;
	}

	public void setNameCerca(String nameCerca) {
		this.nameCerca = nameCerca;
	}

	@Column(name = "denominazione")
    private String denominazione;

    @Column(name = "nome")
    private String nome;

    @Column(name = "cognome")
    private String cognome;

    @Column(name = "titolo")
    private String titolo;

    @Column(name = "email")
    private String email;

    @Column(name = "pec")
    private String pec;

    @Column(name = "tipo")
    private String tipo;
    
    @Column(name = "notifica_giunta_automatica")
    private Boolean notificaGiuntaAutomatica;

	@ManyToOne
    private Aoo aoo;

    @ManyToOne(cascade={CascadeType.PERSIST,CascadeType.MERGE,CascadeType.REMOVE})
    private Indirizzo indirizzo;

    @Embedded
    Validita validita = new Validita();
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDenominazione() {
        return denominazione;
    }

    public void setDenominazione(String denominazione) {
        this.denominazione = denominazione;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public String getTitolo() {
        return titolo;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPec() {
        return pec;
    }

    public void setPec(String pec) {
        this.pec = pec;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Aoo getAoo() {
        return aoo;
    }

    public void setAoo(Aoo aoo) {
        this.aoo = aoo;
    }

    public Indirizzo getIndirizzo() {
        return indirizzo;
    }

    public void setIndirizzo(Indirizzo indirizzo) {
        this.indirizzo = indirizzo;
    }
    
    public Validita getValidita() {
		return validita;
	}

	public void setValidita(Validita validita) {
		this.validita = validita;
	}
	
	public Boolean getNotificaGiuntaAutomatica() {
		return notificaGiuntaAutomatica;
	}

	public void setNotificaGiuntaAutomatica(Boolean notificaGiuntaAutomatica) {
		this.notificaGiuntaAutomatica = notificaGiuntaAutomatica;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        RubricaDestinatarioEsterno rubricaDestinatarioEsterno = (RubricaDestinatarioEsterno) o;

        if ( ! Objects.equals(id, rubricaDestinatarioEsterno.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "RubricaDestinatarioEsterno{" +
                "id=" + id +
                ", denominazione='" + denominazione + "'" +
                ", nome='" + nome + "'" +
                ", cognome='" + cognome + "'" +
                ", titolo='" + titolo + "'" +
                ", email='" + email + "'" +
                ", pec='" + pec + "'" +
                ", tipo='" + tipo + "'" +
                '}';
    }
}
