package it.linksmt.assatti.datalayer.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import it.linksmt.assatti.datalayer.domain.util.CustomDateTimeDeserializer;
import it.linksmt.assatti.datalayer.domain.util.CustomDateTimeSerializer;

@Entity
@Table(name = "insediamento_consiglio")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class InsediamentoConsiglio implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "descrizione")
    private String descrizione;
    
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @JsonSerialize(using = CustomDateTimeSerializer.class)
    @JsonDeserialize(using = CustomDateTimeDeserializer.class)
	@Column(name = "inizio_insediamento")
	private DateTime inizioInsediamento;
    
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @JsonSerialize(using = CustomDateTimeSerializer.class)
    @JsonDeserialize(using = CustomDateTimeDeserializer.class)
	@Column(name = "fine_insediamento")
	private DateTime fineInsediamento;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

	public DateTime getInizioInsediamento() {
		return inizioInsediamento;
	}

	public void setInizioInsediamento(DateTime inizioInsediamento) {
		this.inizioInsediamento = inizioInsediamento;
	}

	public DateTime getFineInsediamento() {
		return fineInsediamento;
	}

	public void setFineInsediamento(DateTime fineInsediamento) {
		this.fineInsediamento = fineInsediamento;
	}
}
