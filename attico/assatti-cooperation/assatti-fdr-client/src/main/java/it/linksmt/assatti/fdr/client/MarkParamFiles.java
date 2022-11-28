
package it.linksmt.assatti.fdr.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per markParamFiles complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="markParamFiles"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="file" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0"/&gt;
 *         &lt;element name="format" type="{http://service.ws.fdr.linksmt.it/}markFormat" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "markParamFiles", propOrder = {
    "file",
    "format"
})
public class MarkParamFiles {

    protected byte[] file;
    @XmlSchemaType(name = "string")
    protected MarkFormat format;

    /**
     * Recupera il valore della proprietà file.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getFile() {
        return file;
    }

    /**
     * Imposta il valore della proprietà file.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setFile(byte[] value) {
        this.file = value;
    }

    /**
     * Recupera il valore della proprietà format.
     * 
     * @return
     *     possible object is
     *     {@link MarkFormat }
     *     
     */
    public MarkFormat getFormat() {
        return format;
    }

    /**
     * Imposta il valore della proprietà format.
     * 
     * @param value
     *     allowed object is
     *     {@link MarkFormat }
     *     
     */
    public void setFormat(MarkFormat value) {
        this.format = value;
    }

}
