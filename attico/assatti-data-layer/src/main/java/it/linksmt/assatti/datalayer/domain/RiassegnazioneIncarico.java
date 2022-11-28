package it.linksmt.assatti.datalayer.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.springframework.data.annotation.CreatedDate;

@Entity
@Table(name = "riassegnazione_incarico")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class RiassegnazioneIncarico  implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column(name = "task_id")
    private String taskId;
	
	@Column(name = "task_name")
    private String taskName;
	
	@CreatedDate
    @NotNull
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @Column(name = "created_date", nullable = false)
    private DateTime createdDate;
	
	@ManyToOne
	@JoinColumn(name="id_qualifica_assegnazione")
    private QualificaProfessionale qualificaAssegnazione;
	
	@ManyToOne
	@JoinColumn(name = "id_profilo_provenienza")
	private Profilo profiloProvenienza;
	
	@ManyToOne
	@JoinColumn(name = "id_profilo_assegnazione")
	private Profilo profiloAssegnazione;
	
	@ManyToOne
	@JoinColumn(name = "id_profilo_responsabile")
	private Profilo profiloResponsabile;
	
	@ManyToOne
	@JoinColumn(name = "id_configurazione_incarico")
	private ConfigurazioneIncarico configurazioneIncarico;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public DateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(DateTime createdDate) {
		this.createdDate = createdDate;
	}

	public QualificaProfessionale getQualificaAssegnazione() {
		return qualificaAssegnazione;
	}

	public void setQualificaAssegnazione(QualificaProfessionale qualificaAssegnazione) {
		this.qualificaAssegnazione = qualificaAssegnazione;
	}

	public Profilo getProfiloProvenienza() {
		return profiloProvenienza;
	}

	public void setProfiloProvenienza(Profilo profiloProvenienza) {
		this.profiloProvenienza = profiloProvenienza;
	}

	public Profilo getProfiloAssegnazione() {
		return profiloAssegnazione;
	}

	public void setProfiloAssegnazione(Profilo profiloAssegnazione) {
		this.profiloAssegnazione = profiloAssegnazione;
	}

	public Profilo getProfiloResponsabile() {
		return profiloResponsabile;
	}

	public void setProfiloResponsabile(Profilo profiloResponsabile) {
		this.profiloResponsabile = profiloResponsabile;
	}

	public ConfigurazioneIncarico getConfigurazioneIncarico() {
		return configurazioneIncarico;
	}

	public void setConfigurazioneIncarico(ConfigurazioneIncarico configurazioneIncarico) {
		this.configurazioneIncarico = configurazioneIncarico;
	}
}
