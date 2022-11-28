package it.linksmt.assatti.service.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Domain class for ConfigurazioneIncarico.
 */
public class ConfigurazioneIncaricoDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	
	private Integer tipologia;

	private Boolean enabled;

	private Boolean editor;
	
	private Date dataCreazione;

	private Date dataModifica;

	// @Transient
	// @JsonProperty
	// private Aoo aooNonProponente;

	private Long idAtto;

	private Long idConfigurazioneTask;
	
	private String configurazioneTaskNome;
	
	private String configurazioneTaskCodice;
	
	private List<ConfigurazioneIncaricoAooDto> listConfigurazioneIncaricoAooDto;
	
	private List<ConfigurazioneIncaricoProfiloDto> listConfigurazioneIncaricoProfiloDto;


	public ConfigurazioneIncaricoDto() {
		this.enabled = true;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		ConfigurazioneIncaricoDto configurazioneIncarico = (ConfigurazioneIncaricoDto) o;

		if (!Objects.equals(id, configurazioneIncarico.id))
			return false;

		return true;
	}

	@Override
	public String toString() {
		return "ConfigurazioneIncaricoDto{" + "id=" + id + ", editor='" + editor + "'" + '}';
	}

	/*
	 * Get & Set
	 */

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Boolean getEditor() {
		return editor;
	}

	public void setEditor(Boolean editor) {
		this.editor = editor;
	}

	public Integer getTipologia() {
		return tipologia;
	}

	public void setTipologia(Integer tipologia) {
		this.tipologia = tipologia;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public Long getIdConfigurazioneTask() {
		return idConfigurazioneTask;
	}

	public void setIdConfigurazioneTask(Long idConfigurazioneTask) {
		this.idConfigurazioneTask = idConfigurazioneTask;
	}

	public List<ConfigurazioneIncaricoAooDto> getListConfigurazioneIncaricoAooDto() {
		return listConfigurazioneIncaricoAooDto;
	}

	public void setListConfigurazioneIncaricoAooDto(List<ConfigurazioneIncaricoAooDto> listConfigurazioneIncaricoAooDto) {
		this.listConfigurazioneIncaricoAooDto = listConfigurazioneIncaricoAooDto;
	}

	public List<ConfigurazioneIncaricoProfiloDto> getListConfigurazioneIncaricoProfiloDto() {
		return listConfigurazioneIncaricoProfiloDto;
	}

	public void setListConfigurazioneIncaricoProfiloDto(
			List<ConfigurazioneIncaricoProfiloDto> listConfigurazioneIncaricoProfiloDto) {
		this.listConfigurazioneIncaricoProfiloDto = listConfigurazioneIncaricoProfiloDto;
	}

	public Long getIdAtto() {
		return idAtto;
	}

	public void setIdAtto(Long idAtto) {
		this.idAtto = idAtto;
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

	public String getConfigurazioneTaskNome() {
		return configurazioneTaskNome;
	}

	public void setConfigurazioneTaskNome(String configurazioneTaskNome) {
		this.configurazioneTaskNome = configurazioneTaskNome;
	}

	public String getConfigurazioneTaskCodice() {
		return configurazioneTaskCodice;
	}

	public void setConfigurazioneTaskCodice(String configurazioneTaskCodice) {
		this.configurazioneTaskCodice = configurazioneTaskCodice;
	}

}
