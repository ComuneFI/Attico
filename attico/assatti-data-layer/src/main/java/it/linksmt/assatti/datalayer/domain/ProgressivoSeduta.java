package it.linksmt.assatti.datalayer.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "PROGRESSIVOSEDUTA",uniqueConstraints={@UniqueConstraint(columnNames={ "anno","progressivo","organo"} )})
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ProgressivoSeduta {

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "anno")
    private Integer anno;

    @Column(name = "progressivo")
    private Integer progressivo;
    
    @Column(name = "organo")
    private String organo;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getAnno() {
		return anno;
	}

	public void setAnno(Integer anno) {
		this.anno = anno;
	}

	public Integer getProgressivo() {
		return progressivo;
	}

	public void setProgressivo(Integer progressivo) {
		this.progressivo = progressivo;
	}

	public String getOrgano() {
		return organo;
	}

	public void setOrgano(String organo) {
		this.organo = organo;
	}

	
}
