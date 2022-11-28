package it.linksmt.assatti.datalayer.domain;

import java.io.Serializable;

import javax.persistence.CascadeType;
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
 * Ricevuta Pec.
 */
@Entity
@Table(name = "RICEVUTAPEC")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class RicevutaPec implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @ManyToOne(fetch=FetchType.LAZY, optional=false, cascade = { CascadeType.ALL })
	@JoinColumn(name="documento_id", insertable=true, updatable = false)
    private DocumentoPdf documento;

    @Column(name = "messageid")
    private String messageId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public DocumentoPdf getDocumento() {
		return documento;
	}

	public void setDocumento(DocumentoPdf documento) {
		this.documento = documento;
	}

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}
    
}
