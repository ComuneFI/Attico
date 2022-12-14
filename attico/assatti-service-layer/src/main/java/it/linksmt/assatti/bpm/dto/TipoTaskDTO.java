package it.linksmt.assatti.bpm.dto;

import javax.xml.datatype.XMLGregorianCalendar;


public class TipoTaskDTO {
	
	private String id;
    private String businessKey;
    
    private String codiceCifra;
    private String numeroAdozione;
	private String oggetto;
	private String tipoAtto;
	private String startedBy;
	private String startedByName;
	private String assegnatarioName;
	private String esecutoreName;
	private String aooId;
	
    private XMLGregorianCalendar dataAvvioProcesso;
    private XMLGregorianCalendar dataAvvioTask;
    
    private XMLGregorianCalendar dataAttivita;
    
    private String nomeVisualizzato;
    private String ultimaAzione;
    private XMLGregorianCalendar dataUltimaAzione;
    private XMLGregorianCalendar dataAdozione;
    private XMLGregorianCalendar dataEsecutivita;
    private String esecutore;
    
    private String azioneDaEffettuare;
    // private String pulsanteCompletamentoTask;
    
    private String idAssegnatario;
    private String idDelegante;
    private String idProfOriginario;
    private Boolean isTaskInDelegaSingolaLavorazione;
    private String candidateGroups;
    
    private String ufficioGiacenza;
    
    private FirmaMassivaDTO firmaDto;
    
    private XMLGregorianCalendar dataUltimoAggiornamento;
    
    /*
     * TODO: sembrano non utilizzate
     */
    // protected long idAttore;
    // protected String ruoloTask;
    
    // protected XMLGregorianCalendar dataAssegnata;
    // protected XMLGregorianCalendar dataFineAttesa;
    
    // protected TipoTaskPriorita priorita;
    // protected String descrizione;
    // protected String descrizioneVisualizzata;
    
    // protected String nome;
    // protected String idEsecutore;
    
    // protected String idIstanzaProcesso;
	// protected String idDefinizioneProcesso;
	// protected String nomeDefinizioneProcesso;
	// protected String versioneDefinizioneProcesso;
    // protected String idDefinizioneSottoProcesso;  
    // protected String idIstanzaSottoProcesso;
    
    // protected XMLGregorianCalendar dataAvvio;
    // protected XMLGregorianCalendar dataArchivio;
    // protected Boolean terminale;
    
    // protected String stato;
    // protected XMLGregorianCalendar dataAssegnazione;
    

    /**
     * Recupera il valore della propriet?? candidateGroups.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCandidateGroups() {
        return candidateGroups;
    }

    /**
     * Imposta il valore della propriet?? candidateGroups.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCandidateGroups(String value) {
        this.candidateGroups = value;
    }    
    
    public String getUfficioGiacenza() {
		return ufficioGiacenza;
	}

	public void setUfficioGiacenza(String ufficioGiacenza) {
		this.ufficioGiacenza = ufficioGiacenza;
	}

	/**
     * Recupera il valore della propriet?? startedBy.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStartedBy() {
        return startedBy;
    }

    /**
     * Imposta il valore della propriet?? startedBy.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStartedBy(String value) {
        this.startedBy = value;
    }

    /**
     * Recupera il valore della propriet?? codiceCifra.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodiceCifra() {
        return codiceCifra;
    }

    /**
     * Imposta il valore della propriet?? codiceCifra.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodiceCifra(String value) {
        this.codiceCifra = value;
    }

    /**
     * Recupera il valore della propriet?? oggetto.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOggetto() {
        return oggetto;
    }

    /**
     * Imposta il valore della propriet?? oggetto.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOggetto(String value) {
        this.oggetto = value;
    }

    /**
     * Recupera il valore della propriet?? tipoAtto.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTipoAtto() {
        return tipoAtto;
    }

    /**
     * Imposta il valore della propriet?? tipoAtto.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTipoAtto(String value) {
        this.tipoAtto = value;
    }

    /**
     * Recupera il valore della propriet?? idAssegnatario.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdAssegnatario() {
        return idAssegnatario;
    }

    /**
     * Imposta il valore della propriet?? idAssegnatario.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdAssegnatario(String value) {
        this.idAssegnatario = value;
    }
    
    

    /**
     * @return
     */
    public String getIdDelegante() {
		return idDelegante;
	}

	/**
	 * @param idDelegante
	 */
	public void setIdDelegante(String idDelegante) {
		this.idDelegante = idDelegante;
	}

	/**
     * Recupera il valore della propriet?? dataUltimaAzione.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDataUltimaAzione() {
        return dataUltimaAzione;
    }

    /**
     * Imposta il valore della propriet?? dataUltimaAzione.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDataUltimaAzione(XMLGregorianCalendar value) {
        this.dataUltimaAzione = value;
    }

    /**
     * Recupera il valore della propriet?? esecutore.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEsecutore() {
        return esecutore;
    }

    /**
     * Imposta il valore della propriet?? esecutore.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEsecutore(String value) {
        this.esecutore = value;
    }

    /**
     * Recupera il valore della propriet?? ultimaAzione.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUltimaAzione() {
        return ultimaAzione;
    }

    /**
     * Imposta il valore della propriet?? ultimaAzione.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUltimaAzione(String value) {
        this.ultimaAzione = value;
    }

    /**
     * Recupera il valore della propriet?? nomeVisualizzato.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNomeVisualizzato() {
        return nomeVisualizzato;
    }

    /**
     * Imposta il valore della propriet?? nomeVisualizzato.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNomeVisualizzato(String value) {
        this.nomeVisualizzato = value;
    }
    
    


    public String getAzioneDaEffettuare() {
		return azioneDaEffettuare;
	}

	public void setAzioneDaEffettuare(String azioneDaEffettuare) {
		this.azioneDaEffettuare = azioneDaEffettuare;
	}

	/**
     * Recupera il valore della propriet?? id.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Imposta il valore della propriet?? id.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Recupera il valore della propriet?? businessKey.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBusinessKey() {
        return businessKey;
    }

    /**
     * Imposta il valore della propriet?? businessKey.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBusinessKey(String value) {
        this.businessKey = value;
    }

    /**
     * Recupera il valore della propriet?? dataUltimoAggiornamento.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDataUltimoAggiornamento() {
        return dataUltimoAggiornamento;
    }

    /**
     * Imposta il valore della propriet?? dataUltimoAggiornamento.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDataUltimoAggiornamento(XMLGregorianCalendar value) {
        this.dataUltimoAggiornamento = value;
    }

    /**
     * Recupera il valore della propriet?? dataAvvioProcesso.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDataAvvioProcesso() {
        return dataAvvioProcesso;
    }

    /**
     * Imposta il valore della propriet?? dataAvvioProcesso.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDataAvvioProcesso(XMLGregorianCalendar value) {
        this.dataAvvioProcesso = value;
    }
    
    /**
     * Recupera il valore della propriet?? dataAvvio.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDataAvvioTask() {
        return dataAvvioTask;
    }

    /**
     * Imposta il valore della propriet?? dataAvvio.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDataAvvioTask(XMLGregorianCalendar value) {
        this.dataAvvioTask = value;
    }

	public XMLGregorianCalendar getDataAttivita() {
		return dataAttivita;
	}

	public void setDataAttivita(XMLGregorianCalendar dataAttivita) {
		this.dataAttivita = dataAttivita;
	}

	public Boolean getIsTaskInDelegaSingolaLavorazione() {
		return isTaskInDelegaSingolaLavorazione;
	}

	public void setIsTaskInDelegaSingolaLavorazione(Boolean isTaskInDelegaSingolaLavorazione) {
		this.isTaskInDelegaSingolaLavorazione = isTaskInDelegaSingolaLavorazione;
	}

	public FirmaMassivaDTO getFirmaDto() {
		return firmaDto;
	}

	public void setFirmaDto(FirmaMassivaDTO firmaDto) {
		this.firmaDto = firmaDto;
	}

	public String getStartedByName() {
		return startedByName;
	}

	public void setStartedByName(String startedByName) {
		this.startedByName = startedByName;
	}

	public String getAssegnatarioName() {
		return assegnatarioName;
	}

	public void setAssegnatarioName(String assegnatarioName) {
		this.assegnatarioName = assegnatarioName;
	}

	public String getEsecutoreName() {
		return esecutoreName;
	}

	public void setEsecutoreName(String esecutoreName) {
		this.esecutoreName = esecutoreName;
	}

	public String getAooId() {
		return aooId;
	}

	public void setAooId(String aooId) {
		this.aooId = aooId;
	}

	public String getIdProfOriginario() {
		return idProfOriginario;
	}

	public void setIdProfOriginario(String idProfOriginario) {
		this.idProfOriginario = idProfOriginario;
	}

	public String getNumeroAdozione() {
		return numeroAdozione;
	}

	public void setNumeroAdozione(String numeroAdozione) {
		this.numeroAdozione = numeroAdozione;
	}

	public XMLGregorianCalendar getDataAdozione() {
		return dataAdozione;
	}

	public void setDataAdozione(XMLGregorianCalendar dataAdozione) {
		this.dataAdozione = dataAdozione;
	}

	public XMLGregorianCalendar getDataEsecutivita() {
		return dataEsecutivita;
	}

	public void setDataEsecutivita(XMLGregorianCalendar dataEsecutivita) {
		this.dataEsecutivita = dataEsecutivita;
	}
	
}
