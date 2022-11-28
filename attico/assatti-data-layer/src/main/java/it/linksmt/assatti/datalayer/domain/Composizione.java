package it.linksmt.assatti.datalayer.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import it.linksmt.assatti.datalayer.domain.util.CustomDateTimeDeserializer;
import it.linksmt.assatti.datalayer.domain.util.CustomDateTimeSerializer;
import it.linksmt.assatti.datalayer.domain.util.CustomLocalDateSerializer;
import it.linksmt.assatti.datalayer.domain.util.ISO8601LocalDateDeserializer;

/**
 * A Composizione.
 */
@Entity
@Table(name = "COMPOSIZIONE")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Composizione implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5925838285387032814L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "organo")
    private String organo;
    
    @Column(name = "version")
    private Integer version;
    
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @JsonSerialize(using = CustomDateTimeSerializer.class)
    @JsonDeserialize(using = CustomDateTimeDeserializer.class)
    @Column(name = "data_creazione")
    private DateTime dataCreazione;
    

    @Column(name = "descrizione")
    private String descrizione;

    @Column(name = "predefinita")
    private Boolean predefinita;
    
    @OneToMany(mappedBy = "composizione", cascade = { CascadeType.ALL })
    @OrderBy("ordine ASC")
	private List<ProfiloComposizione> hasProfiloComposizione = new ArrayList<ProfiloComposizione>();
    
    

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getOrgano() {
		return organo;
	}

	public void setOrgano(String organo) {
		this.organo = organo;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public DateTime getDataCreazione() {
		return dataCreazione;
	}

	public void setDataCreazione(DateTime dataCreazione) {
		this.dataCreazione = dataCreazione;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	public Boolean getPredefinita() {
		return predefinita;
	}

	public void setPredefinita(Boolean predefinita) {
		this.predefinita = predefinita;
	}

	public List<ProfiloComposizione> getHasProfiloComposizione() {
		return hasProfiloComposizione;
	}

	public void setHasProfiloComposizione(List<ProfiloComposizione> hasProfiloComposizione) {
		this.hasProfiloComposizione = hasProfiloComposizione;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Composizione composizione = (Composizione) o;

        if ( ! Objects.equals(id, composizione.id)) {
			return false;
		}

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }


	@Override
    public String toString() {
        return "Composizione{" +
                "id=" + id +
                ", organo='" + organo + "'" +
                ", descrizione='" + descrizione + "'" +
                ", predefinita='" + predefinita + "'" +
                ", versione='" + version + "'" +
                '}';
    }
}
