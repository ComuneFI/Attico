package it.linksmt.assatti.datalayer.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public enum InseribiliOdgExcelEnum {
	CODICE_ATTO("Codice Atto", 1),
	DATA("Data", 2),
	OGGETTO("Oggetto", 3),
	TIPO_ATTO("Tipo atto", 4),
	TIPO_ITER("Tipo iter", 5),
	SOSPESO("Sospeso", 6),
	MOTIVO_SOSPENSIONE("Motivo Sospensione", 7),
	ASSESSORE_PROPONENTE("Assessore Proponente", 8);
	
	private String descrizione;
	private Integer ordine;

	public String getDescrizione() {
		return descrizione;
	}
	
	InseribiliOdgExcelEnum(String descrizione, Integer ordine){
		this.descrizione = descrizione;
		this.ordine = ordine;
	}
	
	public static List<InseribiliOdgExcelEnum> sortedEnum(){
		List<InseribiliOdgExcelEnum> list = new ArrayList<InseribiliOdgExcelEnum>();
		for(InseribiliOdgExcelEnum en : InseribiliOdgExcelEnum.values()) {
			list.add(en);
		}
		
		Collections.sort(list, new Comparator<InseribiliOdgExcelEnum>() {
            public int compare(InseribiliOdgExcelEnum obj1, InseribiliOdgExcelEnum obj2) {
                    return obj1.ordine.compareTo(obj2.ordine);
            } 
		});
		
		return list;
	}
	
}
