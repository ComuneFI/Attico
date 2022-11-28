
package it.linksmt.assatti.fdr.client;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per xadesParam complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="xadesParam"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="canonicalizedType" type="{http://service.ws.fdr.linksmt.it/}xadesCanonicalizedType" minOccurs="0"/&gt;
 *         &lt;element name="transforms" type="{http://service.ws.fdr.linksmt.it/}transform" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="type" type="{http://service.ws.fdr.linksmt.it/}xadesType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "xadesParam", propOrder = {
    "canonicalizedType",
    "transforms",
    "type"
})
public class XadesParam {

    @XmlSchemaType(name = "string")
    protected XadesCanonicalizedType canonicalizedType;
    @XmlElement(nillable = true)
    protected List<Transform> transforms;
    @XmlSchemaType(name = "string")
    protected XadesType type;

    /**
     * Recupera il valore della proprietà canonicalizedType.
     * 
     * @return
     *     possible object is
     *     {@link XadesCanonicalizedType }
     *     
     */
    public XadesCanonicalizedType getCanonicalizedType() {
        return canonicalizedType;
    }

    /**
     * Imposta il valore della proprietà canonicalizedType.
     * 
     * @param value
     *     allowed object is
     *     {@link XadesCanonicalizedType }
     *     
     */
    public void setCanonicalizedType(XadesCanonicalizedType value) {
        this.canonicalizedType = value;
    }

    /**
     * Gets the value of the transforms property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the transforms property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTransforms().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Transform }
     * 
     * 
     */
    public List<Transform> getTransforms() {
        if (transforms == null) {
            transforms = new ArrayList<Transform>();
        }
        return this.transforms;
    }

    /**
     * Recupera il valore della proprietà type.
     * 
     * @return
     *     possible object is
     *     {@link XadesType }
     *     
     */
    public XadesType getType() {
        return type;
    }

    /**
     * Imposta il valore della proprietà type.
     * 
     * @param value
     *     allowed object is
     *     {@link XadesType }
     *     
     */
    public void setType(XadesType value) {
        this.type = value;
    }

}
