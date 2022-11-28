
package it.linksmt.assatti.fdr.client;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per padesProfile.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <p>
 * <pre>
 * &lt;simpleType name="padesProfile"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="BASIC"/&gt;
 *     &lt;enumeration value="PADESBES"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "padesProfile")
@XmlEnum
public enum PadesProfile {

    BASIC,
    PADESBES;

    public String value() {
        return name();
    }

    public static PadesProfile fromValue(String v) {
        return valueOf(v);
    }

}
