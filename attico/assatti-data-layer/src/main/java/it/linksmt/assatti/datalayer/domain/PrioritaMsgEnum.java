package it.linksmt.assatti.datalayer.domain;

public enum PrioritaMsgEnum {
	Alta, Media, Bassa;
	
	static public PrioritaMsgEnum getByString(String val){
		PrioritaMsgEnum ris = null;
		for(PrioritaMsgEnum en : PrioritaMsgEnum.values()){
			if(en.toString().equalsIgnoreCase(val)){
				ris = en;
				break;
			}
		}
		return ris;
	}
}
