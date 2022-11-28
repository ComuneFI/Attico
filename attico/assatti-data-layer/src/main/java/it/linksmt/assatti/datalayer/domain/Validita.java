package it.linksmt.assatti.datalayer.domain;

import it.linksmt.assatti.datalayer.domain.util.CustomLocalDateSerializer;
import it.linksmt.assatti.datalayer.domain.util.ISO8601LocalDateDeserializer;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Embeddable
public class Validita implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4955759895141306509L;

	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    @JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
    @Column(name = "validodal", insertable=true,updatable=false)
    private LocalDate validodal = new LocalDate();

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    @JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
    @Column(name = "validoal")
    private LocalDate validoal;

	public LocalDate getValidodal() {
		return validodal;
	}

	public void setValidodal(LocalDate validodal) {
		this.validodal = validodal;
	}

	public LocalDate getValidoal() {
		return validoal;
	}

	public void setValidoal(LocalDate validoal) {
		this.validoal = validoal;
	}


}
