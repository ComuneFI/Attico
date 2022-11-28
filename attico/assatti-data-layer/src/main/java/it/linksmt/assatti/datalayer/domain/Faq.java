package it.linksmt.assatti.datalayer.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Faq.
 */
@Entity
@Table(name = "FAQ")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Faq implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Size(max = 255)
    @Column(name = "domanda", length = 255)
    private String domanda;

    @Size(max = 2500)
    @Column(name = "risposta", length = 2500)
    private String risposta;

    @Column(name = "pubblica")
    private Boolean pubblica;

    @ManyToOne
    private CategoriaFaq categoria;

    public Long getId() {
        return id;
    }
    
    @ManyToMany
   	@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
   	@JoinTable(name = "DESTINATARI_FAQ", joinColumns = { @JoinColumn(name = "faq_id", nullable = false ) }, inverseJoinColumns = { @JoinColumn(name = "aoo_id", nullable = false ) } )
   	private Set<Aoo> aoo = new HashSet<Aoo>();

    public void setId(Long id) {
        this.id = id;
    }

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

    public Boolean getPubblica() {
        return pubblica;
    }

    public void setPubblica(Boolean pubblica) {
        this.pubblica = pubblica;
    }

    public CategoriaFaq getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaFaq categoriaFaq) {
        this.categoria = categoriaFaq;
    }

	public Set<Aoo> getAoo() {
		return aoo;
	}

	public void setAoo(Set<Aoo> aoo) {
		this.aoo = aoo;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Faq faq = (Faq) o;

        if ( ! Objects.equals(id, faq.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

	@Override
	public String toString() {
		return "Faq [id=" + id + ", domanda=" + domanda + ", risposta="
				+ risposta + ", pubblica=" + pubblica + ", categoria="
				+ categoria + "]";
	}
   
}
