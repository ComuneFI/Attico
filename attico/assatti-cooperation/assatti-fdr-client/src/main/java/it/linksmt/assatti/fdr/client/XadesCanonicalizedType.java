
package it.linksmt.assatti.fdr.client;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per xadesCanonicalizedType.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <p>
 * <pre>
 * &lt;simpleType name="xadesCanonicalizedType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="ALGO_ID_C14N11_OMIT_COMMENTS"/&gt;
 *     &lt;enumeration value="ALGO_ID_C14N11_WITH_COMMENTS"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "xadesCanonicalizedType")
@XmlEnum
public enum XadesCanonicalizedType {

    @XmlEnumValue("ALGO_ID_C14N11_OMIT_COMMENTS")
    ALGO_ID_C_14_N_11_OMIT_COMMENTS("ALGO_ID_C14N11_OMIT_COMMENTS"),
    @XmlEnumValue("ALGO_ID_C14N11_WITH_COMMENTS")
    ALGO_ID_C_14_N_11_WITH_COMMENTS("ALGO_ID_C14N11_WITH_COMMENTS");
    private final String value;

    XadesCanonicalizedType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static XadesCanonicalizedType fromValue(String v) {
        for (XadesCanonicalizedType c: XadesCanonicalizedType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
