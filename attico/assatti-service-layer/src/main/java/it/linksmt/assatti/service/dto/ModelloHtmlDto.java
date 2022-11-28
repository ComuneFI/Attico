package it.linksmt.assatti.service.dto;

import java.io.Serializable;

/**
 * A ModelloHtml.
 */
public class ModelloHtmlDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long idModelloHtml;

    private String titolo;

    private String html;
    
    private Boolean pageOrientation;

//    private Long idTipoDocumento;
    
    /*
     * Get & Set
     */

	public String getTitolo() {
        return titolo;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

	public Boolean getPageOrientation() {
		return pageOrientation;
	}

	public void setPageOrientation(Boolean pageOrientation) {
		this.pageOrientation = pageOrientation;
	}

	public Long getIdModelloHtml() {
		return idModelloHtml;
	}

	public void setIdModelloHtml(Long idModelloHtml) {
		this.idModelloHtml = idModelloHtml;
	}

}
