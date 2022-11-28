package it.linksmt.assatti.datalayer.domain;

public enum StatoPubblicazioneEnum {

	ORDINARIA("Ordinaria"),
	REVOCATA("Revocata"),
	RETTIFICATA("Rettificata"),
	RIPROPOSTA("Riproposta"),
	ANNULLATA("Annullata");
	
	private final String value;

	StatoPubblicazioneEnum(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static StatoPubblicazioneEnum fromValue(String v) {
        for (StatoPubblicazioneEnum c: StatoPubblicazioneEnum.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
