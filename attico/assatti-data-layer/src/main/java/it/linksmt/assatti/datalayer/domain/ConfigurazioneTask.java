package it.linksmt.assatti.datalayer.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A ConfigurazioneTask.
 */
@Entity
@Table(name = "configurazione_task")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ConfigurazioneTask implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id_configurazione_task", nullable = false)
	private Long idConfigurazioneTask;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_tipo_configurazione_task")
	private TipoConfigurazioneTask tipoConfigurazioneTask;

	@Column(name = "process_var_name")
	private String processVarName;
	
	@Column(name = "nome", nullable = false)
	private String nome;

	@Column(name = "codice")
	private String codice;

	@Column(name = "obbligatoria")
	private boolean obbligatoria;

	@Column(name = "multipla")
	private boolean multipla;

	@Column(name = "proponente")
	private boolean proponente;
	
	@Column(name = "ufficio_corrente")
	private boolean ufficioCorrente;
	
	@Column(name = "data_creazione")
	private Date dataCreazione;

	@Column(name = "data_modifica")
	private Date dataModifica;
	
	@Column(name = "imposta_scadenza")
	private Boolean impostaScadenza;
	
	@Column(name = "scadenza_obbligatoria")
	private Boolean scadenzaObbligatoria;
	
	@Column(name = "data_manuale")
	private Boolean dataManuale;
	
	@Column(name = "uffici_livello_superiore")
	private Boolean ufficiLivelloSuperiore;
	
	@Column(name = "stessa_direzione_proponente")
	private boolean stessaDirezioneProponente;

	public Long getIdConfigurazioneTask() {
		return idConfigurazioneTask;
	}

	public void setIdConfigurazioneTask(Long idConfigurazioneTask) {
		this.idConfigurazioneTask = idConfigurazioneTask;
	}

	public TipoConfigurazioneTask getTipoConfigurazioneTask() {
		return tipoConfigurazioneTask;
	}

	public void setTipoConfigurazioneTask(TipoConfigurazioneTask tipoConfigurazioneTask) {
		this.tipoConfigurazioneTask = tipoConfigurazioneTask;
	}

	public String getProcessVarName() {
		return processVarName;
	}

	public void setProcessVarName(String processVarName) {
		this.processVarName = processVarName;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCodice() {
		return codice;
	}

	public void setCodice(String codice) {
		this.codice = codice;
	}

	public boolean isObbligatoria() {
		return obbligatoria;
	}

	public void setObbligatoria(boolean obbligatoria) {
		this.obbligatoria = obbligatoria;
	}

	public boolean isMultipla() {
		return multipla;
	}

	public void setMultipla(boolean multipla) {
		this.multipla = multipla;
	}

	public boolean isProponente() {
		return proponente;
	}

	public void setProponente(boolean proponente) {
		this.proponente = proponente;
	}

	public boolean isUfficioCorrente() {
		return ufficioCorrente;
	}

	public void setUfficioCorrente(boolean ufficioCorrente) {
		this.ufficioCorrente = ufficioCorrente;
	}

	public Date getDataCreazione() {
		return dataCreazione;
	}

	public void setDataCreazione(Date dataCreazione) {
		this.dataCreazione = dataCreazione;
	}

	public Date getDataModifica() {
		return dataModifica;
	}

	public void setDataModifica(Date dataModifica) {
		this.dataModifica = dataModifica;
	}

	public Boolean getImpostaScadenza() {
		return impostaScadenza;
	}

	public void setImpostaScadenza(Boolean impostaScadenza) {
		this.impostaScadenza = impostaScadenza;
	}

	public Boolean getScadenzaObbligatoria() {
		return scadenzaObbligatoria;
	}

	public void setScadenzaObbligatoria(Boolean scadenzaObbligatoria) {
		this.scadenzaObbligatoria = scadenzaObbligatoria;
	}

	public Boolean getDataManuale() {
		return dataManuale;
	}

	public void setDataManuale(Boolean dataManuale) {
		this.dataManuale = dataManuale;
	}

	public Boolean getUfficiLivelloSuperiore() {
		return ufficiLivelloSuperiore;
	}

	public void setUfficiLivelloSuperiore(Boolean ufficiLivelloSuperiore) {
		this.ufficiLivelloSuperiore = ufficiLivelloSuperiore;
	}

	public boolean isStessaDirezioneProponente() {
		return stessaDirezioneProponente;
	}

	public void setStessaDirezioneProponente(boolean stessaDirezioneProponente) {
		this.stessaDirezioneProponente = stessaDirezioneProponente;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codice == null) ? 0 : codice.hashCode());
		result = prime * result + ((dataCreazione == null) ? 0 : dataCreazione.hashCode());
		result = prime * result + ((dataManuale == null) ? 0 : dataManuale.hashCode());
		result = prime * result + ((dataModifica == null) ? 0 : dataModifica.hashCode());
		result = prime * result + ((idConfigurazioneTask == null) ? 0 : idConfigurazioneTask.hashCode());
		result = prime * result + ((impostaScadenza == null) ? 0 : impostaScadenza.hashCode());
		result = prime * result + (multipla ? 1231 : 1237);
		result = prime * result + ((nome == null) ? 0 : nome.hashCode());
		result = prime * result + (obbligatoria ? 1231 : 1237);
		result = prime * result + ((processVarName == null) ? 0 : processVarName.hashCode());
		result = prime * result + (proponente ? 1231 : 1237);
		result = prime * result + ((scadenzaObbligatoria == null) ? 0 : scadenzaObbligatoria.hashCode());
		result = prime * result + (stessaDirezioneProponente ? 1231 : 1237);
		result = prime * result + ((tipoConfigurazioneTask == null) ? 0 : tipoConfigurazioneTask.hashCode());
		result = prime * result + ((ufficiLivelloSuperiore == null) ? 0 : ufficiLivelloSuperiore.hashCode());
		result = prime * result + (ufficioCorrente ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ConfigurazioneTask other = (ConfigurazioneTask) obj;
		if (codice == null) {
			if (other.codice != null)
				return false;
		} else if (!codice.equals(other.codice))
			return false;
		if (dataCreazione == null) {
			if (other.dataCreazione != null)
				return false;
		} else if (!dataCreazione.equals(other.dataCreazione))
			return false;
		if (dataManuale == null) {
			if (other.dataManuale != null)
				return false;
		} else if (!dataManuale.equals(other.dataManuale))
			return false;
		if (dataModifica == null) {
			if (other.dataModifica != null)
				return false;
		} else if (!dataModifica.equals(other.dataModifica))
			return false;
		if (idConfigurazioneTask == null) {
			if (other.idConfigurazioneTask != null)
				return false;
		} else if (!idConfigurazioneTask.equals(other.idConfigurazioneTask))
			return false;
		if (impostaScadenza == null) {
			if (other.impostaScadenza != null)
				return false;
		} else if (!impostaScadenza.equals(other.impostaScadenza))
			return false;
		if (multipla != other.multipla)
			return false;
		if (nome == null) {
			if (other.nome != null)
				return false;
		} else if (!nome.equals(other.nome))
			return false;
		if (obbligatoria != other.obbligatoria)
			return false;
		if (processVarName == null) {
			if (other.processVarName != null)
				return false;
		} else if (!processVarName.equals(other.processVarName))
			return false;
		if (proponente != other.proponente)
			return false;
		if (scadenzaObbligatoria == null) {
			if (other.scadenzaObbligatoria != null)
				return false;
		} else if (!scadenzaObbligatoria.equals(other.scadenzaObbligatoria))
			return false;
		if (stessaDirezioneProponente != other.stessaDirezioneProponente)
			return false;
		if (tipoConfigurazioneTask == null) {
			if (other.tipoConfigurazioneTask != null)
				return false;
		} else if (!tipoConfigurazioneTask.equals(other.tipoConfigurazioneTask))
			return false;
		if (ufficiLivelloSuperiore == null) {
			if (other.ufficiLivelloSuperiore != null)
				return false;
		} else if (!ufficiLivelloSuperiore.equals(other.ufficiLivelloSuperiore))
			return false;
		if (ufficioCorrente != other.ufficioCorrente)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ConfigurazioneTask [idConfigurazioneTask=" + idConfigurazioneTask + ", tipoConfigurazioneTask="
				+ tipoConfigurazioneTask + ", processVarName=" + processVarName + ", nome=" + nome + ", codice="
				+ codice + ", obbligatoria=" + obbligatoria + ", multipla=" + multipla + ", proponente=" + proponente
				+ ", ufficioCorrente=" + ufficioCorrente + ", dataCreazione=" + dataCreazione + ", dataModifica="
				+ dataModifica + ", impostaScadenza=" + impostaScadenza + ", scadenzaObbligatoria="
				+ scadenzaObbligatoria + ", dataManuale=" + dataManuale + ", ufficiLivelloSuperiore="
				+ ufficiLivelloSuperiore + ", stessaDirezioneProponente=" + stessaDirezioneProponente + "]";
	}
}
