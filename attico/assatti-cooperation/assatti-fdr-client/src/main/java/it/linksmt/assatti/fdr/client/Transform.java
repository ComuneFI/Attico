
package it.linksmt.assatti.fdr.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per transform complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="transform"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="transform" type="{http://service.ws.fdr.linksmt.it/}xadesTransform" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "transform", propOrder = {
    "transform"
})
public class Transform {

    protected XadesTransform transform;

    /**
     * Recupera il valore della proprietà transform.
     * 
     * @return
     *     possible object is
     *     {@link XadesTransform }
     *     
     */
    public XadesTransform getTransform() {
        return transform;
    }

    /**
     * Imposta il valore della proprietà transform.
     * 
     * @param value
     *     allowed object is
     *     {@link XadesTransform }
     *     
     */
    public void setTransform(XadesTransform value) {
        this.transform = value;
    }

}
