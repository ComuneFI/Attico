package it.linksmt.assatti.datalayer.domain;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A File.
 */
@Entity
@Table(name = "FILE")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class File extends AbstractAuditingEntity implements Serializable {

	private static final long serialVersionUID = 1L;

//	public File(String nomeFile, String contentType, Long size, byte[] contenuto) {
//		super();
//		this.nomeFile = nomeFile;
//		this.contentType = contentType;
//		this.size = size;
//		this.contenuto = contenuto;
//	}

	public File(String nomeFile, String contentType, Long size) {
		super();
		this.nomeFile = nomeFile;
		this.contentType = contentType;
		this.size = size;
	}

	public File() {
	}
	
	public File(Long id) {
		this.id=id;
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "nome_file")
	private String nomeFile;

	@Column(name = "content_type")
	private String contentType;

	@Column(name = "size")
	private Long size;
	
	@Column(name = "sha256_checksum")
	private String sha256Checksum;

	
//	@JsonIgnore
//	@Lob @Basic(fetch = FetchType.LAZY)
//	@Column(name = "contenuto",nullable=false,columnDefinition="longblob", insertable = true, updatable=false)
//	private byte[] contenuto;
	
	@Column(name = "cmisObjectId")
	private String cmisObjectId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNomeFile() {
		return nomeFile;
	}

	public void setNomeFile(String nomeFile) {
		this.nomeFile = nomeFile;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
	}
	
	

//	public byte[] getContenuto() {
//		return contenuto;
//	}
//
//	public void setContenuto(byte[] contenuto) {
//		this.contenuto = contenuto;
//	}

	public String getSha256Checksum() {
		return sha256Checksum;
	}

	public void setSha256Checksum(String sha256Checksum) {
		this.sha256Checksum = sha256Checksum;
	}

	public String getCmisObjectId() {
		return cmisObjectId;
	}

	public void setCmisObjectId(String cmisObjectId) {
		this.cmisObjectId = cmisObjectId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		File file = (File) o;

		if (!Objects.equals(id, file.id))
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

	@Override
	public String toString() {
		return "File{" + "id=" + id + ", nomeFile='" + nomeFile + "'"
				+ ", contentType='" + contentType + "'" + ", size='" + size
				+ "'" + '}';
	}

}
