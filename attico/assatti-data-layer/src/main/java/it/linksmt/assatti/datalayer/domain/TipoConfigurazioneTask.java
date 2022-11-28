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

/**
 * A TipoConfigurazioneTask.
 */
@Entity
@Table(name = "tipo_configurazione_task")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class TipoConfigurazioneTask implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="id_tipo_configurazione_task", nullable=false)
    private Long idTipoConfigurazioneTask;
    
    @Column(name = "nome", nullable=false)
	private String nome;

    @Column(name = "descrizione")
    private String descrizione;
    
    /*
     * Get & Set
     */

	public Long getIdTipoConfigurazioneTask() {
		return idTipoConfigurazioneTask;
	}

	public void setIdTipoConfigurazioneTask(Long idTipoConfigurazioneTask) {
		this.idTipoConfigurazioneTask = idTipoConfigurazioneTask;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

}
