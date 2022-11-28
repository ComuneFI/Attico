
package it.linksmt.assatti.fdr.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per padesParam complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="padesParam"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="sessionId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="flagVisibile" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="location" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="reason" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="page" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="leftx" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="lefty" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="rightx" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="righty" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="image" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0"/&gt;
 *         &lt;element name="testo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="padesProfile" type="{http://service.ws.fdr.linksmt.it/}padesProfile" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "padesParam", propOrder = {
    "sessionId",
    "flagVisibile",
    "location",
    "reason",
    "page",
    "leftx",
    "lefty",
    "rightx",
    "righty",
    "image",
    "testo",
    "padesProfile"
})
public class PadesParam {

    protected String sessionId;
    protected boolean flagVisibile;
    protected String location;
    protected String reason;
    protected int page;
    protected int leftx;
    protected int lefty;
    protected int rightx;
    protected int righty;
    protected byte[] image;
    protected String testo;
    @XmlSchemaType(name = "string")
    protected PadesProfile padesProfile;

    /**
     * Recupera il valore della proprietà sessionId.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSessionId() {
        return sessionId;
    }

    /**
     * Imposta il valore della proprietà sessionId.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSessionId(String value) {
        this.sessionId = value;
    }

    /**
     * Recupera il valore della proprietà flagVisibile.
     * 
     */
    public boolean isFlagVisibile() {
        return flagVisibile;
    }

    /**
     * Imposta il valore della proprietà flagVisibile.
     * 
     */
    public void setFlagVisibile(boolean value) {
        this.flagVisibile = value;
    }

    /**
     * Recupera il valore della proprietà location.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLocation() {
        return location;
    }

    /**
     * Imposta il valore della proprietà location.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLocation(String value) {
        this.location = value;
    }

    /**
     * Recupera il valore della proprietà reason.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReason() {
        return reason;
    }

    /**
     * Imposta il valore della proprietà reason.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReason(String value) {
        this.reason = value;
    }

    /**
     * Recupera il valore della proprietà page.
     * 
     */
    public int getPage() {
        return page;
    }

    /**
     * Imposta il valore della proprietà page.
     * 
     */
    public void setPage(int value) {
        this.page = value;
    }

    /**
     * Recupera il valore della proprietà leftx.
     * 
     */
    public int getLeftx() {
        return leftx;
    }

    /**
     * Imposta il valore della proprietà leftx.
     * 
     */
    public void setLeftx(int value) {
        this.leftx = value;
    }

    /**
     * Recupera il valore della proprietà lefty.
     * 
     */
    public int getLefty() {
        return lefty;
    }

    /**
     * Imposta il valore della proprietà lefty.
     * 
     */
    public void setLefty(int value) {
        this.lefty = value;
    }

    /**
     * Recupera il valore della proprietà rightx.
     * 
     */
    public int getRightx() {
        return rightx;
    }

    /**
     * Imposta il valore della proprietà rightx.
     * 
     */
    public void setRightx(int value) {
        this.rightx = value;
    }

    /**
     * Recupera il valore della proprietà righty.
     * 
     */
    public int getRighty() {
        return righty;
    }

    /**
     * Imposta il valore della proprietà righty.
     * 
     */
    public void setRighty(int value) {
        this.righty = value;
    }

    /**
     * Recupera il valore della proprietà image.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getImage() {
        return image;
    }

    /**
     * Imposta il valore della proprietà image.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setImage(byte[] value) {
        this.image = value;
    }

    /**
     * Recupera il valore della proprietà testo.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTesto() {
        return testo;
    }

    /**
     * Imposta il valore della proprietà testo.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTesto(String value) {
        this.testo = value;
    }

    /**
     * Recupera il valore della proprietà padesProfile.
     * 
     * @return
     *     possible object is
     *     {@link PadesProfile }
     *     
     */
    public PadesProfile getPadesProfile() {
        return padesProfile;
    }

    /**
     * Imposta il valore della proprietà padesProfile.
     * 
     * @param value
     *     allowed object is
     *     {@link PadesProfile }
     *     
     */
    public void setPadesProfile(PadesProfile value) {
        this.padesProfile = value;
    }

}
