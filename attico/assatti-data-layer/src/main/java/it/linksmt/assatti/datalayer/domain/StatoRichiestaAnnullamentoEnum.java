package it.linksmt.assatti.datalayer.domain;

public enum StatoRichiestaAnnullamentoEnum {
	INIZIALE("Iniziale"),
	PROTOCOLLO("Protocollo"),
	CONCLUSA("Conclusa");
	
	
	private final String value;

	StatoRichiestaAnnullamentoEnum(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static StatoRichiestaAnnullamentoEnum fromValue(String v) {
        for (StatoRichiestaAnnullamentoEnum c: StatoRichiestaAnnullamentoEnum.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
