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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A Cat_obbligo_DL33.
 */
@Entity
@Table(name = "CAT_OBBLIGO_DL33")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Cat_obbligo_dl33 implements Serializable {
	public Cat_obbligo_dl33() {
	}

	public Cat_obbligo_dl33(Long id) {
		this.id = id;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column(name = "codice")
    private String codice;

	@Column(name = "descrizione")
	private String descrizione;

	@Column(name = "attiva")
	private Boolean attiva;

	@ManyToOne
	private Macro_cat_obbligo_dl33 fk_cat_obbligo_macro_cat_obbligo_idx;

	@OneToMany(mappedBy = "cat_obbligo_DL33")
	private Set<Obbligo_DL33> obblighi = new HashSet<Obbligo_DL33>();
	
	@Transient
	@JsonProperty
	private Boolean atti;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public String getCodice() {
		return codice;
	}

	public void setCodice(String codice) {
		this.codice = codice;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	public Boolean getAttiva() {
		return attiva;
	}

	public void setAttiva(Boolean attiva) {
		this.attiva = attiva;
	}

	public Macro_cat_obbligo_dl33 getFk_cat_obbligo_macro_cat_obbligo_idx() {
		return fk_cat_obbligo_macro_cat_obbligo_idx;
	}

	public void setFk_cat_obbligo_macro_cat_obbligo_idx(Macro_cat_obbligo_dl33 macro_cat_obbligo_dl33) {
		this.fk_cat_obbligo_macro_cat_obbligo_idx = macro_cat_obbligo_dl33;
	}

	public Set<Obbligo_DL33> getObblighi() {
		return obblighi;
	}

	public void setObblighi(Set<Obbligo_DL33> obblighi) {
		this.obblighi = obblighi;
	}

	public Boolean getAtti() {
		return atti;
	}

	public void setAtti(Boolean atti) {
		this.atti = atti;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		Cat_obbligo_dl33 cat_obbligo_DL33 = (Cat_obbligo_dl33) o;

		if (!Objects.equals(id, cat_obbligo_DL33.id))
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

	@Override
	public String toString() {
		return "Cat_obbligo_DL33{" + "id=" + id + ", codice='" + codice +"'" +  ", descrizione='" + descrizione + "'" + ", attiva='" + attiva + "'"
				+ '}';
	}
}
