package it.linksmt.assatti.datalayer.domain;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * A StoricoAttoGiunta.
 */
@Entity
@Table(name = "STORICO_DOCUMENTO" )
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class StoricoDocumento implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public StoricoDocumento(final Long id) {
		this.id = id;
	}

	public StoricoDocumento() {
	}
	
	@Id
	@Column(name = "iddocumento")
	private Long id;

	@ManyToOne
	@JoinColumn(name="atto_giunta")
	private StoricoAttoGiunta attoGiunta;

	@Column(name = "nome", length = 800, insertable = true, updatable = false)
	private String nome;
	
	@Column(name = "codice_cifra", length = 18, insertable = true, updatable = false)
	private String codiceCifra;
	
	@Column(name = "cod_lav", length = 50, insertable = true, updatable = false)
	private String codiceLavorazione;
	
	@JsonIgnore
	@Lob @Basic(fetch = FetchType.LAZY)
	@Column(name="file", insertable = true, columnDefinition="longblob", updatable = false)
	private byte[] fileContent;
	
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public StoricoAttoGiunta getAttoGiunta() {
		return attoGiunta;
	}

	public void setAttoGiunta(StoricoAttoGiunta attoGiunta) {
		this.attoGiunta = attoGiunta;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCodiceCifra() {
		return codiceCifra;
	}

	public void setCodiceCifra(String codiceCifra) {
		this.codiceCifra = codiceCifra;
	}

	public String getCodiceLavorazione() {
		return codiceLavorazione;
	}

	public void setCodiceLavorazione(String codiceLavorazione) {
		this.codiceLavorazione = codiceLavorazione;
	}

	public byte[] getFileContent() {
		return fileContent;
	}

	public void setFileContent(byte[] fileContent) {
		this.fileContent = fileContent;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        
        StoricoDocumento sd = (StoricoDocumento) o;
        
        if ( ! Objects.equals(id, sd.id)) return false;

        return true;
	}
	
	@Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

}
