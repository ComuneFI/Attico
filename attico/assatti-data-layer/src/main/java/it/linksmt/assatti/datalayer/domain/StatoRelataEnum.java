package it.linksmt.assatti.datalayer.domain;

public enum StatoRelataEnum {
	
	DA_GENERARE(0),
	GENERATA(1),
	FIRMATA(2);
	
	private final int value;

	StatoRelataEnum(int v) {
        value = v;
    }

    public int value() {
        return value;
    }

    public static StatoRelataEnum fromValue(int v) {
        for (StatoRelataEnum c: StatoRelataEnum.values()) {
            if (c.value == v) {
                return c;
            }
        }
        throw new IllegalArgumentException(String.valueOf(v));
    }
}
