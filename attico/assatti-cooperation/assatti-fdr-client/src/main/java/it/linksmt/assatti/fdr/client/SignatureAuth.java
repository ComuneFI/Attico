
package it.linksmt.assatti.fdr.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per signatureAuth complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="signatureAuth"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="sessionId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="codiceFiscale" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="codiceCertificato" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="password" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="otpPassword" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "signatureAuth", propOrder = {
    "sessionId",
    "codiceFiscale",
    "codiceCertificato",
    "password",
    "otpPassword"
})
public class SignatureAuth {

    protected String sessionId;
    @XmlElement(required = true)
    protected String codiceFiscale;
    protected String codiceCertificato;
    @XmlElement(required = true)
    protected String password;
    @XmlElement(required = true)
    protected String otpPassword;

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
     * Recupera il valore della proprietà codiceFiscale.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodiceFiscale() {
        return codiceFiscale;
    }

    /**
     * Imposta il valore della proprietà codiceFiscale.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodiceFiscale(String value) {
        this.codiceFiscale = value;
    }

    /**
     * Recupera il valore della proprietà codiceCertificato.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodiceCertificato() {
        return codiceCertificato;
    }

    /**
     * Imposta il valore della proprietà codiceCertificato.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodiceCertificato(String value) {
        this.codiceCertificato = value;
    }

    /**
     * Recupera il valore della proprietà password.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPassword() {
        return password;
    }

    /**
     * Imposta il valore della proprietà password.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPassword(String value) {
        this.password = value;
    }

    /**
     * Recupera il valore della proprietà otpPassword.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOtpPassword() {
        return otpPassword;
    }

    /**
     * Imposta il valore della proprietà otpPassword.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOtpPassword(String value) {
        this.otpPassword = value;
    }

}
