package it.linksmt.assatti.datalayer.domain;

public enum StatoProceduraPubblicazioneEnum {
	IN_ATTESA("In Attesa"),
	IN_CORSO("In Corso"),
	CONCLUSA("Conclusa");
	
	
	private final String value;

	StatoProceduraPubblicazioneEnum(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static StatoProceduraPubblicazioneEnum fromValue(String v) {
        for (StatoProceduraPubblicazioneEnum c: StatoProceduraPubblicazioneEnum.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
