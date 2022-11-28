package it.linksmt.assatti.datalayer.domain.dto;

import java.util.Set;

import it.linksmt.assatti.datalayer.domain.Assessorato;
import it.linksmt.assatti.datalayer.domain.Indirizzo;
import it.linksmt.assatti.datalayer.domain.TipoAoo;
import it.linksmt.assatti.datalayer.domain.Validita;

public class AooDto{

    private Long id;

    private String codice;

    private String descrizione;

    private String telefono;

    private String fax;

    private String email;

    private String pec;

    private String identitavisiva;
    
	private Boolean uo;

    private Validita validita;
    
    private Long profiloResponsabileId;
    
    private ProfiloDto profiloResponsabile;
    
    private Set<Assessorato> hasAssessorati;

    private Indirizzo indirizzo;

    private TipoAoo tipoAoo;

    private String logo;
    
    private AooDto aooPadre;

    private Set<AooDto> sottoAoo;
   
	public String dataType;
   
	public String stato;
	
	public AooDto() {
		super();
	}

	public AooDto(Long id, String codice, String descrizione, String telefono, String fax, String email, String pec,
			String identitavisiva, Boolean uo, Validita validita, Long profiloResponsabileId,
			ProfiloDto profiloResponsabile, Set<Assessorato> hasAssessorati, TipoAoo tipoAoo, String logo,
			String dataType, String stato) {
		super();
		this.id = id;
		this.codice = codice;
		this.descrizione = descrizione;
		this.telefono = telefono;
		this.fax = fax;
		this.email = email;
		this.pec = pec;
		this.identitavisiva = identitavisiva;
		this.uo = uo;
		this.validita = validita;
		this.profiloResponsabileId = profiloResponsabileId;
		this.profiloResponsabile = profiloResponsabile;
		this.hasAssessorati = hasAssessorati;
		this.tipoAoo = tipoAoo;
		this.logo = logo;
		this.dataType = dataType;
		this.stato = stato;
	}

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

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPec() {
		return pec;
	}

	public void setPec(String pec) {
		this.pec = pec;
	}

	public String getIdentitavisiva() {
		return identitavisiva;
	}

	public void setIdentitavisiva(String identitavisiva) {
		this.identitavisiva = identitavisiva;
	}

	public Boolean getUo() {
		return uo;
	}

	public void setUo(Boolean uo) {
		this.uo = uo;
	}

	public Validita getValidita() {
		return validita;
	}

	public void setValidita(Validita validita) {
		this.validita = validita;
	}

	public Long getProfiloResponsabileId() {
		return profiloResponsabileId;
	}

	public void setProfiloResponsabileId(Long profiloResponsabileId) {
		this.profiloResponsabileId = profiloResponsabileId;
	}

	public ProfiloDto getProfiloResponsabile() {
		return profiloResponsabile;
	}

	public void setProfiloResponsabile(ProfiloDto profiloResponsabile) {
		this.profiloResponsabile = profiloResponsabile;
	}

	public Set<Assessorato> getHasAssessorati() {
		return hasAssessorati;
	}

	public void setHasAssessorati(Set<Assessorato> hasAssessorati) {
		this.hasAssessorati = hasAssessorati;
	}

	public Indirizzo getIndirizzo() {
		return indirizzo;
	}

	public void setIndirizzo(Indirizzo indirizzo) {
		this.indirizzo = indirizzo;
	}

	public TipoAoo getTipoAoo() {
		return tipoAoo;
	}

	public void setTipoAoo(TipoAoo tipoAoo) {
		this.tipoAoo = tipoAoo;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public AooDto getAooPadre() {
		return aooPadre;
	}

	public void setAooPadre(AooDto aooPadre) {
		this.aooPadre = aooPadre;
	}

	public Set<AooDto> getSottoAoo() {
		return sottoAoo;
	}

	public void setSottoAoo(Set<AooDto> sottoAoo) {
		this.sottoAoo = sottoAoo;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getStato() {
		return stato;
	}

	public void setStato(String stato) {
		this.stato = stato;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		AooDto other = (AooDto) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
}
