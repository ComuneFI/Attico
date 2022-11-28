
package it.linksmt.assatti.fdr.client;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per markFormat.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <p>
 * <pre>
 * &lt;simpleType name="markFormat"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="TSD"/&gt;
 *     &lt;enumeration value="TSR"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "markFormat")
@XmlEnum
public enum MarkFormat {

    TSD,
    TSR;

    public String value() {
        return name();
    }

    public static MarkFormat fromValue(String v) {
        return valueOf(v);
    }

}
