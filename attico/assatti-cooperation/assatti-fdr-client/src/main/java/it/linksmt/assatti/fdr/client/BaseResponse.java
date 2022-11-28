
package it.linksmt.assatti.fdr.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per baseResponse complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="baseResponse"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="listaErrori" type="{http://service.ws.fdr.linksmt.it/}elencoErrori" minOccurs="0"/&gt;
 *         &lt;element name="esito" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "baseResponse", propOrder = {
    "listaErrori",
    "esito"
})
@XmlSeeAlso({
    ConfigurationResponse.class,
    FdrWsResponse.class
})
public class BaseResponse {

    protected ElencoErrori listaErrori;
    protected String esito;

    /**
     * Recupera il valore della proprietà listaErrori.
     * 
     * @return
     *     possible object is
     *     {@link ElencoErrori }
     *     
     */
    public ElencoErrori getListaErrori() {
        return listaErrori;
    }

    /**
     * Imposta il valore della proprietà listaErrori.
     * 
     * @param value
     *     allowed object is
     *     {@link ElencoErrori }
     *     
     */
    public void setListaErrori(ElencoErrori value) {
        this.listaErrori = value;
    }

    /**
     * Recupera il valore della proprietà esito.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEsito() {
        return esito;
    }

    /**
     * Imposta il valore della proprietà esito.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEsito(String value) {
        this.esito = value;
    }

}
